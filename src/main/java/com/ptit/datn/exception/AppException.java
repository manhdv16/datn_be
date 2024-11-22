package com.ptit.datn.exception;

import com.ptit.datn.utils.Translator;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.formattedMessage = Translator.getMessage(errorCode.getMessage());
    }

    public AppException(ErrorCode errorCode, Object... params) {
        super(Translator.getMessage(errorCode.getMessage(), params));
        this.errorCode = errorCode;
        this.formattedMessage = Translator.getMessage(errorCode.getMessage(), params);
    }

    private final ErrorCode errorCode;
    private final String formattedMessage;
}
