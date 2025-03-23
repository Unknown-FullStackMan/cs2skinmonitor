package com.example.fegin.uu.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 14:04
 * @Description
 */
@Data
public class OrderListResp {


    private String total;

    private List<Order> orderList;

    @Data
    public static class Order {
        private String id;
        private Long orderId;
        private String orderNo;
        private Long createOrderTime;
        private Long finishOrderTime;

    }
}
