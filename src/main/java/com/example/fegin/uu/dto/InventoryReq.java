package com.example.fegin.uu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReq extends BaseRequest {

    private List<String> tags;

    public InventoryReq(int pageSize) {
        super.setPageSize(pageSize);
    }
}
