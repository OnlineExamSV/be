package com.toeic.online.service.dto;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class ServiceResult<T> implements Serializable {
    private HttpStatus status;
    private String message;
    private T data;

    public ServiceResult() {

    }

    public ServiceResult(T data, HttpStatus status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
