package com.ptit.datn.service;

import com.cloudinary.http44.api.Response;
import com.lowagie.text.pdf.BaseFont;
import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.ColumnPropertyEntity;
import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.exception.AppException;
import com.ptit.datn.exception.ErrorCode;
import com.ptit.datn.repository.ColumnPropertyRepository;
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
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
    private final TemplateEngine templateEngine;

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

        String htmlContent = templateEngine.process("contract", context);
            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver().addFont(ResourceUtils.getFile("times.ttf").getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
    }
}
