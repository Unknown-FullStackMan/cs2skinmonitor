package com.example.service;

import com.example.common.dto.SkinAssetTotalInfoVo;

/**
 * @Author Simple.Mu
 * @Date 2025/3/22 17:59
 * @Description
 */
public interface InventoryService {

    void initLocal();

    String valuation();

    SkinAssetTotalInfoVo assetAllInfo();

}
