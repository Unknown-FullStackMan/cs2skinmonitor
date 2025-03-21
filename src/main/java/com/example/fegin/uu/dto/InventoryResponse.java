package com.example.fegin.uu.dto;

import java.util.List;

import lombok.Data;

@Data
public class InventoryResponse{

    //实际全部库存数量
    private int totalCount;
    //根据请求是否去重判断得到的数量
    private int itemCount;

    private List<ItemsInfo> itemsInfos;

    @Data
    public static class ItemsInfo {
        //资产唯一标识
        private String steamAssetId;
        private String name;
        private String actionLink;
        private String marketHashName;
        private int haveNameTag;
        //冷却到期描述
        private String cacheExpirationDesc;
        //稀有度
        private Rarity rarity;
        //外观
        private Rarity exterior;
        private List<Object> stickers;
        private List<Object> pendants;
        private int stickerType;
        private int hasSticker;
        private int analysis2dStatus;
        private boolean isCanAnalysis;
        private String assetRemark;
        private String imageUrl;
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
        //货币标识+价格
        private String priceDesc;
        //价格
        private double price;
        private String assetTagColor;
        private int mergeCoolingCount;
        private int mergeOnShlefCount;
        private int mergeTradingCount;
        private int mergeUnTradeCount;

        @Data
        public static class Rarity {
            private String name;
            private String color;
        }

    }

}
