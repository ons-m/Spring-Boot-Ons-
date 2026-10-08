package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Vehicule;

// Depot Spring Data pour manipuler les vehicules en base.
public interface IVehiculeRepository extends JpaRepository<Vehicule, Long> {
}
