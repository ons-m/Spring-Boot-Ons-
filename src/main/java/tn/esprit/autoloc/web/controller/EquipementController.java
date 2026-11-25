package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.service.IEquipementService;
import tn.esprit.autoloc.web.ErrorResponse;
import tn.esprit.autoloc.web.dto.EquipementDto;
import tn.esprit.autoloc.web.dto.EquipementMapper;

import java.util.List;

@RestController
@RequestMapping("/equipements")
@Tag(name = "Equipements", description = "Gestion des equipements")
public class EquipementController {

    private final IEquipementService equipementService;
    private final EquipementMapper equipementMapper;

    public EquipementController(IEquipementService equipementService, EquipementMapper equipementMapper) {
        this.equipementService = equipementService;
        this.equipementMapper = equipementMapper;
    }

    @Operation(summary = "Lister les equipements")
    @ApiResponse(responseCode = "200", description = "Liste retournee")
    @GetMapping
    public List<EquipementDto> findAll() {
        return equipementMapper.toDtoList(equipementService.findAll());
    }

    @Operation(summary = "Consulter : Equipement")
    @ApiResponse(responseCode = "200", description = "Ressource trouvee")
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public EquipementDto findById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        return equipementMapper.toDto(equipementService.findById(id));
    }

    @Operation(summary = "Creer : Equipement")
    @ApiResponse(responseCode = "201", description = "Ressource creee")
            @ApiResponse(responseCode = "400", description = "Donnees invalides ou identifiant fourni",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipementDto create(@Valid @RequestBody EquipementDto equipement) {
        return equipementMapper.toDto(equipementService.create(equipementMapper.toEntity(equipement)));
    }

    @Operation(summary = "Modifier : Equipement")
    @ApiResponse(responseCode = "200", description = "Ressource modifiee")
            @ApiResponse(responseCode = "400", description = "Donnees invalides",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public EquipementDto update(@Parameter(description = "Identifiant") @PathVariable Long id,
            @Valid @RequestBody EquipementDto equipement) {
        return equipementMapper.toDto(equipementService.update(id, equipementMapper.toEntity(equipement)));
    }

    @Operation(summary = "Supprimer : Equipement")
    @ApiResponse(responseCode = "204", description = "Ressource supprimee, sans corps")
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        equipementService.deleteById(id);
    }
}
