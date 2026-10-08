package tn.esprit.autoloc.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contrat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrat;

    @Column(nullable = false)
    private LocalDate dateSignature;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(nullable = false)
    private Boolean valide;

    // Relation inverse avec Reservation.
    // mappedBy = "contrat" signifie que Reservation porte la cle etrangere contrat_id.
    @OneToOne(mappedBy = "contrat", fetch = FetchType.LAZY)
    private Reservation reservation;

    // Relation Contrat -> Paiement.
    // fetch EAGER: charger un contrat charge aussi ses paiements, comme demande dans l'atelier.
    // cascade ALL: les paiements suivent le cycle de vie du contrat dans les tests d'atelier.
    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Paiement> paiements = new ArrayList<>();
}
