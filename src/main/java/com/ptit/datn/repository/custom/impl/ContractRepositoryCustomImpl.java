package com.ptit.datn.repository.custom.impl;

import static com.ptit.datn.database.domain.Tables.*;

import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.repository.custom.ContractRepositoryCustom;
import com.ptit.datn.service.dto.ContractDTO;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
import com.ptit.datn.service.mapper.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ContractRepositoryCustomImpl implements ContractRepositoryCustom {

    private final DSLContext dslContext;
    private final ContractMapper contractMapper;

    @Override
    public Page<ContractDTO> getAll(PageFilterInput<List<FilterDTO>> input, Condition condition, Pageable pageable) {
        SelectQuery query = dslContext
            .select(
                CONTRACT.as("contract")
            ).from(CONTRACT)
            .join(CONTRACT_OFFICE)
            .on(CONTRACT_OFFICE.CONTRACT_ID.eq(CONTRACT.ID))
            .join(OFFICE)
            .on(OFFICE.ID.eq(CONTRACT_OFFICE.OFFICE_ID))
            .join(JHI_USER)
            .on(JHI_USER.ID.eq(CONTRACT.TENANT_ID))
            .join(BUILDING)
            .on(BUILDING.ID.eq(OFFICE.BUILDING_ID))
            .getQuery();

        if(!CollectionUtils.isEmpty(input.getBuildingIds())){
            condition = condition.and(BUILDING.ID.in(input.getBuildingIds()));
        }

        condition = condition.and(CONTRACT.IS_ACTIVE.eq((byte) 1));

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
        List<ContractDTO> result = new ArrayList<>();

        for (Record record : records) {
            ContractEntity contractEntity = record.get("contract", ContractEntity.class);
            ContractDTO contractDTO = contractMapper.toDTO(contractEntity);
            result.add(contractDTO);
        }

        return new PageImpl<>(result, pageable, totalElements);
    }
}
