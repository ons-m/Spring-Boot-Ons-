package tn.esprit.autoloc.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;

import java.util.List;

public record AgenceEmployesRequest(
        @NotNull @Valid Agence agence,
        @NotNull List<Employe> employes) {
}
