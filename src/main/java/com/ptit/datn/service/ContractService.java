package com.ptit.datn.service;

import com.ptit.datn.domain.ColumnPropertyEntity;
import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.exception.AppException;
import com.ptit.datn.exception.ErrorCode;
import com.ptit.datn.repository.ColumnPropertyRepository;
import com.ptit.datn.repository.ContractRepository;
import com.ptit.datn.repository.OfficeRepository;
import com.ptit.datn.security.SecurityUtils;
import com.ptit.datn.service.dto.ContractDTO;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.OfficeDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
import com.ptit.datn.service.mapper.ContractMapper;
import com.ptit.datn.utils.Constants;
import com.ptit.datn.utils.Utils;
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

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContractService {

    private static final Logger log = LoggerFactory.getLogger(ContractService.class);

    private final ContractRepository contractRepository;
    private final OfficeService officeService;
    private final OfficeRepository officeRepository;
    private final UserService userService;
    private final ColumnPropertyRepository columnPropertyRepository;
    private final ContractMapper contractMapper;

    public Page<ContractDTO> getAll(PageFilterInput<List<FilterDTO>> input){
        Pageable pageable = Utils.getPageable(input);
        Map<String, ColumnPropertyEntity> columnMap = columnPropertyRepository.findByEntityTypeAndIsActiveTrueMap(
            Constants.EntityType.CONTRACT
        );
        Condition condition = Utils.getFilter(input.getFilter(), columnMap);
        Page<ContractDTO> contractPage = contractRepository.getAll(input, condition, pageable);
        List<ContractDTO> contractDTOS = contractPage.getContent();
        contractDTOS.forEach(
            contract -> {
                contract.setOffice(officeService.getOffice(contract.getOfficeId()));
                contract.setTenant(userService.getUserName(contract.getTenantId()));
            }
        );
        return new PageImpl<>(contractDTOS, pageable, contractPage.getTotalElements());
    }

    public ContractDTO getDetail(Long id){
        ContractEntity contract = contractRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOT_FOUND)
            );
        ContractDTO result = contractMapper.toDTO(contract);
        result.setBuilding(officeRepository.findBuildingByOfficeId(contract.getOfficeId()).orElseThrow(
            () -> new AppException(ErrorCode.RECORD_NOT_FOUND)
        ));
        result.setOffice(officeService.getOffice(contract.getOfficeId()));
        result.setTenant(userService.getUserName(contract.getTenantId()));
        return result;
    }

    @Transactional
    public Long saveContract(ContractDTO contractDTO){
        log.info("save contract by {}", SecurityUtils.getCurrentUserLogin());
        ContractEntity contractSave = contractMapper.toEntity(contractDTO);
        contractSave.setStatus(Constants.ContractStatus.DRAFT);
        return contractRepository.save(contractSave).getId();
    }

    @Transactional
    public Long updateContract(Long id, ContractDTO contractDTO){
        ContractEntity contract = contractRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOT_FOUND)
            );
        contractMapper.updateFromDTO(contract, contractDTO);
        return contractRepository.save(contract).getId();
    }

    @Transactional
    public void deleteContract(Long id){
        ContractEntity contract = contractRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOT_FOUND)
            );
        contract.setActive(false);
        contractRepository.save(contract);
    }
}
