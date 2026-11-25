package tn.esprit.autoloc.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.autoloc.domain.Maintenance;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface MaintenanceMapper {
    @Mapping(target = "vehiculeId", source = "vehicule.idVehicule")
    MaintenanceDto toDto(Maintenance entity);
    List<MaintenanceDto> toDtoList(List<Maintenance> entities);
    @Mapping(target = "vehicule", ignore = true)
    Maintenance toEntity(MaintenanceDto dto);
}
