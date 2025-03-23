package com.example.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 13:15
 * @Description
 */
@Data
public class BResponse<T> implements Serializable {

    private int code = 200;
    private String message = "成功";
    private boolean success = true;
    private T data;

    public static <T> BResponse<T> successResult(T t) {
        BResponse<T> bResponse = new BResponse<T>();
        bResponse.data = t;
        return bResponse;
    }

    public static  BResponse successResult() {
        return new BResponse();
    }

}
