package com.example.fegin.uu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class InventoryRequest extends BaseRequest {
    //饰品关键词
    private String commodityName;

    private List<String> tags;

    public InventoryRequest(int pageSize) {
        super.setPageSize(pageSize);
    }
}
