package com.example.service.impl;

import com.example.entity.Wallet;
import com.example.mapper.WalletMapper;
import com.example.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 13:40
 * @Description
 */
@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletMapper walletMapper;

    @Override
    public int initWallet(Wallet wallet) {
        log.info("初始化钱包");
        return walletMapper.insert(wallet);
    }
}
