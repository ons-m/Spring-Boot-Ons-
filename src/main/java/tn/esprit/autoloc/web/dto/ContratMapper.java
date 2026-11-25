package tn.esprit.autoloc.web.dto;

import org.mapstruct.Mapper;
import tn.esprit.autoloc.domain.Contrat;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ContratMapper {
    ContratDto toDto(Contrat entity);
    List<ContratDto> toDtoList(List<Contrat> entities);
    Contrat toEntity(ContratDto dto);
}
