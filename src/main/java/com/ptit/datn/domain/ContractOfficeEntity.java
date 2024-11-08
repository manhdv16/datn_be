package com.ptit.datn.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(name = "contract_office", schema = "defaultdb", catalog = "")
public class ContractOfficeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;
    @Basic
    @Column(name = "contract_id")
    private Long contractId;
    @Basic
    @Column(name = "office_id")
    private Long officeId;
    @Basic
    @Column(name = "rental_price")
    private BigInteger rentalPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public BigInteger getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(BigInteger rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ContractOfficeEntity that = (ContractOfficeEntity) object;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (contractId != null ? !contractId.equals(that.contractId) : that.contractId != null) return false;
        if (officeId != null ? !officeId.equals(that.officeId) : that.officeId != null) return false;
        if (rentalPrice != null ? !rentalPrice.equals(that.rentalPrice) : that.rentalPrice != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (contractId != null ? contractId.hashCode() : 0);
        result = 31 * result + (officeId != null ? officeId.hashCode() : 0);
        result = 31 * result + (rentalPrice != null ? rentalPrice.hashCode() : 0);
        return result;
    }
}
