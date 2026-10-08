package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Agence;

// JpaRepository fournit CrudRepository + tri + pagination.
// Le nom commence par I uniquement parce que l'atelier impose cette convention.
public interface IAgenceRepository extends JpaRepository<Agence, Long> {
}
