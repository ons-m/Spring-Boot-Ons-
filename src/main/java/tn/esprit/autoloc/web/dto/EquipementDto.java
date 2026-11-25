package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;

public record EquipementDto(Long idEquipement, @NotBlank String libelle) {}
