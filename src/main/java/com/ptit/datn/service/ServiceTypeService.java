package com.ptit.datn.service;

import com.cloudinary.api.exceptions.ApiException;
import com.ptit.datn.domain.ColumnPropertyEntity;
import com.ptit.datn.domain.ServiceTypeEntity;
import com.ptit.datn.exception.AppException;
import com.ptit.datn.exception.ErrorCode;
import com.ptit.datn.repository.ColumnPropertyRepository;
import com.ptit.datn.repository.ServiceTypeRepository;
import com.ptit.datn.security.SecurityUtils;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.ServiceTypeDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
import com.ptit.datn.service.mapper.ServiceTypeMapper;
import com.ptit.datn.utils.Constants;
import com.ptit.datn.utils.Utils;
import com.ptit.datn.web.rest.BuildingResource;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceTypeService {

    private static final Logger log = LoggerFactory.getLogger(ServiceTypeService.class);

    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceTypeMapper serviceTypeMapper;
    private final ColumnPropertyRepository columnPropertyRepository;

    public Page<ServiceTypeDTO> getAll(PageFilterInput<List<FilterDTO>> input){
        Pageable pageable = Utils.getPageable(input);

        Map<String, ColumnPropertyEntity> columnMap = columnPropertyRepository.findByEntityTypeAndIsActiveTrueMap(
            Constants.EntityType.SERVICE_TYPE
        );

        Condition condition = DSL.noCondition();
        for (FilterDTO filterDTO : input.getFilter()) {
            condition = condition.and(filterDTO.getCondition(columnMap));
        }
        Page<ServiceTypeEntity> serviceTypeEntities = serviceTypeRepository.getAll(input, condition, pageable);
        List<ServiceTypeDTO> result = new ArrayList<>();
        for (ServiceTypeEntity serviceTypeEntity : serviceTypeEntities) {
            ServiceTypeDTO serviceTypeDTO = serviceTypeMapper.toDTO(serviceTypeEntity);
            result.add(serviceTypeDTO);
        }
        return new PageImpl<>(result, pageable, serviceTypeEntities.getTotalElements());
    }

    public ServiceTypeDTO getDetail(Integer id){
        ServiceTypeEntity serviceType = serviceTypeRepository.findByIdAndIsActiveTrue(id);
        if(serviceType == null){
            throw new AppException(ErrorCode.RECORD_NOT_FOUND);
        }
        ServiceTypeDTO result = serviceTypeMapper.toDTO(serviceType);
        return result;
    }

    @Transactional
    public Integer saveServiceType(ServiceTypeDTO input){
        log.info("save service type by {}", SecurityUtils.getCurrentUserLogin());
        ServiceTypeEntity serviceType = serviceTypeMapper.toEntity(input);
        return serviceTypeRepository.save(serviceType).getId();
    }

    @Transactional
    public Integer updateServiceType(Integer id, ServiceTypeDTO input){
        log.info("update service type {} by {}", id, SecurityUtils.getCurrentUserLogin());
        ServiceTypeEntity serviceType = serviceTypeRepository.findByIdAndIsActiveTrue(id);
        if(serviceType == null){
            throw new AppException(ErrorCode.RECORD_NOT_FOUND);
        }
        serviceTypeMapper.updateFromDto(serviceType, input);
        return serviceTypeRepository.save(serviceType).getId();
    }

    @Transactional
    public void deleteServiceType(Integer id){
        log.info("delete service type {} by {}", id, SecurityUtils.getCurrentUserLogin());
        ServiceTypeEntity serviceType = serviceTypeRepository.findByIdAndIsActiveTrue(id);
        if(serviceType == null){
            throw new AppException(ErrorCode.RECORD_NOT_FOUND);
        }
        serviceType.setActive(false);
        serviceTypeRepository.save(serviceType);
    }
}
