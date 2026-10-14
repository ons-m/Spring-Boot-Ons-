package tn.esprit.autoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.service.IAgenceService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AgenceTests {

    @Autowired
    private AgenceRepositoryMock basicAgenceRepository;

    @Autowired
    private IAgenceRepository fullAgenceRepository;

    @Autowired
    private IAgenceService agenceService;

    @Test
    void basicAddAgence() {
        // Test avec CrudRepository: on verifie les operations CRUD simples.
        addAgence(basicAgenceRepository);
    }

    @Test
    void fullAddAgence() {
        // Test avec JpaRepository: meme sauvegarde, mais avec un depot plus riche.
        addAgence(fullAgenceRepository);
    }

    @Test
    void createAgence() {
        Agence agence = new Agence();
        agence.setNom("Agence service");
        agence.setVille("Tunis");
        agence.setAdresse("10 Avenue Habib Bourguiba");
        agence.setTelephone("70000000");

        Agence createdAgence = agenceService.create(agence);

        assertNotNull(createdAgence.getIdAgence());
        assertTrue(agenceService.findAll().stream()
                .anyMatch(item -> item.getIdAgence().equals(createdAgence.getIdAgence())));

        System.out.println("Agence creee par le service : "
                + createdAgence.getIdAgence()
                + " | "
                + createdAgence.getNom());
    }

    private void addAgence(CrudRepository<Agence, Long> repository) {
        // L'atelier demande d'ajouter un suffixe pour eviter les doublons d'immatriculation.
        // On garde les plaques courtes car la colonne immatriculation a une taille de 20.
        int suffix = (int) (System.currentTimeMillis() % 1_000_000);

        Agence agence = new Agence();
        agence.setNom("Agence ariana");
        agence.setVille("Tunis");
        agence.setAdresse("1 Rue Hedi");
        agence.setTelephone("71585874");

        Vehicule vehicule1 = new Vehicule();
        vehicule1.setCategorie(CategorieVehicule.SUV);
        vehicule1.setImmatriculation("785414TU" + suffix);
        vehicule1.setMarque("Isuzu");
        vehicule1.setModele("DMax");
        vehicule1.setStatut(StatutVehicule.MAINTENANCE);
        vehicule1.setTarifJournalier(new BigDecimal("100"));
        vehicule1.setAgence(agence);

        Vehicule vehicule2 = new Vehicule();
        vehicule2.setCategorie(CategorieVehicule.UTILITAIRE);
        vehicule2.setImmatriculation("785415TU" + suffix);
        vehicule2.setMarque("Toyota");
        vehicule2.setModele("Yaris");
        vehicule2.setStatut(StatutVehicule.DISPONIBLE);
        vehicule2.setTarifJournalier(new BigDecimal("80"));
        vehicule2.setAgence(agence);

        // On remplit aussi le cote inverse pour que l'objet Java soit coherent en memoire.
        agence.getVehicules().add(vehicule1);
        agence.getVehicules().add(vehicule2);

        // Grace au cascade PERSIST dans Agence, save(agence) persiste aussi les vehicules.
        repository.save(agence);
    }

    @Test
    void basicLoadAgence() {
        // Chargement avec CrudRepository.
        loadAgence(basicAgenceRepository, "CrudRepository");
    }

    @Test
    void fullLoadAgence() {
        // Chargement avec JpaRepository.
        loadAgence(fullAgenceRepository, "JpaRepository");
    }

    private void loadAgence(CrudRepository<Agence, Long> repository, String repositoryType) {
        StringBuilder builder = new StringBuilder();
        builder.append("Depot utilise : ").append(repositoryType).append(System.lineSeparator());

        repository.findAll().forEach(agence -> {
            builder.append(agence.getIdAgence())
                    .append(" | ")
                    .append(agence.getNom())
                    .append(System.lineSeparator());

            builder.append("Vehicules Count : ")
                    .append(agence.getVehicules().size())
                    .append(System.lineSeparator());

            agence.getVehicules().forEach(vehicule -> builder.append("=== ")
                    .append(vehicule.getIdVehicule())
                    .append(" | ")
                    .append(vehicule.getImmatriculation())
                    .append(System.lineSeparator()));
        });

        // Dans le PDF, on utilise une assertion qui echoue pour afficher le resultat.
        // Ici on affiche dans la console pour garder les tests executables sans echec volontaire.
        System.out.println(builder);
    }

    @Test
    void loadSortedAgences() {
        // JpaRepository permet de trier directement avec Sort.
        Iterable<Agence> agences = fullAgenceRepository.findAll(
                Sort.by(Sort.Direction.DESC, "idAgence")
        );

        StringBuilder builder = new StringBuilder();
        builder.append("Agences triees par id decroissant").append(System.lineSeparator());

        agences.forEach(agence -> builder.append(agence.getIdAgence())
                .append(" | ")
                .append(agence.getNom())
                .append(System.lineSeparator()));

        System.out.println(builder);
    }

    @Test
    void loadPagedAgences() {
        // L'atelier demande de garder le code duplique avec loadSortedAgences.
        // PageRequest.of(0, 2, ...) signifie: premiere page, 2 elements par page, tri descendant.
        Page<Agence> page = fullAgenceRepository.findAll(
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "idAgence"))
        );

        StringBuilder builder = new StringBuilder();
        builder.append("Agences triees par id decroissant avec pagination").append(System.lineSeparator());
        builder.append("Nombre total de pages : ").append(page.getTotalPages()).append(System.lineSeparator());
        builder.append("Page en cours : ").append(page.getNumber()).append(System.lineSeparator());

        page.getContent().forEach(agence -> builder.append(agence.getIdAgence())
                .append(" | ")
                .append(agence.getNom())
                .append(System.lineSeparator()));

        System.out.println(builder);
    }
}

// Depot minimal demande dans Chap3.
// CrudRepository contient save, findAll, findById, delete, etc.
@Repository
interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
}
