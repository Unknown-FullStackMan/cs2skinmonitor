package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author Simple.Mu
 * @Date 2025/3/22 15:20
 * @Description
 */
@Data
@TableName("skin_item")
public class SkinItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    //steam上的资产Id,唯一
    private String steamAssetId;

    //磨损
    private String abrade;

    private boolean merge;

    private String purchasePrice;

    private int quantity;

    private String purchaseAvgPrice;

    private String salePrice;

    private int saleQuantity;

    private String saleAvgPrice;

    private int remainingQuantity;

    //来源
    private String from;

    @TableLogic
    private Integer deleted = 0;

}
