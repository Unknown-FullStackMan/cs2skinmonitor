package com.example.scheduled;

import com.example.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author Simple.Mu
 * @Date 2025/3/22 16:48
 * 获取每日购买数据
 * @Description
 */
@Component
@Slf4j
public class PullBuyOrderTask {

    @Autowired
    private OrderService orderService;
    @Scheduled(cron ="0 0 * * * *")
    public void pullBuyOrders() {
        log.info("定时任务 pullBuyOrders 执行");
        orderService.buyOrderHandler();
        log.info("定时任务 pullBuyOrders执行完成");
    }
}
