package tn.esprit.autoloc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IEmployeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Pas de @Transactional sur les tests : on verifie les vrais commits et rollbacks du service.
@SpringBootTest
@AutoConfigureMockMvc
class AgenceEmployesTests {
    @Autowired IAgenceService agences;
    @Autowired IEmployeService employes;
    @Autowired JdbcTemplate jdbc;
    @Autowired MockMvc mvc;
    @Autowired PlatformTransactionManager transactions;
    Long id;

    @BeforeEach
    void preparer() {
        id = agences.create(agence("Avant")).getIdAgence();
    }

    @AfterEach
    void nettoyer() {
        jdbc.update("delete from employe where agence_id = ?", id);
        agences.deleteById(id);
    }

    private Agence agence(String nom) {
        Agence agence = new Agence();
        agence.setNom(nom);
        agence.setVille("Tunis");
        agence.setAdresse("1 rue de Tunis");
        agence.setTelephone("71000000");
        return agence;
    }

    private Employe employe(String nom) {
        Employe employe = new Employe();
        employe.setNom(nom);
        employe.setPrenom("Test");
        employe.setRole(RoleEmploye.AGENT);
        return employe;
    }

    private int nombreEmployes() {
        return jdbc.queryForObject("select count(*) from employe where agence_id = ?", Integer.class, id);
    }

    private String corps(boolean valide) {
        return """
                {"agence":{"nom":"Apres","ville":"Tunis","adresse":"1 rue de Tunis","telephone":"71000000"},
                 "employes":[{"nom":"Premier","prenom":"Ali","role":"AGENT"},
                             {"nom":"%s","prenom":"Sara","role":"MANAGER"}]}
                """.formatted(valide ? "Deuxieme" : "");
    }

    @Test
    void relationSymetriqueAvecTransfertRetraitEtSansDoublon() {
        Agence a = agence("A");
        Agence b = agence("B");
        Employe e = employe("Test");
        a.ajouterEmploye(e);
        a.ajouterEmploye(e);
        assertSame(a, e.getAgence());
        assertEquals(1, a.getEmployes().size());
        e.setAgence(b);
        assertTrue(a.getEmployes().isEmpty());
        assertTrue(b.getEmployes().contains(e));
        b.retirerEmploye(e);
        assertNull(e.getAgence());
        assertTrue(b.getEmployes().isEmpty());
        a.setEmployes(List.of(e));
        assertSame(a, e.getAgence());
        a.setEmployes(List.of());
        assertNull(e.getAgence());
    }

    @Test
    void apiInsereDeuxEmployesEtCalculeEffectifTotal() throws Exception {
        agences.updateAvecEmployes(id, agence("Avant"), List.of(employe("Existant")));
        mvc.perform(put("/agences/{id}/employes", id).contentType(MediaType.APPLICATION_JSON).content(corps(true)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.effectif").value(3));
        assertEquals(3, nombreEmployes());
        assertEquals("Apres", agences.findById(id).getNom());
        assertEquals(3, agences.findById(id).getEffectif());
        assertEquals(1, jdbc.queryForObject("select count(*) from employe where agence_id = ? and nom = 'Premier'", Integer.class, id));
        mvc.perform(get("/agences/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.effectif").value(3));
    }

    @Test
    void sansTransactionGlobaleLeDeuxiemeEmployeInvalideLaisseDesEcrituresPartielles() {
        // Reproduction pedagogique du probleme, sans exposer une API incorrecte.
        agences.update(id, agence("Apres"));
        TransactionTemplate transaction = new TransactionTemplate(transactions);
        transaction.executeWithoutResult(status -> {
            Agence a = agences.findById(id);
            a.ajouterEmploye(employes.create(employe("Premier")));
        });
        assertThrows(IllegalArgumentException.class, () -> employes.create(employe("")));
        assertEquals("Apres", agences.findById(id).getNom());
        assertEquals(1, nombreEmployes());
        assertEquals(0, agences.findById(id).getEffectif());
    }

    @Test
    void apiInvalideAnnuleToutPuisDonneesValidesReussissent() throws Exception {
        agences.updateAvecEmployes(id, agence("Avant"), List.of(employe("Existant")));
        mvc.perform(put("/agences/{id}/employes", id).contentType(MediaType.APPLICATION_JSON).content(corps(false)))
                .andExpect(status().isBadRequest());
        assertEquals("Avant", agences.findById(id).getNom());
        assertEquals(1, nombreEmployes());
        assertEquals(1, agences.findById(id).getEffectif());
        mvc.perform(put("/agences/{id}/employes", id).contentType(MediaType.APPLICATION_JSON).content(corps(true)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.effectif").value(3));
        assertEquals(3, nombreEmployes());
    }

    @Test
    void listeAbsenteRefuseeEtAgenceInconnueRetourne404() throws Exception {
        mvc.perform(put("/agences/{id}/employes", id).contentType(MediaType.APPLICATION_JSON)
                        .content(corps(true).replace("\"employes\":", "\"autre\":")))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/agences/{id}/employes", Long.MAX_VALUE).contentType(MediaType.APPLICATION_JSON).content(corps(true)))
                .andExpect(status().isNotFound());
        assertEquals("Avant", agences.findById(id).getNom());
        assertEquals(0, nombreEmployes());
    }
}
