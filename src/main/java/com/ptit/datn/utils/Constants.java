package com.ptit.datn.utils;

import org.springframework.stereotype.Component;

@Component
public class Constants {
    public interface EntityType {
        int SERVICE_TYPE = 1;
        int CONTRACT = 2;
    }

    public interface DataType {
        int INT = 1;
        int STRING = 2;
        int DATE = 3;
        int FLOAT = 4;
        int BOOLEAN = 5;
        int MULTIPLE_CHOICE = 6;
    }

    public interface ServiceCategory {
        int UTILITIES = 1;
        int MAINTENANCE_REPAIR = 2;
        int CLEANING = 3;
        int SECURITY = 4;
        int MANAGEMENT_SUPPORT = 5;
        int PARKING = 6;
        int OTHER = 7;
    }

    public interface ContractStatus {
        int DRAFT = 1; // ban nháp
        int PENDING = 2; // đang chờ
        int ACTIVE = 3; // có hiệu lực
        int EXPIRED = 4; // hết hạn
        int TERMINATED = 5; // đã chấm dứt
        int RENEWED = 6; // đã gia hạn
        int CANCELLED = 7; // hủy bỏ
    }

    public interface ContractType {
        int LONG_TERM = 1;
        int SHORT_TERM = 2;
    }
}
