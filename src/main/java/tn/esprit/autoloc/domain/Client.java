package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClient;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le nom est obligatoire") @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres")
    private String nom;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le prenom est obligatoire") @Size(max = 100, message = "Le prenom ne doit pas depasser 100 caracteres")
    private String prenom;

    @Column(nullable = false, unique = true, length = 150)
    @NotBlank(message = "L'email est obligatoire") @Email(message = "L'email est invalide")
    private String email;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Le telephone est obligatoire")
    private String telephone;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Le numero de permis est obligatoire")
    private String numPermis;

    @Column(nullable = false)
    @NotNull(message = "La date d'inscription est obligatoire")
    private LocalDate dateInscription;

    // Relation Client -> Reservation.
    // fetch EAGER: le chargement du client charge aussi ses reservations.
    // cascade REMOVE: la suppression du client supprime ses reservations.
    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.REMOVE,
            fetch = FetchType.EAGER
    )
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();
}
