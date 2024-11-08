package com.ptit.datn.utils;

import com.ptit.datn.domain.ColumnPropertyEntity;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public static String convertToCustomFormat(Object date) {
        if (date instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("'ngày' dd 'tháng' MM 'năm' yyyy");
            return sdf.format((Date) date);
        } else if (date instanceof LocalDate) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'ngày' dd 'tháng' MM 'năm' yyyy");
            return ((LocalDate) date).format(formatter);
        } else {
            throw new IllegalArgumentException("Đối tượng không hợp lệ. Cần phải là Date hoặc LocalDate.");
        }
    }

    public static String convertToWord(long number) {
        if (number == 0L) return "không";

        String[] units = {"", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        String[] teens = {"mười", "mười một", "mười hai", "mười ba", "mười bốn", "mười lăm", "mười sáu", "mười bảy", "mười tám", "mười chín"};
        String[] tens = {"", "", "hai mươi", "ba mươi", "bốn mươi", "năm mươi", "sáu mươi", "bảy mươi", "tám mươi", "chín mươi"};
        String[] scales = {"", "nghìn", "triệu", "tỷ"};

        StringBuilder words = new StringBuilder();
        int scaleIndex = 0;

        while (number > 0) {
            int part = (int) (number % 1000);
            if (part > 0) {
                StringBuilder partWords = new StringBuilder();
                if (part >= 100) {
                    partWords.append(units[part / 100]).append(" trăm ");
                    part %= 100;
                }
                if (part >= 20) {
                    partWords.append(tens[part / 10]).append(" ");
                    part %= 10;
                } else if (part >= 10) {
                    partWords.append(teens[part - 10]).append(" ");
                    part = 0;
                }
                if (part > 0) {
                    partWords.append(units[part]).append(" ");
                }
                words.insert(0, partWords.toString().trim() + " " + scales[scaleIndex] + " ");
            }
            number /= 1000;
            scaleIndex++;
        }

        return words.toString().trim();
    }
}
