package com.toeic.online.commons;

import com.toeic.online.service.dto.ServiceResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {

    public static ResponseEntity buildResponseUnauthorized() {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED.value())
            .body(new ServiceResult(null, HttpStatus.UNAUTHORIZED, Translator.toLocale("msg.unauthorized")));
    }

    public static ResponseEntity buildResponseBadRequest(String key) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST.value())
            .body(new ServiceResult(null, HttpStatus.BAD_REQUEST, Translator.toLocale(key)));
    }

    public static ResponseEntity buildResponseBadRequest1(String mess) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST.value())
            .body(new ServiceResult(null, HttpStatus.BAD_REQUEST, mess));
    }
}
