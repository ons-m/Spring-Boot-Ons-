package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Equipement;

// Depot Spring Data pour manipuler les equipements en base.
public interface IEquipementRepository extends JpaRepository<Equipement, Long> {
}
