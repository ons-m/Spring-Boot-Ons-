package tn.esprit.autoloc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "server.servlet.context-path=/api")
@AutoConfigureMockMvc
@Transactional
class AutolocApiApplicationTests {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private EntityManager entityManager;

    static Stream<Arguments> ressources() {
        return Stream.of(
                Arguments.of("agences", "idAgence", "Agence", "{\"nom\":\"Agence Atelier\",\"ville\":\"Tunis\",\"adresse\":\"10 Avenue Habib Bourguiba\",\"telephone\":\"70123456\"}", "{\"nom\":\"Agence Atelier\",\"ville\":\"Sfax\",\"adresse\":\"10 Avenue Habib Bourguiba\",\"telephone\":\"70123456\"}"),
                Arguments.of("vehicules", "idVehicule", "Vehicule", "{\"immatriculation\":\"AT-TEST\",\"marque\":\"Renault\",\"modele\":\"Clio\",\"categorie\":\"CITADINE\",\"tarifJournalier\":75,\"statut\":\"DISPONIBLE\"}", "{\"immatriculation\":\"AT-TEST\",\"marque\":\"Renault\",\"modele\":\"Clio modifiee\",\"categorie\":\"CITADINE\",\"tarifJournalier\":75,\"statut\":\"DISPONIBLE\"}"),
                Arguments.of("clients", "idClient", "Client", "{\"nom\":\"Client Atelier\",\"prenom\":\"Ons\",\"email\":\"atelier.TEST@example.test\",\"telephone\":\"22123456\",\"numPermis\":\"PERMIS-TEST\",\"dateInscription\":\"2026-10-22\"}", "{\"nom\":\"Client Atelier\",\"prenom\":\"Sara\",\"email\":\"atelier.TEST@example.test\",\"telephone\":\"22123456\",\"numPermis\":\"PERMIS-TEST\",\"dateInscription\":\"2026-10-22\"}"),
                Arguments.of("employes", "idEmploye", "Employe", "{\"nom\":\"Employe Atelier\",\"prenom\":\"Ali\",\"role\":\"AGENT\"}", "{\"nom\":\"Employe Atelier\",\"prenom\":\"Sami\",\"role\":\"AGENT\"}"),
                Arguments.of("reservations", "idReservation", "Reservation", "{\"dateDebut\":\"2026-11-01\",\"dateFin\":\"2026-11-03\",\"statut\":\"CONFIRMEE\"}", "{\"dateDebut\":\"2026-11-01\",\"dateFin\":\"2026-11-03\",\"statut\":\"ANNULEE\"}"),
                Arguments.of("contrats", "idContrat", "Contrat", "{\"dateSignature\":\"2026-10-22\",\"montantTotal\":480,\"valide\":true}", "{\"dateSignature\":\"2026-10-22\",\"montantTotal\":550,\"valide\":true}"),
                Arguments.of("maintenances", "idMaintenance", "Maintenance", "{\"dateDebut\":\"2026-11-01\",\"dateFin\":\"2026-11-03\",\"description\":\"Revision Atelier\"}", "{\"dateDebut\":\"2026-11-01\",\"dateFin\":\"2026-11-03\",\"description\":\"Revision terminee\"}"),
                Arguments.of("equipements", "idEquipement", "Equipement", "{\"libelle\":\"GPS Atelier TEST\"}", "{\"libelle\":\"GPS modifie TEST\"}")
        );
    }

