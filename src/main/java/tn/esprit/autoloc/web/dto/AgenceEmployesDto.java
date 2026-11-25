package tn.esprit.autoloc.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AgenceEmployesDto(@NotNull @Valid AgenceDto agence,
        @NotNull List<@Valid EmployeDto> employes) {}
