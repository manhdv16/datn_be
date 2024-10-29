package com.ptit.datn.service.dto;

import com.ptit.datn.domain.ColumnPropertyEntity;
import com.ptit.datn.utils.Utils;
import com.ptit.datn.utils.Constants;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class FilterDTO<T> {
    private static final int IS_NULL = -1;
    private String operator;
    private String key;
    private String value;
    private String otherValue;
    private List<Object> valueSelected;

    public Condition getCondition(Map<String, ColumnPropertyEntity> columnMap){
        ColumnPropertyEntity columnPropertyEntity = columnMap.get(key);
        if(columnPropertyEntity == null) return DSL.noCondition();
        key = Utils.camelToSnake(columnPropertyEntity.getKeyNameSearch());
        try {
            switch (columnPropertyEntity.getDataType()){
                case Constants.DataType.INT:
                    if (!StringUtils.hasText(value)) return DSL.noCondition();
                    if(Integer.parseInt(value) == IS_NULL){
                        return DSL.field(key).isNull();
                    }
                    return switch (operator) {
                        case "=" -> DSL.field(key).contains(Integer.parseInt(value));
                        case ">" -> DSL.field(key).gt(Integer.parseInt(value));
                        case "<" -> DSL.field(key).lt(Integer.parseInt(value));
                        case ">=" -> DSL.field(key).ge(Integer.parseInt(value));
                        case "<=" -> DSL.field(key).le(Integer.parseInt(value));
                        case "contain" -> DSL.field(key).containsIgnoreCase(value);
                        case "in" -> DSL.field(key).in(valueSelected);
                        case "-" -> DSL.field(key).between(Integer.parseInt(value), Integer.parseInt(otherValue));
                        default -> DSL.noCondition();
                    };
                case Constants.DataType.STRING:
                    if (!StringUtils.hasText(value)) return DSL.noCondition();
                    return switch (operator) {
                        case "=" -> DSL.field(key).contains(value);
                        case ">" -> DSL.field(key).gt(value);
                        case "<" -> DSL.field(key).lt(value);
                        case ">=" -> DSL.field(key).ge(value);
                        case "<=" -> DSL.field(key).le(value);
                        case "contain" -> DSL.field(key).containsIgnoreCase(value);
                        case "in" -> DSL.field(key).in(valueSelected);
                        case "-" -> DSL.field(key).between(value, otherValue);
                        default -> DSL.noCondition();
                    };
                case Constants.DataType.DATE:
                    if (!StringUtils.hasText(value)) return DSL.noCondition();
                    return switch (operator) {
                        case "=" -> DSL.field(key).cast(LocalDate.class).eq(LocalDate.parse(value));
                        case ">" -> DSL.field(key).gt(LocalDate.parse(value).atTime(LocalTime.MAX));
                        case "<" -> DSL.field(key).lt(LocalDate.parse(value).atStartOfDay());
                        case ">=" -> DSL.field(key).ge(LocalDate.parse(value).atStartOfDay());
                        case "<=" -> DSL.field(key).le(LocalDate.parse(value).atTime(LocalTime.MAX));
                        case "contain" -> DSL.field(key).containsIgnoreCase(LocalDate.parse(value).atStartOfDay());
                        case "in" -> DSL.field(key).in(valueSelected);
                        case "-" -> DSL.field(key).between(
                            LocalDate.parse(value).atStartOfDay(),
                            LocalDate.parse(otherValue).atTime(LocalTime.MAX)
                        );
                        default -> DSL.noCondition();
                    };
                case Constants.DataType.FLOAT:
                    if (!StringUtils.hasText(value)) return DSL.noCondition();
                    return switch (operator) {
                        case "=" -> DSL.field(key).contains(Float.parseFloat(value));
                        case ">" -> DSL.field(key).gt(Float.parseFloat(value));
                        case "<" -> DSL.field(key).lt(Float.parseFloat(value));
                        case ">=" -> DSL.field(key).ge(Float.parseFloat(value));
                        case "<=" -> DSL.field(key).le(Float.parseFloat(value));
                        case "contain" -> DSL.field(key).containsIgnoreCase(Float.parseFloat(value));
                        case "in" -> DSL.field(key).in(valueSelected);
                        case "-" -> DSL.field(key).between(Float.parseFloat(value), Float.parseFloat(otherValue));
                        default -> DSL.noCondition();
                    };
                case Constants.DataType.MULTIPLE_CHOICE:
                    if (CollectionUtils.isEmpty(valueSelected) && value == null) return DSL.noCondition();
                    if(
                        (value != null && Integer.parseInt(value) == IS_NULL) ||
                            (!CollectionUtils.isEmpty(valueSelected) && valueSelected.contains(IS_NULL))
                    ){
                        return DSL.field(key).isNull();
                    }
                    return switch (operator) {
                        case "=" -> DSL.field(key).eq(Integer.parseInt(value));
                        case ">" -> DSL.field(key).gt(Integer.parseInt(value));
                        case "<" -> DSL.field(key).lt(Integer.parseInt(value));
                        case ">=" -> DSL.field(key).ge(Integer.parseInt(value));
                        case "<=" -> DSL.field(key).le(Integer.parseInt(value));
                        case "contain" -> DSL.field(key).containsIgnoreCase(Integer.parseInt(value));
                        case "in" -> DSL.field(key).in(valueSelected);
                        case "-" -> DSL.field(key).between(Float.parseFloat(value), Float.parseFloat(otherValue));
                        default -> DSL.noCondition();
                    };
                default:
                    return DSL.noCondition();
            }
        }catch(Exception e){
            return DSL.noCondition();
        }
    }

    public FilterDTO(String operator, String key, String value, String otherValue) {
        this.operator = operator;
        this.key = key;
        this.value = value;
        this.otherValue = otherValue;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOtherValue() {
        return otherValue;
    }

    public void setOtherValue(String otherValue) {
        this.otherValue = otherValue;
    }

    public List<Object> getValueSelected() {
        return valueSelected;
    }

    public void setValueSelected(List<Object> valueSelected) {
        this.valueSelected = valueSelected;
    }
}
