package tn.esprit.autoloc.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.autoloc.domain.Reservation;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ReservationMapper {
    @Mapping(target = "clientId", source = "client.idClient")
    @Mapping(target = "vehiculeId", source = "vehicule.idVehicule")
    @Mapping(target = "contratId", source = "contrat.idContrat")
    ReservationDto toDto(Reservation entity);
    List<ReservationDto> toDtoList(List<Reservation> entities);
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "vehicule", ignore = true)
    @Mapping(target = "contrat", ignore = true)
    Reservation toEntity(ReservationDto dto);
}
