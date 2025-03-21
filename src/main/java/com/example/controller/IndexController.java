package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fegin.uu.UuApi;
import com.example.fegin.uu.dto.*;

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