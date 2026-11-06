package tn.esprit.autoloc;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.service.ISimulationAgenceService;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SimulationTests {
    @Autowired private IVehiculeService vehiculeService;
    @Autowired private IAgenceService agenceService;
    @Autowired private ISimulationAgenceService simulationAgenceService;
    @Autowired private EntityManager entityManager;
    @Autowired private MockMvc mvc;

    @ParameterizedTest
    @CsvSource({
            "100, 10, STANDARD, 1000", "100, 10, WEEKEND, 800",
            "100, 10, LONGUE_DUREE, 750", "100, 40, LONGUE_DUREE, 2850",
            "100, 20, LONGUE_DUREE, 1500",
            "80, 33, LONGUE_DUREE, 1980", "80, 34, LONGUE_DUREE, 1938",
            "0, 10, STANDARD, 0", "8, 1, WEEKEND, 6.4",
            // Juste en dessous et juste au-dessus de 2000 apres reduction.
            "2666.66, 1, LONGUE_DUREE, 1999.995",
            "2666.67, 1, LONGUE_DUREE, 1900.002375"
    })
    void appliqueLesOffres(String tarif, int jours, VehiculeOffre offre, double attendu) {
        Vehicule vehicule = new Vehicule();
        vehicule.setTarifJournalier(new BigDecimal(tarif));
        // Les calculs en double peuvent produire un tres petit ecart d'arrondi.
        assertThat(vehiculeService.calculTarif(vehicule, jours, offre)).isCloseTo(attendu, offset(0.000001));
    }

    @Test
    void rapportEtTroisOffresViaHttp() throws Exception {
        Agence agence = agenceService.create(agence());
        for (int i = 1; i <= 2; i++) {
            Vehicule vehicule = new Vehicule();
            vehicule.setImmatriculation("SIMULATION-" + i);
            vehicule.setMarque("Renault");
            vehicule.setModele("Clio");
            vehicule.setCategorie(CategorieVehicule.CITADINE);
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
            vehicule.setTarifJournalier(new BigDecimal("100"));
            vehicule.setAgence(agence);
            vehiculeService.create(vehicule);
        }
        entityManager.flush();
        entityManager.clear();
        // Deux vehicules : la remise additionnelle se calcule par vehicule.
        for (String[] cas : new String[][]{{"STANDARD", "8000.0"},
                {"WEEKEND", "6400.0"}, {"LONGUE_DUREE", "5700.0"}}) {
            mvc.perform(get("/agences/{id}/simulation", agence.getIdAgence())
                            .param("jours", "40").param("offre", cas[0]))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith("text/plain"))
                    .andExpect(content().string("Id : " + agence.getIdAgence()
                            + "; Nom : Agence Simulation; Tel : 70123456\n"
                            + "Total : " + cas[1] + "\n"));
        }
    }

    @Test
    void agenceVideEtErreursHttp() throws Exception {
        Long id = agenceService.create(agence()).getIdAgence();
        assertThat(simulationAgenceService.simuler(agenceService.findById(id), 3, VehiculeOffre.STANDARD))
                .contains("Total : 0.0\n");
        mvc.perform(get("/agences/{id}/simulation", id).param("jours", "0").param("offre", "STANDARD"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/agences/{id}/simulation", id).param("jours", "2").param("offre", "INCONNUE"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/agences/-1/simulation").param("jours", "2").param("offre", "STANDARD"))
                .andExpect(status().isNotFound());
    }

    private Agence agence() {
        Agence agence = new Agence();
        agence.setNom("Agence Simulation");
        agence.setVille("Tunis");
        agence.setAdresse("Centre");
        agence.setTelephone("70123456");
        return agence;
    }
}
