package com.ptit.datn.repository.specification;

import com.ptit.datn.domain.Office;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigInteger;
import java.util.Set;

public class OfficeSpecification {

    public static Specification<Office> search(String keyword) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty())
                return criteriaBuilder.conjunction();

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("building").get("name")), "%" + keyword.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("building").get("address")), "%" + keyword.toLowerCase() + "%")
            );
        };
    }

    public static Specification<Office> hasBuildingId(Long buildingId) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("building").get("id"), buildingId);
        };
    }

    public static Specification<Office> hasBuildingIdIn(Set<Long> buildingIds) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.and(root.get("building").get("id").in(buildingIds));
        };
    }

    public static Specification<Office> hasWardId(Long wardId) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("building").get("ward").get("id"), wardId);
        };
    }

    public static Specification<Office> hasDistrictId(Long districtId) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("building").get("ward").get("district").get("id"), districtId);
        };
    }

    public static Specification<Office> hasProvinceId(Long provinceId) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("building").get("ward").get("district").get("province").get("id"), provinceId);
        };
    }

    public static Specification<Office> hasAreaGreaterOrEqual(Double area) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.greaterThanOrEqualTo(root.get("area"), area);
        };
    }

    public static Specification<Office> hasAreaLessOrEqual(Double area) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.lessThanOrEqualTo(root.get("area"), area);
        };
    }

    public static Specification<Office> hasPriceGreaterOrEqual(BigInteger price) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price);
        };
    }

    public static Specification<Office> hasPriceLessOrEqual(BigInteger price) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), price);
        };
    }

    public static Specification<Office> hasStatus(Integer status) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}
