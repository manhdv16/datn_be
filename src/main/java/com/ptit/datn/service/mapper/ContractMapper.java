package com.ptit.datn.service.mapper;

import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.service.dto.ContractDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ContractMapper extends EntityMapper<ContractDTO, ContractEntity> {
    ContractEntity toEntity(ContractDTO contractDTO);

    @Mapping(target = "offices", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "rentalPrice", ignore = true)
    ContractDTO toDTO(ContractEntity contractEntity);

    @Mapping(target = "id", ignore = true)
    void updateFromDTO(@MappingTarget ContractEntity contractEntity, ContractDTO contractDTO);
}
