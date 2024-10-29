package com.ptit.datn.utils;

import com.ptit.datn.service.dto.model.PageFilterInput;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
}
