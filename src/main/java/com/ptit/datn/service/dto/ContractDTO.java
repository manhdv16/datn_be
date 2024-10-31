package com.ptit.datn.service.dto;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ContractDTO {
    private Long id;
    private String code;
    private BuildingNameDTO building;
    private Long officeId;
    private OfficeDTO office;
    private Long tenantId;
    private UserNameDTO tenant;
    private Timestamp startDate;
    private Timestamp endDate;
    private String duration;
    private BigDecimal rentalPrice;
    private BigDecimal depositAmount;
    private String paymentFrequency;
    private Date paymentDueDate;
    private Integer status;
    private Integer contractType;
    private String terminationClause;
    private String contractDetails;
    private String renewalTerms;
    private String createdBy;
    private Instant lastModifiedDate;
}
