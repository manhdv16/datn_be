package com.ptit.datn.service;

import com.ptit.datn.domain.ColumnPropertyEntity;
import com.ptit.datn.domain.ServiceTypeEntity;
import com.ptit.datn.repository.ColumnPropertyRepository;
import com.ptit.datn.repository.ServiceTypeRepository;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.ServiceTypeDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
import com.ptit.datn.service.mapper.ServiceTypeMapper;
import com.ptit.datn.utils.Constants;
import com.ptit.datn.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceTypeService {
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
}
