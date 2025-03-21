package com.example.fegin.uu.dto;

import lombok.Data;
import java.util.List;

@Data
public class InventoryRequest extends BaseRequest {
    //饰品关键词
    private String commodityName;

    private List<String> tags;
}
