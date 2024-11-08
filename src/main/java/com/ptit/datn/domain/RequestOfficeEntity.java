package com.ptit.datn.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

//@Entity
//@jakarta.persistence.Table(name = "request_office", schema = "defaultdb", catalog = "")
//@jakarta.persistence.IdClass(com.ptit.datn.domain.RequestOfficeEntityPK.class)
public class RequestOfficeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @jakarta.persistence.Column(name = "request_id")
    private Long requestId;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @jakarta.persistence.Column(name = "office_id")
    private Long officeId;

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        RequestOfficeEntity that = (RequestOfficeEntity) object;

        if (requestId != null ? !requestId.equals(that.requestId) : that.requestId != null) return false;
        if (officeId != null ? !officeId.equals(that.officeId) : that.officeId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = requestId != null ? requestId.hashCode() : 0;
        result = 31 * result + (officeId != null ? officeId.hashCode() : 0);
        return result;
    }
}
