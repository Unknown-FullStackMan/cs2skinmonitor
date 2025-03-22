package com.example.controller;

import com.example.fegin.uu.UuApi;
import com.example.fegin.uu.dto.BaseResponse;
import com.example.fegin.uu.dto.InventoryRequest;
import com.example.fegin.uu.dto.InventoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Autowired
    private UuApi uuApi;

    @GetMapping("/index")
    public String index() {
        return "Web connectivity test successful!";
    }

    @GetMapping("/test/uu")
    public BaseResponse<InventoryResponse> testUu() {
        return uuApi.list(new InventoryRequest());
    }

    
}