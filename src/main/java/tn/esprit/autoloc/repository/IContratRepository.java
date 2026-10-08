package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Contrat;

// Depot Spring Data pour manipuler les contrats en base.
public interface IContratRepository extends JpaRepository<Contrat, Long> {
}
