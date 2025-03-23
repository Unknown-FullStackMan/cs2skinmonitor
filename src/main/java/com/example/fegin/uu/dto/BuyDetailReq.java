package com.example.fegin.uu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 14:16
 * @Description
 */
@Data
@NoArgsConstructor
public class BuyDetailReq extends BaseRequest{

    private String orderNo;

    public BuyDetailReq(String orderNo) {
        this.orderNo = orderNo;
    }
}
