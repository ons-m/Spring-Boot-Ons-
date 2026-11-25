package tn.esprit.autoloc.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaiementDto(Long idPaiement, BigDecimal montant, LocalDate datePaiement,
        String modePaiement, Long contratId) {}
