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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
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
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String telephone;

    @Column(nullable = false, unique = true, length = 50)
    private String numPermis;

    @Column(nullable = false)
    private LocalDate dateInscription;

    // Relation Client -> Reservation.
    // fetch EAGER: le chargement du client charge aussi ses reservations.
    // cascade REMOVE: la suppression du client supprime ses reservations.
    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.REMOVE,
            fetch = FetchType.EAGER
    )
    private List<Reservation> reservations = new ArrayList<>();
}
