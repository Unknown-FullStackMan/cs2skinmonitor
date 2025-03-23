package com.example.controller;

import com.example.common.dto.BResponse;
import com.example.common.dto.SkinAssetTotalInfoVo;
import com.example.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 13:09
 * @Description
 */
@RestController
@RequestMapping("/monitor/skin")
public class MonitorController {

    @Autowired
    private InventoryService inventoryService;


    @GetMapping("/valuation")
    public BResponse<String> valuation() {
        return BResponse.successResult(inventoryService.valuation());
    }

    /**
     * 获取饰品资产详情
     * @return
     */
    @GetMapping("/total/info")
    public BResponse<SkinAssetTotalInfoVo> assetInfo() {
        return BResponse.successResult(inventoryService.assetAllInfo());
    }


}
