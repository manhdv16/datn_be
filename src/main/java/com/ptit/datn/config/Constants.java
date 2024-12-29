package com.ptit.datn.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";
    public static final String DEFAULT_PASSWORD = "123456";
    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final Integer MAX_MANAGER = 5;
    private Constants() {}

    public static final class REDIS_EXPIRE {
        public static final int DEFAULT =43200000;
        public static final int TOKEN = 43200000;
        public static final int PAYMENT = 900000;
        private REDIS_EXPIRE() {}
    }

}
