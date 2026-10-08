package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Maintenance;

// Depot Spring Data pour manipuler les maintenances en base.
public interface IMaintenanceRepository extends JpaRepository<Maintenance, Long> {
}
