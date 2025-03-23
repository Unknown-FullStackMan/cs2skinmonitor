package com.example.fegin.uu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 12:59
 * @Description
 */
@Data
@NoArgsConstructor
public class PcInventoryReq extends BaseRequest {
    //饰品关键词
    private String commodityName;

    public PcInventoryReq(int pageSize) {
        super.setPageSize(pageSize);
    }
}
