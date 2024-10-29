package com.ptit.datn.utils;

import org.springframework.stereotype.Component;

@Component
public class Constants {
    public interface EntityType {
        int SERVICE_TYPE = 1;
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
}
