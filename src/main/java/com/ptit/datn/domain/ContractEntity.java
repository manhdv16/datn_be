package com.ptit.datn.domain;

import com.ptit.datn.utils.Constants;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

@Entity
@Table(name = "contract", schema = "defaultdb", catalog = "")
public class ContractEntity extends AbstractAuditingEntity<Long>{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;
    @Basic
    @Column(name = "code")
    private String code;
    @Basic
    @Column(name = "tenant_id")
    private Long tenantId;
    @Basic
    @Column(name = "start_date")
    private Date startDate;
    @Basic
    @Column(name = "end_date")
    private Date endDate;
    @Basic
    @Column(name = "duration")
    private String duration;
    @Basic
    @Column(name = "deposit_amount")
    private BigInteger depositAmount;
    @Basic
    @Column(name = "payment_frequency")
    private String paymentFrequency;
    @Basic
    @Column(name = "handover_date")
    private Date handoverDate;
    @Basic
    @Column(name = "rental_purpose")
    private String rentalPurpose;
    @Basic
    @Column(name = "status")
    private Integer status;
    @Basic
    @Column(name = "contract_type")
    private Integer contractType;
    @Basic
    @Column(name = "termination_clause")
    private String terminationClause;
    @Basic
    @Column(name = "contract_details")
    private String contractDetails;
    @Basic
    @Column(name = "renewal_terms")
    private String renewalTerms;
    @Basic
    @Column(name = "is_active")
    private Boolean isActive = true;
    @Basic
    @Column(name = "payment_status")
    private Integer paymentStatus = Constants.PaymentStatus.UN_PAID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public BigInteger getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigInteger depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public Date getHandoverDate() {
        return handoverDate;
    }

    public void setHandoverDate(Date handoverDate) {
        this.handoverDate = handoverDate;
    }

    public String getRentalPurpose() {
        return rentalPurpose;
    }

    public void setRentalPurpose(String rentalPurpose) {
        this.rentalPurpose = rentalPurpose;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getContractType() {
        return contractType;
    }

    public void setContractType(Integer contractType) {
        this.contractType = contractType;
    }

    public String getTerminationClause() {
        return terminationClause;
    }

    public void setTerminationClause(String terminationClause) {
        this.terminationClause = terminationClause;
    }

    public String getContractDetails() {
        return contractDetails;
    }

    public void setContractDetails(String contractDetails) {
        this.contractDetails = contractDetails;
    }

    public String getRenewalTerms() {
        return renewalTerms;
    }

    public void setRenewalTerms(String renewalTerms) {
        this.renewalTerms = renewalTerms;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ContractEntity that = (ContractEntity) object;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (tenantId != null ? !tenantId.equals(that.tenantId) : that.tenantId != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (duration != null ? !duration.equals(that.duration) : that.duration != null) return false;
        if (depositAmount != null ? !depositAmount.equals(that.depositAmount) : that.depositAmount != null)
            return false;
        if (paymentFrequency != null ? !paymentFrequency.equals(that.paymentFrequency) : that.paymentFrequency != null)
            return false;
        if (handoverDate != null ? !handoverDate.equals(that.handoverDate) : that.handoverDate != null)
            return false;
        if (rentalPurpose != null ? !rentalPurpose.equals(that.rentalPurpose) : that.rentalPurpose != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (contractType != null ? !contractType.equals(that.contractType) : that.contractType != null) return false;
        if (terminationClause != null ? !terminationClause.equals(that.terminationClause) : that.terminationClause != null)
            return false;
        if (contractDetails != null ? !contractDetails.equals(that.contractDetails) : that.contractDetails != null)
            return false;
        if (renewalTerms != null ? !renewalTerms.equals(that.renewalTerms) : that.renewalTerms != null) return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;
        if (paymentStatus != null ? !paymentStatus.equals(that.paymentStatus) : that.paymentStatus != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (depositAmount != null ? depositAmount.hashCode() : 0);
        result = 31 * result + (paymentFrequency != null ? paymentFrequency.hashCode() : 0);
        result = 31 * result + (handoverDate != null ? handoverDate.hashCode() : 0);
        result = 31 * result + (rentalPurpose != null ? rentalPurpose.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (contractType != null ? contractType.hashCode() : 0);
        result = 31 * result + (terminationClause != null ? terminationClause.hashCode() : 0);
        result = 31 * result + (contractDetails != null ? contractDetails.hashCode() : 0);
        result = 31 * result + (renewalTerms != null ? renewalTerms.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        result = 31 * result + (paymentStatus != null ? paymentStatus.hashCode() : 0);
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
