package tn.esprit.autoloc.web.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.autoloc.domain.Paiement;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PaiementMapper {
    @Mapping(target = "contratId", source = "contrat.idContrat")
    PaiementDto toDto(Paiement entity);
    List<PaiementDto> toDtoList(List<Paiement> entities);
}
