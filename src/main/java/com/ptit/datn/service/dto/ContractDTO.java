package com.ptit.datn.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class ContractDTO {
    private Long id;
    private String code;
    private Long officeId;
    private OfficeDTO office;
    private Long tenantId;
    private UserNameDTO tenant;
    private Date startDate;
    private Date endDate;
    private String duration;
    private BigDecimal rentalPrice;
    private BigDecimal depositAmount;
    private String paymentFrequency;
    private Date handoverDate;
    private String rentalPurpose;
    private Integer status;
    private Integer contractType;
    private String terminationClause;
    private String contractDetails;
    private String renewalTerms;
    private String createdBy;
    private Instant lastModifiedDate;
}
