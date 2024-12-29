package com.ptit.datn.service;

import com.cloudinary.http44.api.Response;
import com.lowagie.text.pdf.BaseFont;
import com.ptit.datn.cloudinary.CloudinaryService;
import com.ptit.datn.constants.OfficeStatus;
import com.ptit.datn.domain.*;
import com.ptit.datn.dto.response.StatisticsContractResponse;
import com.ptit.datn.exception.AppException;
import com.ptit.datn.exception.ErrorCode;
import com.ptit.datn.repository.*;
import com.ptit.datn.security.AuthoritiesConstants;
import com.ptit.datn.security.SecurityUtils;
import com.ptit.datn.service.dto.*;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService {

    private static final Logger log = LoggerFactory.getLogger(ContractService.class);

    private final ContractRepository contractRepository;
    private final OfficeRepository officeRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ColumnPropertyRepository columnPropertyRepository;
    private final ContractMapper contractMapper;
    private final TemplateEngine templateEngine;
    private final ContractOfficeRepository contractOfficeRepository;
    private final ContractSignatureRepository contractSignatureRepository;
    private final MailService mailService;
    private CloudinaryService cloudinaryService;
    public Page<ContractDTO> getAll(PageFilterInput<List<FilterDTO>> input, int operator){
        Pageable pageable = Utils.getPageable(input);
        Map<String, ColumnPropertyEntity> columnMap = columnPropertyRepository.findByEntityTypeAndIsActiveTrueMap(
            Constants.EntityType.CONTRACT
        );
        Condition condition = Utils.getFilter(input.getFilter(), columnMap, operator);
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

    private String generateContractCode(String fullname){
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        // lay so hop dong da tao trong ngay
        int index = contractRepository.countContractPerDay();
        // chuan hoa ten khach hang
        String convertedName = "";
        if(StringUtils.hasText(fullname)){
            convertedName += Arrays.stream(fullname.trim().split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase())
                .collect(Collectors.joining(""));
        }
        return "MHD" + convertedName + String.format("%02d%02d%04d", year % 100, month, index+1);
    }

    @Transactional
    public Long saveContract(ContractDTO contractDTO){
        log.info("save contract by {}", SecurityUtils.getCurrentUserLogin());
        ContractEntity contractSave = contractMapper.toEntity(contractDTO);
        contractSave.setStatus(Constants.ContractStatus.DRAFT);
        contractSave.setPaymentStatus(Constants.PaymentStatus.UN_PAID);

        UserNameDTO tenant = null;
        if(contractDTO.getRequest() != null) {
            contractSave.setTenantId(contractDTO.getRequest().getTenantId());
            tenant = userService.getUserName(contractDTO.getRequest().getTenantId());

            boolean isRole = SecurityUtils.getAuthorities().stream()
                .anyMatch(r -> Set.of(AuthoritiesConstants.ADMIN, AuthoritiesConstants.MANAGER).contains(r.toString()));

            if (isRole) {
                // Lấy thông tin người thuê
                User user = userRepository.findOneById(contractDTO.getRequest().getTenantId())
                    .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND));

                // Gửi email thông báo
                mailService.sendMailToNotification(user);
            }
        }
        String contractCode = generateContractCode(tenant != null ? tenant.getFullName() : "");
        contractSave.setCode(contractCode);
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

        // delete relevant contract_office
        List<ContractOfficeEntity> contractOfficeEntities = contractOfficeRepository.findByContractId(contract.getId());
        contractOfficeRepository.deleteAll(contractOfficeEntities);

        // delete relevant contract_signature
        List<ContractSignatureEntity> contractSignatureEntities = contractSignatureRepository.findByContractId(contract.getId());
        contractSignatureRepository.deleteAll(contractSignatureEntities);
    }

    public void exportPdf(OutputStream outputStream, Long id) throws IOException {
        ContractDTO contractDTO = getDetail(id);

        List<UserNameDTO> signers = userRepository.getUserWithSignImageByContractId(id);
        signers.forEach(user -> {
            String resizedImg = addResizeParameters(user.getImageSignature(), 170, 140, "c_scale");
            user.setImageSignature(resizedImg);
        });

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

        UserNameDTO representative = userService.getUserName(Long.parseLong(contractDTO.getCreatedBy()));
        context.setVariable("representative", representative);

        context.setVariable("offices", contractDTO.getOffices());

        long totalPrice = contractDTO.getOffices().stream().mapToLong(office -> office.getPrice().longValue()*office.getArea().longValue()).sum();
        context.setVariable("totalPrice", totalPrice);

        if (!CollectionUtils.isEmpty(signers)) {
            context.setVariable("approvalUsers", signers);
            int numberOfSignatureTable = (int) Math.ceil(signers.size() / 4.0); // Always round up
            List<List<UserNameDTO>> signatureTable = new ArrayList<>(numberOfSignatureTable);

            for (int i = 0; i < signers.size(); i++) {
                if (i % 4 == 0) {
                    signatureTable.add(new ArrayList<>(4));
                }
                signatureTable.get(i / 4).add(signers.get(i));
            }

            context.setVariable("signatureTable", signatureTable);
        }

        String htmlContent = templateEngine.process("contract", context);
            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver().addFont(ResourceUtils.getFile("times.ttf").getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
    }

    private String addResizeParameters(String originalUrl, int width, int height, String cropMode) {
        if (!originalUrl.contains("/upload/")) {
            throw new IllegalArgumentException("URL không hợp lệ, thiếu '/upload/'.");
        }
        // Thêm tham số resize vào sau "/upload/"
        String resizeParams = String.format("w_%d,h_%d,%s/", width, height, cropMode);
        return originalUrl.replace("/upload/", "/upload/" + resizeParams);
    }

    public void verifySigner(Long contractId, MultipartFile file){
        // verify
        // Sinh giá trị hash từ file tải lên
        String uploadedHash = SignatureService.generateHashFromMultipartFile(file);
        String storedHash = userService.getDigitalSignature();
        // So sánh hash
        Boolean isMatch = SignatureService.compareHashes(uploadedHash, storedHash);

        if(!isMatch)
            throw new AppException(ErrorCode.BAD_VERIFY);

        Long userId = Long.valueOf(SecurityUtils.getCurrentUserLogin().orElseThrow());

        ContractDTO contract = getDetail(contractId);

        if (!Objects.equals(userId, Long.parseLong(contract.getCreatedBy())) && !Objects.equals(userId, contract.getTenant().getId())){
            throw new AppException(ErrorCode.NOT_REPRESENTATIVE);
        }

        List<ContractSignatureEntity> signatures = contractSignatureRepository.findByContractId(contractId);

        if (signatures.stream().anyMatch(s -> Objects.equals(s.getUserId(), userId))) {
            throw new AppException(ErrorCode.ALREADY_SIGNED);
        }

        ContractSignatureEntity contractSignatureEntity = new ContractSignatureEntity();
        contractSignatureEntity.setContractId(contractId);
        contractSignatureEntity.setUserId(userId);
        contractSignatureEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        int index = contractSignatureRepository.countStep(contractId);
        contractSignatureEntity.setStep(index+1);
        contractSignatureRepository.save(contractSignatureEntity);
        updateContractStatus(contractId, index+1);
    }

    private void updateContractStatus(Long contractId, int index){
        ContractEntity contract = contractRepository.findByIdAndIsActiveTrue(contractId).orElseThrow(
            () -> new AppException(ErrorCode.RECORD_NOT_FOUND)
        );
        if(index == 1) contract.setStatus(Constants.ContractStatus.PENDING);
        else if(index == 2) {
            contract.setStatus(Constants.ContractStatus.ACTIVE);
            List<ContractOfficeEntity> contractOfficeEntities = contractOfficeRepository.findByContractId(contractId);
            Set<Long> officeIds = contractOfficeEntities.stream()
                .map(ContractOfficeEntity::getOfficeId).collect(Collectors.toSet());
            List<Office> offices = officeRepository.findAllByIds(officeIds);
            offices.forEach(
                office -> office.setStatus(OfficeStatus.RENTED)
            );
            officeRepository.saveAll(offices);
        }
        contractRepository.save(contract);
    }

    public void changeContractStatus(Long contractId, int status){
        ContractEntity contract = contractRepository.findByIdAndIsActiveTrue(contractId).orElseThrow(
            () -> new AppException(ErrorCode.RECORD_NOT_FOUND)
        );
        if(Objects.equals(status, Constants.PaymentStatus.PAID) &&
        Objects.equals(contract.getStatus(), Constants.ContractStatus.ACTIVE)
        && Objects.equals(contract.getPaymentStatus(), Constants.PaymentStatus.UN_PAID)){
            contract.setPaymentStatus(status);
            contractRepository.save(contract);
        }else{
            throw new AppException(ErrorCode.VALIDATE_STATUS);
        }
    }

    public List<BuildingContractStatDTO> getStatBuildingContract(BuildingContractStatDTO input){
        if(input.getStartDate() == null){
            input.setStartDate(contractRepository.getMinStartDate());
        }
        if(input.getEndDate() == null){
            input.setEndDate(contractRepository.getMaxEndDate());
        }

        return contractRepository.getStatBuildingContract(input);
    }

    public List<StatisticsContractResponse> getStatisticsContract(BuildingContractStatDTO input){
        if(input.getStartDate() == null){
            input.setStartDate(contractRepository.getMinStartDate());
        }
        if(input.getEndDate() == null){
            input.setEndDate(contractRepository.getMaxEndDate());
        }

        List<Object[]> results = contractRepository.findContractStatistics(input.getBuildingId(), input.getStartDate(), input.getEndDate());
        return results.stream()
            .map(row -> new StatisticsContractResponse(
                ((java.sql.Date) row[0]).toLocalDate(),
                ((Number) row[1]).longValue()
            ))
            .collect(Collectors.toList());
    }
}
