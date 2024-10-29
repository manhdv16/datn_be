package com.ptit.datn.repository.custom;

import com.ptit.datn.domain.ServiceTypeEntity;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.ServiceTypeDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
import org.jooq.Condition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServiceTypeRepositoryCustom {
    Page<ServiceTypeEntity> getAll(
        PageFilterInput<List<FilterDTO>> input,
        Condition condition,
        Pageable pageable
    );
}
