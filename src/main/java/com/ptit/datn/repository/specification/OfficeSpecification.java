package com.ptit.datn.repository.specification;

import com.ptit.datn.domain.Office;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class OfficeSpecification {
    public static Specification<Office> search(String search) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Predicate p1 = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + search.toLowerCase() + "%");
            Predicate p2 = criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + search.toLowerCase() + "%");
            return criteriaBuilder.or(p1, p2);
        };
    }

    public static Specification<Office> hasAreaBetween(Double minArea, Double maxArea) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Predicate p1 = criteriaBuilder.greaterThanOrEqualTo(root.get("area"), minArea);
            Predicate p2 = criteriaBuilder.lessThanOrEqualTo(root.get("area"), maxArea);
            return criteriaBuilder.and(p1, p2);
        };
    }

    public static Specification<Office> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (Root<Office> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Predicate p1 = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            Predicate p2 = criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            return criteriaBuilder.and(p1, p2);
        };
    }

}
