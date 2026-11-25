package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Données exposées pour créer ou modifier une agence. */
public record AgenceDto(
        Long idAgence,
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres")
        String nom,
        @NotBlank(message = "La ville est obligatoire")
        @Size(max = 100, message = "La ville ne doit pas depasser 100 caracteres")
        String ville,
        @NotBlank(message = "L'adresse est obligatoire")
        @Size(max = 255, message = "L'adresse ne doit pas depasser 255 caracteres")
        String adresse,
        @NotBlank(message = "Le telephone est obligatoire")
        @Size(max = 20, message = "Le telephone ne doit pas depasser 20 caracteres")
        String telephone,
        int effectif
) {}
