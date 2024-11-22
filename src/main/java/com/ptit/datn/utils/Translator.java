package com.ptit.datn.utils;

import com.ptit.datn.constants.Constants;
import jakarta.servlet.http.HttpServletRequest;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Slf4j
public class Translator {

    private static ResourceBundleMessageSource messageSource;
    private static final List<Locale> locales = Arrays.asList(new Locale(Constants.EN), new Locale(Constants.VI));

    @Autowired
    public Translator(ResourceBundleMessageSource messageSource) {
        Translator.messageSource = messageSource;
    }

    public static String toLocale(String msgCode) {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return messageSource.getMessage(msgCode, null, new Locale(Constants.VI));
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        try {
            return messageSource.getMessage(msgCode, null, resolveLocale(request));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return "";
        }
    }

    public static String getMessage(String msgCode, Object... args) {
        if(RequestContextHolder.getRequestAttributes() == null) {
            return messageSource.getMessage(msgCode, null, new Locale(Constants.VI));
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        try {
            String message = messageSource.getMessage(msgCode, null, resolveLocale(request));
            return MessageFormat.format(message, args);
        }catch (Exception ex){
            log.error(ex.getMessage(), ex);
            return "";
        }
    }

    public static Locale resolveLocale(HttpServletRequest request) {
        String acceptLanguageHeader = request.getHeader(Constants.REQUEST_HEADER.ACCEPT_LANGUAGE);

        if (acceptLanguageHeader == null || acceptLanguageHeader.isEmpty()) {
            return new Locale(Constants.VI);
        }

        // Kiểm tra xem acceptLanguageHeader có chứa ký tự đặc biệt không
        if (acceptLanguageHeader.matches(Constants.REGEX_PATH)) {
            return new Locale(Constants.VI);
        }

        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(acceptLanguageHeader);
        return Locale.lookup(list, locales) == null ? new Locale(Constants.VI) : Locale.lookup(list, locales);
    }
}
