package com.example.fegin.uu.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class BaseResponse<T> implements Serializable {

    private int code = 0;
    private String msg = "成功";
    private String timestamp ;
    private T data;

    public boolean isSuccess() {
        return code == 0 || msg.equals("成功");
    }

    
}
