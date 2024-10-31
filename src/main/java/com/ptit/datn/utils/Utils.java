package com.ptit.datn.utils;

import com.ptit.datn.domain.ColumnPropertyEntity;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public class Utils {
    public static String camelToSnake(String camelCase) {
        // Use a regular expression to find the positions where we need to insert underscores
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";

        // Replace the matches with the underscore and convert to lowercase
        String snakeCase = camelCase.replaceAll(regex, replacement).toLowerCase();

        return snakeCase;
    }

    public static Pageable getPageable(PageFilterInput<?> input) {
        if (input.getPageSize() == 0) return Pageable.unpaged();
        return PageRequest.of(input.getPageNumber(), input.getPageSize());
    }

    public static Condition getFilter(List<FilterDTO> filters, Map<String, ColumnPropertyEntity> columnMap){
        Condition condition = DSL.noCondition();
        for (FilterDTO filter : filters) {
            condition = condition.and(filter.getCondition(columnMap));
        }
        return condition;
    }
}
