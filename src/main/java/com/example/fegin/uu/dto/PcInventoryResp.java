package com.example.fegin.uu.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 13:00
 * @Description
 */

@Data
public class PcInventoryResp {

    private int totalCount;

    private List<ItemsInfo> itemsInfos;

    //总估值
    private String valuation;

    private int itemCount;
    @Data
    public static class ItemsInfo {

        //资产唯一标识
        private String steamAssetId;
        private String abrade;
        private String name;

        //当前市场售价
        private Double price;
        //货币+价格
        private String priceDesc;

        private int isMerge;
        //资产数量
        private int assetMergeCount;
        //资产状态
        //0：可交易
        //1：冷却中
        //2：不可交易
        //3：上架中
        //4：交易中
        private int assetStatus;



    }

}
