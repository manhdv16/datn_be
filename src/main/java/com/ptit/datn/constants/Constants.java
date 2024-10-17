package com.ptit.datn.constants;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX =
        "^(?=.{1,50}$)(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$";
    public static final String SYSTEM = "system";
    public static final String VI = "vi";
    public static final String EN = "en";
    public static final String REGEX_PATH = "^/|(/[\\w-]+)+$";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String SPECIAL_CHARACTER = "\"";
    public static final Integer INDEX_OF_MESSAGE = 1;

    public static final class REQUEST_HEADER {

        private REQUEST_HEADER() {}

        public static final String USER_NAME = "USER-NAME";
        public static final String LANGUAGE = "LANGUAGE";
        public static final String AUTHORIZATION = "Authorization";
        public static final String ACCEPT_LANGUAGE = "Accept-Language";
        public static final String USER_INFOR = "userInfo";
        public static final String LANG_VI = "vi-VN";
        public static final String LANG_EN = "en-US";
        public static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
        public static final String X_FORWARDED_HOST = "X-FORWARDED-HOST";
    }

    private Constants() {}

    public static final class HTTP_STATUS {

        private HTTP_STATUS() {}

        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int ACCEPTED = 202;
        public static final int NO_CONTENT = 204;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int METHOD_NOT_ALLOWED = 405;
        public static final int UNSUPPORTED_MEDIA_TYPE = 415;
        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int OTHER_SERVER_ERROR = -1;
    }
}
