package com.ptit.datn.repository.custom.impl;

import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.repository.custom.ContractRepositoryCustom;
import com.ptit.datn.service.dto.BuildingContractStatDTO;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
import com.ptit.datn.service.mapper.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ptit.datn.database.domain.Tables.*;

@RequiredArgsConstructor
public class ContractRepositoryCustomImpl implements ContractRepositoryCustom {

    private final DSLContext dslContext;
    private final ContractMapper contractMapper;

    @Override
    public Page<ContractEntity> getAll(PageFilterInput<List<FilterDTO>> input, Condition condition, Pageable pageable) {
        SelectQuery query = dslContext
            .select(
                CONTRACT.as("contract")
            ).from(CONTRACT)
//            .join(CONTRACT_OFFICE)
//            .on(CONTRACT_OFFICE.CONTRACT_ID.eq(CONTRACT.ID))
//            .join(OFFICE)
//            .on(OFFICE.ID.eq(CONTRACT_OFFICE.OFFICE_ID))
//            .join(JHI_USER)
//            .on(JHI_USER.ID.eq(CONTRACT.TENANT_ID))
//            .join(BUILDING)
//            .on(BUILDING.ID.eq(OFFICE.BUILDING_ID))
            .getQuery();

        if(!CollectionUtils.isEmpty(input.getBuildingIds())){
            condition = condition.and(BUILDING.ID.in(input.getBuildingIds()));
        }

        condition = condition.and(CONTRACT.IS_ACTIVE.eq((byte) 1));

        query.addConditions(condition);

        query.addGroupBy(CONTRACT.ID);

        long totalElements = dslContext.fetchCount(query);

        if(pageable.isPaged()){
            query.addOffset(pageable.getOffset());
            query.addLimit(pageable.getPageSize());
        }

        if(StringUtils.hasText(input.getSortProperty())){
            query.addOrderBy(input.getSort());
        }

        Result<Record> records = query.fetch();
        List<ContractEntity> result = new ArrayList<>();

        for (Record record : records) {
            ContractEntity contractEntity = record.get("contract", ContractEntity.class);
            result.add(contractEntity);
        }

        return new PageImpl<>(result, pageable, totalElements);
    }

    @Override
    public List<BuildingContractStatDTO> getStatBuildingContract(BuildingContractStatDTO input) {
        SelectQuery query = dslContext.select(
            BUILDING.ID.as("buildingId"),
            BUILDING.NAME.as("buildingName"),
            DSL.countDistinct(CONTRACT.ID).as("numberOfContracts")
        ).from(BUILDING)
            .leftJoin(OFFICE)
            .on(OFFICE.BUILDING_ID.eq(BUILDING.ID))
            .leftJoin(CONTRACT_OFFICE)
            .on(CONTRACT_OFFICE.OFFICE_ID.eq(OFFICE.ID))
            .leftJoin(CONTRACT)
            .on(CONTRACT.ID.eq(CONTRACT_OFFICE.CONTRACT_ID))
            .and(CONTRACT.START_DATE.greaterOrEqual(input.getStartDate()))
            .and(CONTRACT.END_DATE.lessOrEqual(input.getEndDate()))
            .and(CONTRACT.IS_ACTIVE.eq((byte)1))
            .getQuery();

        Condition condition = DSL.noCondition();

        if(input.getBuildingId() != null){
            condition = condition.and(BUILDING.ID.eq(input.getBuildingId()));
        }

        query.addConditions(condition);
        query.addGroupBy(BUILDING.ID, BUILDING.NAME);
        return query.fetchInto(BuildingContractStatDTO.class);
    }
}
