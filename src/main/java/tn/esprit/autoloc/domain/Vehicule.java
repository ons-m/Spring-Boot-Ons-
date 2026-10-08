package tn.esprit.autoloc.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    @Column(nullable = false, unique = true, length = 20)
    private String immatriculation;

    @Column(nullable = false, length = 50)
    private String marque;

    @Column(nullable = false, length = 50)
    private String modele;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategorieVehicule categorie;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifJournalier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutVehicule statut;

    // Relation Vehicule -> Agence.
    // Plusieurs vehicules peuvent appartenir a une agence.
    // optional = true garde la relation facultative: un vehicule peut exister sans agence.
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "agence_id")
    private Agence agence;

    // Relation Vehicule -> Reservation.
    // fetch LAZY: charger un vehicule ne charge pas automatiquement ses reservations.
    // cascade REMOVE: supprimer un vehicule supprime aussi ses reservations.
    @OneToMany(
            mappedBy = "vehicule",
            cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY
    )
    private List<Reservation> reservations = new ArrayList<>();

    // Relation Vehicule -> Maintenance.
    // La cle etrangere se trouve dans la table maintenance.
    @OneToMany(
            mappedBy = "vehicule",
            fetch = FetchType.LAZY
    )
    private List<Maintenance> maintenances = new ArrayList<>();

    // Relation Vehicule <-> Equipement.
    // Plusieurs vehicules peuvent avoir plusieurs equipements.
    // Pas de cascade: les cycles de vie des vehicules et equipements restent separes.
    // Hibernate cree une table de jointure vehicule_equipement.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "vehicule_equipement",
            joinColumns = @JoinColumn(name = "vehicule_id"),
            inverseJoinColumns = @JoinColumn(name = "equipement_id")
    )
    private List<Equipement> equipements = new ArrayList<>();
}
