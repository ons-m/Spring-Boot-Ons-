package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratDto(Long idContrat, @NotNull LocalDate dateSignature,
        @NotNull BigDecimal montantTotal, @NotNull Boolean valide) {}
