package tn.esprit.autoloc.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.autoloc.domain.Employe;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EmployeMapper {
    @Mapping(target = "agenceId", source = "agence.idAgence")
    EmployeDto toDto(Employe entity);
    List<EmployeDto> toDtoList(List<Employe> entities);
    @Mapping(target = "agence", ignore = true)
    Employe toEntity(EmployeDto dto);
    List<Employe> toEntityList(List<EmployeDto> dtos);
}
