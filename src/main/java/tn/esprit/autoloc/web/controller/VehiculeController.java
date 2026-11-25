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
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.ErrorResponse;
import tn.esprit.autoloc.web.dto.VehiculeDto;
import tn.esprit.autoloc.web.dto.VehiculeMapper;

import java.util.List;

@RestController
@RequestMapping("/vehicules")
@Tag(name = "Vehicules", description = "Gestion des vehicules")
public class VehiculeController {

    private final IVehiculeService vehiculeService;
    private final VehiculeMapper vehiculeMapper;

    public VehiculeController(IVehiculeService vehiculeService, VehiculeMapper vehiculeMapper) {
        this.vehiculeService = vehiculeService;
        this.vehiculeMapper = vehiculeMapper;
    }

    @Operation(summary = "Lister les vehicules")
    @ApiResponse(responseCode = "200", description = "Liste retournee")
    @GetMapping
    public List<VehiculeDto> findAll() {
        return vehiculeMapper.toDtoList(vehiculeService.findAll());
    }

    @Operation(summary = "Consulter : Vehicule")
    @ApiResponse(responseCode = "200", description = "Ressource trouvee")
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public VehiculeDto findById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        return vehiculeMapper.toDto(vehiculeService.findById(id));
    }

    @Operation(summary = "Creer : Vehicule")
    @ApiResponse(responseCode = "201", description = "Ressource creee")
            @ApiResponse(responseCode = "400", description = "Donnees invalides ou identifiant fourni",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeDto create(@Valid @RequestBody VehiculeDto vehicule) {
        return vehiculeMapper.toDto(vehiculeService.create(vehiculeMapper.toEntity(vehicule)));
    }

    @Operation(summary = "Modifier : Vehicule")
    @ApiResponse(responseCode = "200", description = "Ressource modifiee")
            @ApiResponse(responseCode = "400", description = "Donnees invalides",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public VehiculeDto update(@Parameter(description = "Identifiant") @PathVariable Long id,
            @Valid @RequestBody VehiculeDto vehicule) {
        return vehiculeMapper.toDto(vehiculeService.update(id, vehiculeMapper.toEntity(vehicule)));
    }

    @Operation(summary = "Supprimer : Vehicule")
    @ApiResponse(responseCode = "409", description = "Le vehicule possede encore des equipements")
    @ApiResponse(responseCode = "204", description = "Ressource supprimee, sans corps")
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    @Operation(summary = "Desaffecter l'agence du vehicule")
    @DeleteMapping("/{idVehicule}/agence")
    public VehiculeDto desaffecterAgence(@PathVariable Long idVehicule) {
        return vehiculeMapper.toDto(vehiculeService.desaffecterAgence(idVehicule));
    }

    @Operation(summary = "Affecter une agence au vehicule")
    @PutMapping("/{idVehicule}/agence/{idAgence}")
    public VehiculeDto affecterAgence(@PathVariable Long idVehicule, @PathVariable Long idAgence) {
        return vehiculeMapper.toDto(vehiculeService.affecterAgence(idVehicule, idAgence));
    }

    @Operation(summary = "Affecter un equipement au vehicule")
    @PutMapping("/{idVehicule}/equipements/{idEquipement}")
    public VehiculeDto affecterEquipement(@PathVariable Long idVehicule, @PathVariable Long idEquipement) {
        return vehiculeMapper.toDto(vehiculeService.affecterEquipement(idVehicule, idEquipement));
    }
}
