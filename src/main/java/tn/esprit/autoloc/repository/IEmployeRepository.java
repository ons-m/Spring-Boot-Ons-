package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Employe;

// Depot Spring Data pour manipuler les employes en base.
public interface IEmployeRepository extends JpaRepository<Employe, Long> {
}
