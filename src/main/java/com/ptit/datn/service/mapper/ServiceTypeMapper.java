package com.ptit.datn.service.mapper;

import com.ptit.datn.domain.ServiceTypeEntity;
import com.ptit.datn.service.dto.ServiceTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ServiceTypeMapper extends EntityMapper<ServiceTypeDTO, ServiceTypeEntity>{
    ServiceTypeEntity toEntity(ServiceTypeDTO serviceTypeDTO);

    ServiceTypeDTO toDTO(ServiceTypeEntity serviceTypeEntity);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(@MappingTarget ServiceTypeEntity serviceTypeEntity, ServiceTypeDTO serviceTypeDTO);
}
