package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Reservation;

// Depot Spring Data pour manipuler les reservations en base.
public interface IReservationRepository extends JpaRepository<Reservation, Long> {
}
