package com.example.fegin.uu.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class BaseRequest implements Serializable{
    private int isMerge = 0;
    private int pageIndex = 1;
    private int pageSize = 100;

}
