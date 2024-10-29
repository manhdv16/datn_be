package com.ptit.datn.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ptit.datn.constants.Constants;
import com.ptit.datn.utils.Translator;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
    private Result result;
    private T data;
    private Long totalElements;



    public CommonResponse() {
    }

    public CommonResponse<T> success(){
        this.result = new Result()
            .responseCode(Constants.HTTP_STATUS.OK)
            .message(Translator.toLocale("message.success"));
        return this;
    }

    public CommonResponse<T> result(int responseCode, String msgCode){
        this.result = new Result()
            .responseCode(responseCode)
            .message(Translator.toLocale(msgCode));
        return this;
    }

    public CommonResponse<T> data(T data){
        this.data = data;
        return this;
    }

    public CommonResponse<T> totalElements(Long totalElements){
        this.totalElements = totalElements;
        return this;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }
}
