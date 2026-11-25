package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationDto(Long idReservation, @NotNull LocalDate dateDebut,
        @NotNull LocalDate dateFin, @NotBlank String statut, Long clientId,
        Long vehiculeId, Long contratId) {}
