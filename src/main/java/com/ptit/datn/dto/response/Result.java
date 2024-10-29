package com.ptit.datn.dto.response;

public class Result {
    private int responseCode;
    private String message;

    public Result() {
    }

    public Result(int responseCode, String message) {
        this.responseCode = responseCode;
        this.message = message;
    }

    public Result responseCode(int responseCode){
        this.responseCode = responseCode;
        return this;
    }

    public Result message(String message){
        this.message = message;
        return this;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
