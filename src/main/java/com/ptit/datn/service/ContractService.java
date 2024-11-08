package com.ptit.datn.service;

import com.cloudinary.http44.api.Response;
import com.lowagie.text.pdf.BaseFont;
import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.ColumnPropertyEntity;
import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.domain.ContractOfficeEntity;
import com.ptit.datn.exception.AppException;
import com.ptit.datn.exception.ErrorCode;
import com.ptit.datn.repository.ColumnPropertyRepository;
import com.ptit.datn.repository.ContractOfficeRepository;
import com.ptit.datn.repository.ContractRepository;
import com.ptit.datn.repository.OfficeRepository;
import com.ptit.datn.security.SecurityUtils;
import com.ptit.datn.service.dto.BuildingDTO;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final TemplateEngine templateEngine;
    private final ContractOfficeRepository contractOfficeRepository;
    public Page<ContractDTO> getAll(PageFilterInput<List<FilterDTO>> input){
        Pageable pageable = Utils.getPageable(input);
        Map<String, ColumnPropertyEntity> columnMap = columnPropertyRepository.findByEntityTypeAndIsActiveTrueMap(
            Constants.EntityType.CONTRACT
        );
        Condition condition = Utils.getFilter(input.getFilter(), columnMap);
        Page<ContractEntity> contractPage = contractRepository.getAll(input, condition, pageable);
        List<ContractEntity> contracts = contractPage.getContent();
        List<ContractDTO> result = contracts.stream()
            .map(contract -> {
                ContractDTO contractDTO = contractMapper.toDTO(contract);
                contractDTO.setOffices(officeRepository.findByContractId(contract.getId()));
                contractDTO.setTenant(userService.getUserName(contract.getTenantId()));
                return contractDTO;
            }).collect(Collectors.toList());
        return new PageImpl<>(result, pageable, contractPage.getTotalElements());
    }

    public ContractDTO getDetail(Long id){
        ContractEntity contract = contractRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOT_FOUND)
            );
        ContractDTO result = contractMapper.toDTO(contract);
        result.setOffices(officeRepository.findByContractId(contract.getId()));
        result.setTenant(userService.getUserName(contract.getTenantId()));
        return result;
    }

    @Transactional
    public Long saveContract(ContractDTO contractDTO){
        log.info("save contract by {}", SecurityUtils.getCurrentUserLogin());
        ContractEntity contractSave = contractMapper.toEntity(contractDTO);
        contractSave.setStatus(Constants.ContractStatus.DRAFT);
        if(contractDTO.getRequest() != null) {
            contractSave.setTenantId(contractDTO.getRequest().getTenantId());
        }
        contractSave = contractRepository.save(contractSave);

        List<ContractOfficeEntity> contractOfficeEntitiesSave = new ArrayList<>();
        if(contractDTO.getRequest() != null){
            for (Long officeId : contractDTO.getRequest().getOffices()) {
                ContractOfficeEntity contractOfficeEntity = new ContractOfficeEntity();
                contractOfficeEntity.setContractId(contractSave.getId());
                contractOfficeEntity.setOfficeId(officeId);
                contractOfficeEntity.setRentalPrice(officeRepository.findById(officeId).orElseThrow(
                    () -> new AppException(ErrorCode.RECORD_NOT_FOUND)
                ).getPrice());
                contractOfficeEntitiesSave.add(contractOfficeEntity);
            }
        }
        contractOfficeRepository.saveAll(contractOfficeEntitiesSave);
        return contractSave.getId();
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
        List<ContractOfficeEntity> contractOfficeEntities = contractOfficeRepository.findByContractId(contract.getId());
        contractOfficeRepository.deleteAll(contractOfficeEntities);
    }

    public void exportPdf(OutputStream outputStream, Long id) throws IOException {
        ContractDTO contractDTO = getDetail(id);

        Context context = new Context();
        context.setVariable("contract", contractDTO);
        LocalDate currentDate = LocalDate.now();
        context.setVariable("currentDate",
            String.format(
                "ngày %02d tháng %02d năm %d",
                currentDate.getDayOfMonth(),
                currentDate.getMonthValue(),
                currentDate.getYear()
            )
        );
        long totalPrice = contractDTO.getOffices().stream().mapToLong(office -> office.getPrice().longValue()*office.getArea().longValue()).sum();
        context.setVariable("totalPrice", totalPrice);

        String htmlContent = templateEngine.process("contract", context);
            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver().addFont(ResourceUtils.getFile("times.ttf").getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
    }
}
