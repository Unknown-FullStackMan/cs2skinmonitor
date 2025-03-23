package com.example.fegin.uu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 14:16
 * @Description
 */
@Data
@NoArgsConstructor
public class OrderDetailReq extends BaseRequest{

    private String orderNo;

    public OrderDetailReq(String orderNo) {
        this.orderNo = orderNo;
    }
}
