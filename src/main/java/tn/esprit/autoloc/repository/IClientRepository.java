package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Client;

// Depot Spring Data pour manipuler les clients en base.
public interface IClientRepository extends JpaRepository<Client, Long> {
}
