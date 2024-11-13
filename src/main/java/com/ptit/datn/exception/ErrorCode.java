package com.ptit.datn.exception;

import com.ptit.datn.utils.Translator;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    FEIGN_EXCEPTION(400, Translator.toLocale("FEIGN_EXCEPTION"), HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, Translator.toLocale("UNCATEGORIZED_EXCEPTION"), HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1002, Translator.toLocale("USER_EXISTED"), HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1003, Translator.toLocale("INVALID_USERNAME"), HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, Translator.toLocale("INVALID_PASSWORD"), HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, Translator.toLocale("USER_NOT_EXISTED"), HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(1006, Translator.toLocale("PASSWORD_INCORRECT"), HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1007, Translator.toLocale("UNAUTHENTICATED"), HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1008, Translator.toLocale("INVALID_TOKEN"), HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_NOT_MATCH(1009, Translator.toLocale("OLD_PASSWORD_NOT_MATCH"), HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_MUST_BE_DIFFERENT(1009, Translator.toLocale("NEW_PASSWORD_MUST_BE_DIFFERENT"), HttpStatus.BAD_REQUEST),
    CONFIRM_PASSWORD_NOT_MATCH(1009, Translator.toLocale("CONFIRM_PASSWORD_NOT_MATCH"), HttpStatus.BAD_REQUEST),
    INVALID_OLD_PASSWORD(1009, Translator.toLocale("INVALID_OLD_PASSWORD"), HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1010, Translator.toLocale("EMAIL_EXISTED"), HttpStatus.BAD_REQUEST),
    PASSWORD_IS_NOT_BLANK(1011, Translator.toLocale("PASSWORD_IS_NOT_BLANK"), HttpStatus.BAD_REQUEST),
    FIELDS_OF_DIGITAL_SIGNATURE_ARE_REQUIRED(1012, Translator.toLocale("FIELDS_OF_DIGITAL_SIGNATURE_ARE_REQUIRED"), HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(1013, Translator.toLocale("login.failed"), HttpStatus.BAD_REQUEST),
    USERNAME_IS_NOT_BLANK(1014, Translator.toLocale("USERNAME_IS_NOT_BLANK"), HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_IS_NOT_BLANK(1015, Translator.toLocale("NEW_PASSWORD_IS_NOT_BLANK"), HttpStatus.BAD_REQUEST),
    URL_NOT_FOUND(1016, Translator.toLocale("URL_NOT_FOUND"), HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1016, Translator.toLocale("EMAIL_INVALID"), HttpStatus.BAD_REQUEST),
    USERID_NOT_FOUND(1016, Translator.toLocale("USERID_NOT_FOUND"), HttpStatus.BAD_REQUEST),
    NEW_CONFIRM_PASSWORD_IS_NOT_BLANK(1016, Translator.toLocale("NEW_CONFIRM_PASSWORD_IS_NOT_BLANK"),
        HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(1016, Translator.toLocale("ACCESS_DENIED"), HttpStatus.BAD_REQUEST),
    EMPTY_REQUEST(1016, Translator.toLocale("EMPTY_REQUEST"), HttpStatus.BAD_REQUEST),
    RECORD_NOT_FOUND(1017, Translator.toLocale("record.notfound"), HttpStatus.NOT_FOUND);

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    public static ErrorCode findByDisplayName(String errorMessage) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.toString().equals(errorMessage)) return errorCode;
        }
        return UNCATEGORIZED_EXCEPTION;
    }
}
