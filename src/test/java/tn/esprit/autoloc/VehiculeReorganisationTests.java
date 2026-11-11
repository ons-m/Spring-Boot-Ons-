package tn.esprit.autoloc;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.service.IVehiculeService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VehiculeReorganisationTests {
    @Autowired EntityManager em;
    @Autowired IVehiculeService vehiculeService;
    @Autowired MockMvc mvc;

    private Long vehiculeId;
    private Long agenceId;
    private Long equipementId;

    @BeforeEach
    void preparer() {
        Agence agence = new Agence();
        agence.setNom("Agence test");
        agence.setVille("Tunis");
        agence.setAdresse("1 rue de Tunis");
        agence.setTelephone("71000000");
        em.persist(agence);
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("TEST-REORG");
        vehicule.setMarque("Toyota");
        vehicule.setModele("Yaris");
        vehicule.setCategorie(CategorieVehicule.UTILITAIRE);
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule.setTarifJournalier(new BigDecimal("80"));
        vehicule.setAgence(agence);
        em.persist(vehicule);
        Equipement equipement = new Equipement();
        equipement.setLibelle("GPS test");
        em.persist(equipement);
        vehiculeId = vehicule.getIdVehicule();
        agenceId = agence.getIdAgence();
        equipementId = equipement.getIdEquipement();
        recharger();
    }

    private void recharger() {
        em.flush();
        em.clear();
    }

    @Test
    void desaffecterConserveLeVehiculeSansParent() {
        vehiculeService.desaffecterAgence(vehiculeId);
        recharger();
        Vehicule vehicule = vehiculeService.findById(vehiculeId);
        assertEquals(vehiculeId, vehicule.getIdVehicule());
        assertNull(vehicule.getAgence());
        assertNotNull(em.find(Agence.class, agenceId));
    }

    @Test
    void apiDesaffectationEtAffectationAgence() throws Exception {
        mvc.perform(delete("/vehicules/{id}/agence", vehiculeId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.agence").isEmpty());
        recharger();
        assertNull(vehiculeService.findById(vehiculeId).getAgence());
        mvc.perform(put("/vehicules/{v}/agence/{a}", vehiculeId, agenceId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.agence.idAgence").value(agenceId));
        recharger();
        assertEquals(agenceId, vehiculeService.findById(vehiculeId).getAgence().getIdAgence());
    }

    @Test
    void apiAffectationEquipementSansDoublon() throws Exception {
        for (int i = 0; i < 2; i++) {
            mvc.perform(put("/vehicules/{v}/equipements/{e}", vehiculeId, equipementId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.equipements[0].idEquipement").value(equipementId));
            recharger();
        }
        var equipements = vehiculeService.findById(vehiculeId).getEquipements();
        assertEquals(1, equipements.size());
        assertEquals(equipementId, equipements.get(0).getIdEquipement());
    }

    @Test
    void suppressionVehiculeEquipeLeveUneException() {
        vehiculeService.affecterEquipement(vehiculeId, equipementId);
        recharger();
        assertThrows(DataIntegrityViolationException.class, () -> vehiculeService.deleteById(vehiculeId));
        assertNotNull(em.find(Vehicule.class, vehiculeId));
    }

    @Test
    void apiSuppressionVehiculeEquipeRetourne409() throws Exception {
        vehiculeService.affecterEquipement(vehiculeId, equipementId);
        recharger();
        mvc.perform(delete("/vehicules/{id}", vehiculeId)).andExpect(status().isConflict());
        assertNotNull(em.find(Vehicule.class, vehiculeId));
    }

    @Test
    void suppressionEquipementConserveLeVehiculeEtPermetSaSuppression() throws Exception {
        vehiculeService.affecterEquipement(vehiculeId, equipementId);
        recharger();
        mvc.perform(delete("/equipements/{id}", equipementId)).andExpect(status().isNoContent());
        recharger();
        assertNull(em.find(Equipement.class, equipementId));
        assertTrue(vehiculeService.findById(vehiculeId).getEquipements().isEmpty());
        mvc.perform(delete("/vehicules/{id}", vehiculeId)).andExpect(status().isNoContent());
        recharger();
        assertNull(em.find(Vehicule.class, vehiculeId));
    }

    @Test
    void identifiantsInexistantsRetourne404() throws Exception {
        mvc.perform(delete("/vehicules/{id}/agence", Long.MAX_VALUE)).andExpect(status().isNotFound());
        mvc.perform(put("/vehicules/{v}/agence/{a}", vehiculeId, Long.MAX_VALUE))
                .andExpect(status().isNotFound());
        mvc.perform(put("/vehicules/{v}/equipements/{e}", vehiculeId, Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }
}
