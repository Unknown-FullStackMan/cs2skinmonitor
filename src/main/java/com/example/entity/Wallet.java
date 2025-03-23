package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 12:25
 * @Description
 */
@Data
@TableName("wallet")
public class Wallet {

    private Long id;

    /**
     * 成本
     */
    private String cost;

    /**
     * 当前市值
     */
    private String marketValue;

    /**
     * 余额
     */
    private String balance;

    /**
     * 盈亏
     */
    private String totalProfitAndLoss;

    /**
     * 日盈亏
     */
    private String dailyProfitAndLoss;
}
