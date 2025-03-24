package com.example.controller;

import com.example.common.dto.BResponse;
import com.example.entity.Auth;
import com.example.mapper.AuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 17:52
 * @Description
 */

@RestController
@RequestMapping("/auth/saveToken")
public class AuthController {

    @Autowired
    private AuthMapper authMapper;

    /**
     * uu jwt token可以从uu的pc页面获取
     * @param token
     * @return
     */
    @GetMapping("/uu")
    public BResponse<String> valuation(@RequestParam String token) {
        Auth auth = authMapper.selectById(1);
        auth.setAuthorization(token);
        authMapper.updateById(auth);
        return BResponse.successResult();
    }
}