    @ParameterizedTest(name = "CRUD {0}")
    @MethodSource("ressources")
    void crudComplet(String path, String idField, String resource, String initial, String updated) throws Exception {
        ObjectNode body = (ObjectNode) mapper.readTree(initial);
        JsonNode created = call("POST", path, body, 201);
        long id = created.path(idField).asLong();
        assertThat(id).isPositive();
        assertThat(call("GET", path, null, 200).isArray()).isTrue();
        assertThat(call("GET", path + "/" + id, null, 200).path(idField).asLong()).isEqualTo(id);

        body.put(idField, id);
        assertError("POST", path, body, 400, null);
        body.remove(idField);
        assertError("POST", path, mapper.createObjectNode(), 400, null);
        assertError("PUT", path + "/" + id, mapper.createObjectNode(), 400, null);

        ObjectNode changes = (ObjectNode) mapper.readTree(updated);
        JsonNode modified = call("PUT", path + "/" + id, changes, 200);
        changes.properties().forEach(field -> assertThat(modified.path(field.getKey()))
                .as(field.getKey()).isEqualTo(field.getValue()));
        assertThat(call("GET", path + "/" + id, null, 200).path(idField).asLong()).isEqualTo(id);
        changes.put(idField, id + 9999);
        assertError("PUT", path + "/" + id, changes, 400, null);
        changes.remove(idField);

        String absent = resource + " introuvable avec l'identifiant -1";
        assertError("GET", path + "/-1", null, 404, absent);
        assertError("PUT", path + "/-1", changes, 404, absent);
        assertError("DELETE", path + "/-1", null, 404, absent);
        call("DELETE", path + "/" + id, null, 204);
        assertError("GET", path + "/" + id, null, 404,
                resource + " introuvable avec l'identifiant " + id);
    }

    @Test
    void relationsSansCyclesEtSuppressionsRespectentLesAssociations() throws Exception {
        JsonNode agence = call("POST", "agences", json("""
                {"nom":"Agence Relations","ville":"Tunis","adresse":"Centre","telephone":"70123456"}
                """), 201);
        long agenceId = agence.path("idAgence").asLong();
        long equipementId = call("POST", "equipements", json("""
                {"libelle":"GPS relations"}
                """), 201).path("idEquipement").asLong();
        ObjectNode voiture = (ObjectNode) json("""
                {"immatriculation":"TEST-RELATIONS","marque":"Renault","modele":"Clio",
                 "categorie":"CITADINE","tarifJournalier":75,"statut":"DISPONIBLE"}
                """);
        voiture.putObject("agence").put("idAgence", agenceId);
        voiture.putArray("equipements").addObject().put("idEquipement", equipementId);
        long vehiculeId = call("POST", "vehicules", voiture, 201).path("idVehicule").asLong();
        ObjectNode employe = (ObjectNode) json("""
                {"nom":"Agent","prenom":"Ali","role":"AGENT"}
                """);
        employe.putObject("agence").put("idAgence", agenceId);
        long employeId = call("POST", "employes", employe, 201).path("idEmploye").asLong();
        ObjectNode maintenance = (ObjectNode) json("""
                {"dateDebut":"2026-11-01","dateFin":"2026-11-03","description":"Revision"}
                """);
        maintenance.putObject("vehicule").put("idVehicule", vehiculeId);
        long maintenanceId = call("POST", "maintenances", maintenance, 201).path("idMaintenance").asLong();
        long contratId = call("POST", "contrats", json("""
                {"dateSignature":"2026-10-22","montantTotal":480,"valide":true,
                 "paiements":[{"montant":100,"datePaiement":"2026-10-22","modePaiement":"CARTE"}]}
                """), 201).path("idContrat").asLong();
        ObjectNode reservation = (ObjectNode) json("""
                {"dateDebut":"2026-11-01","dateFin":"2026-11-03","statut":"CONFIRMEE"}
                """);
        reservation.putObject("vehicule").put("idVehicule", vehiculeId);
        reservation.putObject("contrat").put("idContrat", contratId);
        long reservationId = call("POST", "reservations", reservation, 201).path("idReservation").asLong();

        JsonNode loaded = call("GET", "agences/" + agenceId, null, 200);
        assertThat(loaded.path("vehicules").size()).isEqualTo(1);
        assertThat(loaded.path("vehicules").get(0).has("agence")).isFalse();
        ((ObjectNode) agence).remove("vehicules");
        call("PUT", "agences/" + agenceId, agence, 200);
        assertThat(call("GET", "agences/" + agenceId, null, 200).path("vehicules").size()).isEqualTo(1);

        call("DELETE", "agences/" + agenceId, null, 204);
        assertThat(call("GET", "vehicules/" + vehiculeId, null, 200).path("agence").isNull()).isTrue();
        assertThat(call("GET", "employes/" + employeId, null, 200).path("agence").isNull()).isTrue();
        call("DELETE", "equipements/" + equipementId, null, 204);
        assertThat(call("GET", "vehicules/" + vehiculeId, null, 200).path("equipements").isEmpty()).isTrue();
        call("DELETE", "contrats/" + contratId, null, 204);
        assertThat(call("GET", "reservations/" + reservationId, null, 200).path("contrat").isNull()).isTrue();
        call("DELETE", "vehicules/" + vehiculeId, null, 204);
        assertThat(call("GET", "maintenances/" + maintenanceId, null, 200).path("vehicule").isNull()).isTrue();
        assertError("GET", "reservations/" + reservationId, null, 404, null);
    }

