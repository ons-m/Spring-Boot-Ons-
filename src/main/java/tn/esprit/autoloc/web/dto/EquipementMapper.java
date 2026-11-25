package tn.esprit.autoloc.web.dto;

import org.mapstruct.Mapper;
import tn.esprit.autoloc.domain.Equipement;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EquipementMapper {
    EquipementDto toDto(Equipement entity);
    List<EquipementDto> toDtoList(List<Equipement> entities);
    Equipement toEntity(EquipementDto dto);
}
