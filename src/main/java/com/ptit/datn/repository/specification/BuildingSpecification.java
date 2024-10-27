package com.ptit.datn.repository.specification;

import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.District;
import com.ptit.datn.domain.Ward;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class BuildingSpecification {

    public static Specification<Building> search(String search) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + search.toLowerCase() + "%");
        };
    }

    public static Specification<Building> hasWardId(Long wardId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("wardId"), wardId);
        };
    }

    public static Specification<Building> hasDistrictId(Long districtId) {
        return (root, query, criteriaBuilder) -> {
            Root<Ward> wardRoot = query.from(Ward.class);
            query.where(
                criteriaBuilder.equal(root.get("wardId"), wardRoot.get("id"))
            );
            return criteriaBuilder.equal(wardRoot.get("districtId"), districtId);
        };
    }

    public static Specification<Building> hasProvinceId(Long provinceId) {
        return (root, query, criteriaBuilder) -> {
            Root<Ward> wardRoot = query.from(Ward.class);
            Root<District> districtRoot = query.from(District.class);
            query.where(
                criteriaBuilder.equal(root.get("wardId"), wardRoot.get("id")),
                criteriaBuilder.equal(wardRoot.get("districtId"), districtRoot.get("id"))
            );
            return criteriaBuilder.equal(districtRoot.get("provinceId"), provinceId);
        };
    }
}