    @Test
    void paiementsConsultablesUniquementEtCompositionDuContrat() throws Exception {
        JsonNode contrat = call("POST", "contrats", json("""
                {"dateSignature":"2026-10-22","montantTotal":480,"valide":true,
                 "paiements":[{"montant":100,"datePaiement":"2026-10-22","modePaiement":"CARTE"}]}
                """), 201);
        JsonNode paiement = contrat.path("paiements").get(0);
        assertThat(paiement.has("contrat")).isFalse();
        long id = paiement.path("idPaiement").asLong();
        assertThat(id).isPositive();
        assertThat(call("GET", "paiements", null, 200).size()).isEqualTo(1);
        assertThat(call("GET", "paiements/" + id, null, 200).path("montant").asInt()).isEqualTo(100);
        assertError("POST", "paiements", paiement, 405, null);
        assertError("PUT", "paiements/" + id, paiement, 405, null);
        assertError("DELETE", "paiements/" + id, null, 405, null);
        call("DELETE", "contrats/" + contrat.path("idContrat").asLong(), null, 204);
        assertError("GET", "paiements/" + id, null, 404, null);
    }

    @Test
    void swaggerExposeLesNeufRessourcesEtLesAnnotations() throws Exception {
        JsonNode doc = call("GET", "v3/api-docs", null, 200);
        assertThat(doc.path("openapi").asText()).isEqualTo("3.1.1");
        assertThat(doc.path("info").path("title").asText()).isEqualTo("AutoLoc API");
        for (String path : new String[]{"agences", "vehicules", "clients", "employes",
                "reservations", "contrats", "maintenances", "equipements", "paiements"}) {
            assertThat(doc.path("paths").has("/" + path)).as(path).isTrue();
            assertThat(doc.path("paths").has("/" + path + "/{id}")).as(path + " id").isTrue();
        }
        assertThat(doc.path("paths").path("/paiements").has("post")).isFalse();
        assertThat(doc.path("paths").path("/agences/{id}").path("get").path("responses").has("404")).isTrue();
        assertThat(doc.path("paths").path("/agences/{id}").path("get").path("responses")
                .path("404").path("content").path("application/json").path("schema").path("$ref").asText())
                .isEqualTo("#/components/schemas/ErrorResponse");
        mvc.perform(request(HttpMethod.GET, "/api/swagger-ui/index.html").contextPath("/api"))
                .andExpect(status().isOk());
    }

    private JsonNode json(String body) throws Exception {
        return mapper.readTree(body);
    }

    private JsonNode call(String method, String path, JsonNode body, int expected) throws Exception {
        var builder = request(HttpMethod.valueOf(method), "/api/" + path).contextPath("/api");
        if (body != null) {
            builder.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(body));
        }
        var response = mvc.perform(builder).andExpect(status().is(expected)).andReturn().getResponse();
        entityManager.flush();
        entityManager.clear();
        String content = response.getContentAsString();
        if (expected == 204) {
            assertThat(content).isEmpty();
            return mapper.nullNode();
        }
        return mapper.readTree(content);
    }

    private void assertError(String method, String path, JsonNode body, int expected, String message) throws Exception {
        JsonNode error = call(method, path, body, expected);
        assertThat(error.path("status").asInt()).isEqualTo(expected);
        assertThat(error.path("path").asText()).isEqualTo("/api/" + path);
        assertThat(error.has("timestamp")).isTrue();
        assertThat(error.path("error").asText()).isNotBlank();
        assertThat(error.path("message").asText()).isNotBlank();
        if (message != null) {
            assertThat(error.path("message").asText()).isEqualTo(message);
        }
    }
}
