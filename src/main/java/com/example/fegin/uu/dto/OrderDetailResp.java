package com.example.fegin.uu.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 14:17
 * @Description
 */
@Data
public class OrderDetailResp {
    private String orderNumber;

    private PaymentTypeVo paymentTypeVO;

    private Amount amount;

    private Integer commodityNum;
    private Integer orderFailNum;

    private List<UserCommodityVO> userCommodityVOList;

    @Data
    public static class PaymentTypeVo{
        private Integer type;
        private String name;
    }

    @Data
    public static class Amount{
        private String name = "实付款";
        private String amount;
    }


    @Data
    public static class UserCommodityVO{
        private Object userVO;
        private List<CommodityVO> commodityVOList;
    }

    @Data
    public static class CommodityVO{
        private String name;
        private String price;
        private String abrade;
    }

}
