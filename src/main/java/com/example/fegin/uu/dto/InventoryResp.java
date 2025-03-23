package com.example.fegin.uu.dto;

import java.util.List;

import lombok.Data;

@Data
public class InventoryResp {

    //实际全部库存数量
    private int totalCount;
    //根据请求是否去重判断得到的数量
    private int totalPages;

    private List<ItemsInfo> itemsInfos;

    @Data
    public static class ItemsInfo {
        //资产唯一标识
        private String steamAssetId;
        private String commodityName;
        private String shortName;
        //磨损
        private String abrade;
        private Double assetBuyPrice;
        private int isMerge = 1 ;
        //资产数量
        private int assetMergeCount;
//        //资产状态
//        //0：可交易
//        //1：冷却中
//        //2：不可交易
//        //3：上架中
//        //4：交易中
//        private int assetStatus;

        //价格
        private List<ItemsInfo> assetDataList;



    }

}
