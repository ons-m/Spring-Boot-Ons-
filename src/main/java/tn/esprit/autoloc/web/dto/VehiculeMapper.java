package tn.esprit.autoloc.web.dto;

import org.mapstruct.Mapper;
import tn.esprit.autoloc.domain.Vehicule;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface VehiculeMapper {
    VehiculeDto toDto(Vehicule vehicule);
    List<VehiculeDto> toDtoList(List<Vehicule> vehicules);
    Vehicule toEntity(VehiculeDto dto);
    List<Vehicule> toEntityList(List<VehiculeDto> dtos);
}
