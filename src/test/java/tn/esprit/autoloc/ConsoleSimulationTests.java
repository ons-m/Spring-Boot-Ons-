package tn.esprit.autoloc;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IVehiculeService;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Verifie le mode CONSOLE de bout en bout : Factory, Decorator et strategies Spring. */
@SpringBootTest(properties = "autoloc.rapport.mode=CONSOLE")
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
@Transactional
class ConsoleSimulationTests {
    @Autowired private IAgenceService agenceService;
    @Autowired private IVehiculeService vehiculeService;
    @Autowired private MockMvc mvc;

    @ParameterizedTest
    @CsvSource({"STANDARD, 4000.0", "WEEKEND, 3200.0", "LONGUE_DUREE, 2850.0"})
    void enteteDansLaConsoleEtTotalDansLaReponse(String offre, String total, CapturedOutput output)
            throws Exception {
        Agence agence = new Agence();
        agence.setNom("Agence Console");
        agence.setVille("Tunis");
        agence.setAdresse("Centre");
        agence.setTelephone("70123456");
        agence = agenceService.create(agence);

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("CONSOLE-TEST");
        vehicule.setMarque("Renault");
        vehicule.setModele("Clio");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule.setTarifJournalier(new BigDecimal("100"));
        vehicule.setAgence(agence);
        vehiculeService.create(vehicule);
        // Synchroniser le cote inverse pour le rapport dans cette meme transaction.
        agence.getVehicules().add(vehicule);

        mvc.perform(get("/agences/{id}/simulation", agence.getIdAgence())
                        .param("jours", "40").param("offre", offre))
                .andExpect(status().isOk())
                .andExpect(content().string("Total : " + total + "\n"));

        assertThat(output.getOut()).contains("Id : " + agence.getIdAgence()
                + "; Nom : Agence Console; Tel : 70123456");
    }
}
