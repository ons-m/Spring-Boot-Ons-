package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres")
    private String nom;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100, message = "La ville ne doit pas depasser 100 caracteres")
    private String ville;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 255, message = "L'adresse ne doit pas depasser 255 caracteres")
    private String adresse;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Le telephone est obligatoire")
    @Size(max = 20, message = "Le telephone ne doit pas depasser 20 caracteres")
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
    @JsonIgnoreProperties({"agence", "reservations", "maintenances", "equipements"})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Vehicule> vehicules = new ArrayList<>();

    // Relation Agence -> Employe.
    // Pas de cascade REMOVE: supprimer une agence ne supprime pas automatiquement ses employes.
    // fetch LAZY: charger une agence ne charge pas les employes sauf si on les demande.
    @OneToMany(
            mappedBy = "agence",
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Employe> employes = new ArrayList<>();
}
