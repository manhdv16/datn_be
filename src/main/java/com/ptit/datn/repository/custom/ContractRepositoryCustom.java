package com.ptit.datn.repository.custom;

import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.service.dto.BuildingContractStatDTO;
import com.ptit.datn.service.dto.ContractDTO;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
import org.jooq.Condition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContractRepositoryCustom {
    Page<ContractEntity> getAll(
        PageFilterInput<List<FilterDTO>> input,
        Condition condition,
        Pageable pageable);

    List<BuildingContractStatDTO> getStatBuildingContract(BuildingContractStatDTO input);
}
