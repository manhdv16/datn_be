package com.ptit.datn.repository.custom.impl;

import static com.ptit.datn.database.domain.Tables.*;

import com.ptit.datn.domain.ServiceTypeEntity;
import com.ptit.datn.repository.custom.ServiceTypeRepositoryCustom;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.ServiceTypeDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ServiceTypeRepositoryCustomImpl implements ServiceTypeRepositoryCustom {

    private final DSLContext dslContext;

    @Override
    public Page<ServiceTypeEntity> getAll(PageFilterInput<List<FilterDTO>> input, Condition condition, Pageable pageable) {
        SelectQuery query = dslContext
            .select(SERVICE_TYPE)
            .from(SERVICE_TYPE)
            .getQuery();

        condition = condition.and(SERVICE_TYPE.IS_ACTIVE.eq((byte)1));

        if(input.getServiceCategory() != null){
            condition = condition.and(SERVICE_TYPE.CATEGORY.eq(input.getServiceCategory()));
        }

        query.addConditions(condition);

        long totalElements = dslContext.fetchCount(query);
        if(pageable.isPaged()){
            query.addOffset(pageable.getOffset());
            query.addLimit(pageable.getPageSize());
        }

        if(StringUtils.hasText(input.getSortProperty())){
            query.addOrderBy(input.getSort());
        }

        Result<Record> records = query.fetch();
        List<ServiceTypeEntity> result = new ArrayList<>();

        for (Record record : records) {
            result.add(record.get("service_type", ServiceTypeEntity.class));
        }

        return new PageImpl<>(result, pageable, totalElements);
    }
}
