package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MaintenanceDto(Long idMaintenance, @NotNull LocalDate dateDebut,
        @NotNull LocalDate dateFin, @NotBlank @Size(max = 500) String description,
        Long vehiculeId) {}
