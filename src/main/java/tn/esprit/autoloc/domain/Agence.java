package tn.esprit.autoloc.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String ville;

    @Column(length = 255)
    private String adresse;

    @Column(length = 20)
    private String telephone;

    // Relation Agence -> Vehicule.
    // mappedBy = "agence" signifie que la cle etrangere est dans la table vehicule.
    // cascade PERSIST: quand on enregistre une agence, ses vehicules sont enregistres aussi.
    // fetch EAGER: quand on charge une agence, Hibernate charge aussi directement ses vehicules.
    @OneToMany(
            mappedBy = "agence",
            cascade = CascadeType.PERSIST,
            fetch = FetchType.EAGER
    )
    private List<Vehicule> vehicules = new ArrayList<>();

    // Relation Agence -> Employe.
    // Pas de cascade REMOVE: supprimer une agence ne supprime pas automatiquement ses employes.
    // fetch LAZY: charger une agence ne charge pas les employes sauf si on les demande.
    @OneToMany(
            mappedBy = "agence",
            fetch = FetchType.LAZY
    )
    private List<Employe> employes = new ArrayList<>();
}
