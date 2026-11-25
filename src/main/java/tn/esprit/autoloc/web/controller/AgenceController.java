package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.ISimulationAgenceService;
import tn.esprit.autoloc.web.ErrorResponse;
import tn.esprit.autoloc.web.dto.AgenceDto;
import tn.esprit.autoloc.web.dto.AgenceEmployesDto;
import tn.esprit.autoloc.web.dto.AgenceMapper;
import tn.esprit.autoloc.web.dto.EmployeMapper;

import java.util.List;

@RestController
// Le contexte /api est configure dans application.properties : URL finale /api/agences.
@RequestMapping("/agences")
@Tag(name = "Agences", description = "Gestion des agences de location")
public class AgenceController {

    private final IAgenceService agenceService;
    private final ISimulationAgenceService simulationAgenceService;
    private final AgenceMapper agenceMapper;
    private final EmployeMapper employeMapper;

    public AgenceController(IAgenceService agenceService, ISimulationAgenceService simulationAgenceService,
            AgenceMapper agenceMapper, EmployeMapper employeMapper) {
        // Injection par constructeur : le controleur delegue les regles metier au service.
        this.agenceService = agenceService;
        this.simulationAgenceService = simulationAgenceService;
        this.agenceMapper = agenceMapper;
        this.employeMapper = employeMapper;
    }

    @Operation(summary = "Lister les agences")
    @ApiResponse(responseCode = "200", description = "Liste retournee")
    @GetMapping
    public List<AgenceDto> findAll() {
        return agenceMapper.toDtoList(agenceService.findAll());
    }

    @Operation(summary = "Simuler le total des tarifs d'une agence")
    @GetMapping(value = "/{id}/simulation", produces = MediaType.TEXT_PLAIN_VALUE)
    public String simuler(@PathVariable Long id, @RequestParam int jours,
            @RequestParam VehiculeOffre offre) {
        // Exemple : /api/agences/1/simulation?jours=10&offre=WEEKEND.
        if (jours <= 0) {
            throw new IllegalArgumentException("Le nombre de jours doit etre positif");
        }
        Agence agence = agenceService.findById(id);
        return simulationAgenceService.simuler(agence, jours, offre);
    }

    @Operation(summary = "Consulter : Agence")
    @ApiResponse(responseCode = "200", description = "Ressource trouvee")
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public AgenceDto findById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        return agenceMapper.toDto(agenceService.findById(id));
    }

    @Operation(summary = "Creer : Agence")
    @ApiResponse(responseCode = "201", description = "Ressource creee")
            @ApiResponse(responseCode = "400", description = "Donnees invalides ou identifiant fourni",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    // Une creation reussie retourne 201 ; @Valid controle les contraintes de l'entite.
    @ResponseStatus(HttpStatus.CREATED)
    public AgenceDto create(@Valid @RequestBody AgenceDto agence) {
        return agenceMapper.toDto(agenceService.create(agenceMapper.toEntity(agence)));
    }

    @Operation(summary = "Modifier : Agence")
    @ApiResponse(responseCode = "200", description = "Ressource modifiee")
            @ApiResponse(responseCode = "400", description = "Donnees invalides",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public AgenceDto update(@Parameter(description = "Identifiant") @PathVariable Long id,
            @Valid @RequestBody AgenceDto agence) {
        return agenceMapper.toDto(agenceService.update(id, agenceMapper.toEntity(agence)));
    }

    @Operation(summary = "Supprimer : Agence")
    @ApiResponse(responseCode = "204", description = "Ressource supprimee, sans corps")
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    // Une suppression reussie retourne 204, sans corps JSON.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        agenceService.deleteById(id);
    }

    @Operation(summary = "Modifier une agence et ajouter de nouveaux employes dans une transaction")
    @PutMapping("/{id}/employes")
    public AgenceDto updateAvecEmployes(@PathVariable Long id,
            @Valid @RequestBody AgenceEmployesDto request) {
        return agenceMapper.toDto(agenceService.updateAvecEmployes(id, agenceMapper.toEntity(request.agence()),
                employeMapper.toEntityList(request.employes())));
    }
}
