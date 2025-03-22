package com.example.controller;

import com.example.service.PullInventoryService;
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
@RequestMapping("/init")
public class InitController {

    @Autowired
    private PullInventoryService pullInventoryService;

    @GetMapping("/inventory")
    public String inventory() {
        pullInventoryService.pullInventory();
        return "init successfully!";
    }

    @GetMapping("/purchase/price")
    public void price() {
        pullInventoryService.pullInventory();
    }

}
