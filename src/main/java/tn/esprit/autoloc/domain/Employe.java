package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "employe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmploye;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le nom est obligatoire") @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres")
    private String nom;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le prenom est obligatoire") @Size(max = 100, message = "Le prenom ne doit pas depasser 100 caracteres")
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Le role est obligatoire")
    private RoleEmploye role;

    // Plusieurs employes peuvent travailler dans la meme agence.
    // La suppression d'une agence ne supprime pas les employes automatiquement.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id")
    private Agence agence;

    // Un seul point de synchronisation pour les deux sens de la relation.
    public void setAgence(Agence nouvelleAgence) {
        if (agence != null) {
            agence.getEmployes().remove(this);
        }
        agence = nouvelleAgence;
        if (nouvelleAgence != null && !nouvelleAgence.getEmployes().contains(this)) {
            nouvelleAgence.getEmployes().add(this);
        }
    }
}
