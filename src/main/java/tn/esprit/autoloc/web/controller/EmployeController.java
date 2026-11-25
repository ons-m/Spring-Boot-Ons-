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
import tn.esprit.autoloc.service.IEmployeService;
import tn.esprit.autoloc.web.ErrorResponse;
import tn.esprit.autoloc.web.dto.EmployeDto;
import tn.esprit.autoloc.web.dto.EmployeMapper;

import java.util.List;

@RestController
@RequestMapping("/employes")
@Tag(name = "Employes", description = "Gestion des employes")
public class EmployeController {

    private final IEmployeService employeService;
    private final EmployeMapper employeMapper;

    public EmployeController(IEmployeService employeService, EmployeMapper employeMapper) {
        this.employeService = employeService;
        this.employeMapper = employeMapper;
    }

    @Operation(summary = "Lister les employes")
    @ApiResponse(responseCode = "200", description = "Liste retournee")
    @GetMapping
    public List<EmployeDto> findAll() {
        return employeMapper.toDtoList(employeService.findAll());
    }

    @Operation(summary = "Consulter : Employe")
    @ApiResponse(responseCode = "200", description = "Ressource trouvee")
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public EmployeDto findById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        return employeMapper.toDto(employeService.findById(id));
    }

    @Operation(summary = "Creer : Employe")
    @ApiResponse(responseCode = "201", description = "Ressource creee")
            @ApiResponse(responseCode = "400", description = "Donnees invalides ou identifiant fourni",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeDto create(@Valid @RequestBody EmployeDto employe) {
        return employeMapper.toDto(employeService.create(employeMapper.toEntity(employe)));
    }

    @Operation(summary = "Modifier : Employe")
    @ApiResponse(responseCode = "200", description = "Ressource modifiee")
            @ApiResponse(responseCode = "400", description = "Donnees invalides",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public EmployeDto update(@Parameter(description = "Identifiant") @PathVariable Long id,
            @Valid @RequestBody EmployeDto employe) {
        return employeMapper.toDto(employeService.update(id, employeMapper.toEntity(employe)));
    }

    @Operation(summary = "Supprimer : Employe")
    @ApiResponse(responseCode = "204", description = "Ressource supprimee, sans corps")
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        employeService.deleteById(id);
    }
}
