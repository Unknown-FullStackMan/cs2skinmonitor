package com.example.controller;

import com.example.service.InventoryService;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Simple.Mu
 * @Date 2025/3/22 19:28
 * @Description
 */
@RestController
@RequestMapping("/skin/asset")
public class AssetHandlerController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderService orderService;

    /**
     * 项目启动后 首先初始化饰品信息到本地数据库
     * @return
     */
    @GetMapping("/init")
    public String info() {
        inventoryService.initLocal();
        return "init successfully!";
    }

    @GetMapping("/sync/buyOrder/manually")
    public String syncBuyOrder() {
        orderService.buyOrderHandler();
        return "manual sync buyOrder successfully";
    }

}
