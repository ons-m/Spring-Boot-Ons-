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
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.web.ErrorResponse;
import tn.esprit.autoloc.web.dto.ReservationDto;
import tn.esprit.autoloc.web.dto.ReservationMapper;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@Tag(name = "Reservations", description = "Gestion des reservations")
public class ReservationController {

    private final IReservationService reservationService;
    private final ReservationMapper reservationMapper;

    public ReservationController(IReservationService reservationService, ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
    }

    @Operation(summary = "Lister les reservations")
    @ApiResponse(responseCode = "200", description = "Liste retournee")
    @GetMapping
    public List<ReservationDto> findAll() {
        return reservationMapper.toDtoList(reservationService.findAll());
    }

    @Operation(summary = "Consulter : Reservation")
    @ApiResponse(responseCode = "200", description = "Ressource trouvee")
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ReservationDto findById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        return reservationMapper.toDto(reservationService.findById(id));
    }

    @Operation(summary = "Creer : Reservation")
    @ApiResponse(responseCode = "201", description = "Ressource creee")
            @ApiResponse(responseCode = "400", description = "Donnees invalides ou identifiant fourni",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDto create(@Valid @RequestBody ReservationDto reservation) {
        return reservationMapper.toDto(reservationService.create(reservationMapper.toEntity(reservation)));
    }

    @Operation(summary = "Modifier : Reservation")
    @ApiResponse(responseCode = "200", description = "Ressource modifiee")
            @ApiResponse(responseCode = "400", description = "Donnees invalides",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public ReservationDto update(@Parameter(description = "Identifiant") @PathVariable Long id,
            @Valid @RequestBody ReservationDto reservation) {
        return reservationMapper.toDto(reservationService.update(id, reservationMapper.toEntity(reservation)));
    }

    @Operation(summary = "Supprimer : Reservation")
    @ApiResponse(responseCode = "204", description = "Ressource supprimee, sans corps")
            @ApiResponse(responseCode = "404", description = "Ressource introuvable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        reservationService.deleteById(id);
    }
}
