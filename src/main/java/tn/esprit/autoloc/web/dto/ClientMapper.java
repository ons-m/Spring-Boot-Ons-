package tn.esprit.autoloc.web.dto;

import org.mapstruct.Mapper;
import tn.esprit.autoloc.domain.Client;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ClientMapper {
    ClientDto toDto(Client entity);
    List<ClientDto> toDtoList(List<Client> entities);
    Client toEntity(ClientDto dto);
}
