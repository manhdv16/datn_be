package com.ptit.datn.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ContractDTO {
    private Long id;
    private String code;
    private List<OfficeDTO> offices;
    private Long tenantId;
    private UserNameDTO tenant;
    private Date startDate;
    private Date endDate;
    private String duration;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private BigInteger rentalPrice;
    private BigInteger depositAmount;
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
