package com.ptit.datn.exception;

import com.ptit.datn.utils.Translator;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    FEIGN_EXCEPTION(400, "FEIGN_EXCEPTION", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "UNCATEGORIZED_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1002, "USER_EXISTED", HttpStatus.BAD_REQUEST),
    USER_ID_EXISTED(1002, "USER_ID_EXISTED", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1003, "INVALID_USERNAME", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "INVALID_PASSWORD", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "USER_NOT_EXISTED", HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(1006, "PASSWORD_INCORRECT", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1007, "UNAUTHENTICATED", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1008, "INVALID_TOKEN", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_NOT_MATCH(1009, "OLD_PASSWORD_NOT_MATCH", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_MUST_BE_DIFFERENT(1009, "NEW_PASSWORD_MUST_BE_DIFFERENT", HttpStatus.BAD_REQUEST),
    CONFIRM_PASSWORD_NOT_MATCH(1009, "CONFIRM_PASSWORD_NOT_MATCH", HttpStatus.BAD_REQUEST),
    INVALID_OLD_PASSWORD(1009, "INVALID_OLD_PASSWORD", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1010, "EMAIL_EXISTED", HttpStatus.BAD_REQUEST),
    PASSWORD_IS_NOT_BLANK(1011, "PASSWORD_IS_NOT_BLANK", HttpStatus.BAD_REQUEST),
    FIELDS_OF_DIGITAL_SIGNATURE_ARE_REQUIRED(1012, "FIELDS_OF_DIGITAL_SIGNATURE_ARE_REQUIRED", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(1013, "login.failed", HttpStatus.BAD_REQUEST),
    USERNAME_IS_NOT_BLANK(1014, "USERNAME_IS_NOT_BLANK", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_IS_NOT_BLANK(1015, "NEW_PASSWORD_IS_NOT_BLANK", HttpStatus.BAD_REQUEST),
    URL_NOT_FOUND(1016, "URL_NOT_FOUND", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1016, "EMAIL_INVALID", HttpStatus.BAD_REQUEST),
    USERID_NOT_FOUND(1016, "USERID_NOT_FOUND", HttpStatus.BAD_REQUEST),
    NEW_CONFIRM_PASSWORD_IS_NOT_BLANK(1016, "NEW_CONFIRM_PASSWORD_IS_NOT_BLANK",
        HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(1016, "ACCESS_DENIED", HttpStatus.BAD_REQUEST),
    EMPTY_REQUEST(1016, "EMPTY_REQUEST", HttpStatus.BAD_REQUEST),
    MAX_MANAGER(1016, "MAX_MANAGER", HttpStatus.BAD_REQUEST),
    NUMBER_MANAGER_OF_BUILDING(1016, "NUMBER_MANAGER_OF_BUILDING", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER(400, "INVALID_PHONE_NUMBER", HttpStatus.BAD_REQUEST),
    INVALID_CCCD(400, "INVALID_CCCD", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(400, "INVALID_EMAIL", HttpStatus.BAD_REQUEST),
    INVALID_FULLNAME(400, "INVALID_FULLNAME", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(400, "PASSWORD_REQUIRED", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_REQUIRED(400, "NEW_PASSWORD_REQUIRED", HttpStatus.BAD_REQUEST),
    RECORD_NOT_FOUND(1017, "record.notfound", HttpStatus.NOT_FOUND),
    BAD_VERIFY(400, "verify.fail", HttpStatus.BAD_REQUEST),
    NOT_REPRESENTATIVE(400, "not.representative", HttpStatus.BAD_REQUEST),
    KEY_RESET_PASSWORD_NOT_TRUE(400, "KEY_RESET_PASSWORD_NOT_TRUE", HttpStatus.BAD_REQUEST),
    ALREADY_SIGNED(400, "already.signed", HttpStatus.BAD_REQUEST),
    VALIDATE_STATUS(400, "contract.notend", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
        formattedMessage = Translator.getMessage(message);
    }

    private final int code;
    private final String message;
    private final String formattedMessage;
    private final HttpStatus statusCode;

    public static ErrorCode findByDisplayName(String errorMessage) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.toString().equals(errorMessage)) return errorCode;
        }
        return UNCATEGORIZED_EXCEPTION;
    }
}
