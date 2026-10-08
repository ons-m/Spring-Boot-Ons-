package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Paiement;

// Depot Spring Data pour manipuler les paiements en base.
public interface IPaiementRepository extends JpaRepository<Paiement, Long> {
}
