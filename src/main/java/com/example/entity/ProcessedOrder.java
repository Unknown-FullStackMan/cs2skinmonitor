package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/**
 * @Author Simple.Mu
 * @Date 2025/3/23 14:59
 * @Description
 */
@Data
@TableName("processed_order")
public class ProcessedOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long timestamp;

    private String orderNoList;


}
