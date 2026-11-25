package tn.esprit.autoloc.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import tn.esprit.autoloc.domain.Agence;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AgenceMapper {
    AgenceDto toDto(Agence agence);
    List<AgenceDto> toDtoList(List<Agence> agences);
    Agence toEntity(AgenceDto dto);
    List<Agence> toEntityList(List<AgenceDto> dtos);
    void updateEntity(AgenceDto dto, @MappingTarget Agence agence);
}
