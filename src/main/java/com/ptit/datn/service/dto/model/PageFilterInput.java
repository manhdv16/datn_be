package com.ptit.datn.service.dto.model;

import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.utils.Utils;
import jakarta.validation.constraints.NotNull;
import org.jooq.OrderField;
import org.jooq.impl.DSL;

import java.util.List;

public class PageFilterInput<T> {

    @NotNull(message = "pageNumber must not be null")
    private Integer pageNumber;
    @NotNull(message = "pageSize must not be null")
    private Integer pageSize;
    @NotNull(message = "filter must not be null")
    private List<FilterDTO> filter;
    private String sortProperty;
    private String sortOrder;

    public OrderField getSort() {
        if (this.sortProperty == null) {
            return null;
        }
        OrderField<?> orderField;

        if ("ASC".equals(this.sortOrder)) {
            orderField = DSL.field(Utils.camelToSnake(this.sortProperty)).asc();
        } else {
            orderField = DSL.field(Utils.camelToSnake(this.sortProperty)).desc();
        }
        return orderField;
    }

    private Integer serviceCategory;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<FilterDTO> getFilter() {
        return filter;
    }

    public void setFilter(List<FilterDTO> filter) {
        this.filter = filter;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(Integer serviceCategory) {
        this.serviceCategory = serviceCategory;
    }
}
