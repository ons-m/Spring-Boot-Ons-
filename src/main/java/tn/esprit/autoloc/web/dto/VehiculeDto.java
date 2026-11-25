package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

import java.math.BigDecimal;
import java.util.List;

public record VehiculeDto(
        Long idVehicule,
        @NotBlank(message = "L'immatriculation est obligatoire")
        @Size(max = 20, message = "L'immatriculation ne doit pas depasser 20 caracteres")
        String immatriculation,
        @NotBlank(message = "La marque est obligatoire")
        @Size(max = 50, message = "La marque ne doit pas depasser 50 caracteres")
        String marque,
        @NotBlank(message = "Le modele est obligatoire")
        @Size(max = 50, message = "Le modele ne doit pas depasser 50 caracteres")
        String modele,
        @NotNull(message = "La categorie est obligatoire") CategorieVehicule categorie,
        @NotNull(message = "Le tarif journalier est obligatoire")
        @DecimalMin(value = "0.0", inclusive = false, message = "Le tarif journalier doit etre positif")
        BigDecimal tarifJournalier,
        @NotNull(message = "Le statut est obligatoire") StatutVehicule statut,
        AgenceDto agence,
        List<EquipementDto> equipements
) {}
