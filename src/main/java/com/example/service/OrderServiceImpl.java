package com.example.service;

import com.example.common.SkinTypeEnum;
import com.example.entity.ProcessedOrder;
import com.example.entity.SkinItem;
import com.example.entity.Wallet;
import com.example.fegin.uu.UuApi;
import com.example.fegin.uu.dto.BuyDetailReq;
import com.example.fegin.uu.dto.BuyDetailResp;
import com.example.fegin.uu.dto.BuyListReq;
import com.example.fegin.uu.dto.BuyListResp;
import com.example.mapper.ProcessedOrderMapper;
import com.example.mapper.SkinItemMapper;
import com.example.mapper.WalletMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 14:42
 * @Description
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UuApi uuApi;
    @Autowired
    private ProcessedOrderMapper processedOrderMapper;
    @Autowired
    private SkinItemMapper skinItemMapper;
    @Autowired
    private WalletMapper walletMapper;

    @Override
    @Transactional
    public void buyOrderHandler() {
        BuyListResp resp = uuApi.buyList(new BuyListReq(50)).getData();
        //获取今日凌晨时间戳
        LocalDateTime midnight = LocalDate.now().atStartOfDay();
        long timestamp = midnight.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        ProcessedOrder order = processedOrderMapper.selectById(1);
        Long init = order.getTimestamp();
        if(init == null) {
            log.info("项目没有初始化本地库存,不进行购买订单的处理,请先初始化");
            return;
        }
        List<String> lastOrderList = resp.getOrderList().stream().filter(e -> e.getFinishOrderTime() >= timestamp && e.getFinishOrderTime() > init).map(BuyListResp.Order::getOrderNo).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(lastOrderList)) {
            log.info("今日没有订单");
            return;
        }

        log.info("今日新增订单:{}", lastOrderList);
        ProcessedOrder existProcessOrder = processedOrderMapper.selectByDate(timestamp);
        if(existProcessOrder != null){
            Gson gson = new Gson();
            List<String> orderNoList = gson.fromJson(existProcessOrder.getOrderNoList(), new TypeToken<List<String>>(){}.getType());
            List<String> handlerList = new ArrayList<>(lastOrderList);
            //获取未处理订单
            lastOrderList.removeAll(orderNoList);
            log.info("本次处理订单:{}", lastOrderList);
            for (String orderNo : lastOrderList) {
                BuyDetailResp detailResp = uuApi.buyOrderDetail(new BuyDetailReq(orderNo)).getData();
                updateWallet(detailResp);
                updateInventory(detailResp);
            }
            updateConsumerOrder(existProcessOrder, handlerList);
        }else {

            for (String orderNo : lastOrderList) {
                BuyDetailResp detailResp = uuApi.buyOrderDetail(new BuyDetailReq(orderNo)).getData();
                updateWallet(detailResp);
                updateInventory(detailResp);
            }

            recordConsumerOrder(timestamp,lastOrderList);
        }

    }

    public void updateWallet(BuyDetailResp detailResp) {
        Wallet myWallet = walletMapper.selectById(1);
        log.info("支付方式={},金额={}",detailResp.getPaymentTypeVO().getName(),detailResp.getAmount().getAmount());
        if(!"余额".equalsIgnoreCase(detailResp.getPaymentTypeVO().getName())) {
            log.info("不是余额支付，成本需要增加");
            myWallet.setCost(String.valueOf(new BigDecimal(myWallet.getCost()).add(new BigDecimal(detailResp.getAmount().getAmount()))));
            walletMapper.updateById(myWallet);
        }else {
            log.info("余额支付，需要减去balance");
            myWallet.setBalance(String.valueOf(new BigDecimal(myWallet.getBalance()).subtract(new BigDecimal(detailResp.getAmount().getAmount()))));
            myWallet.setCost(String.valueOf(new BigDecimal(myWallet.getCost()).add(new BigDecimal(detailResp.getAmount().getAmount()))));
            walletMapper.updateById(myWallet);
        }
    }

    public void updateInventory( BuyDetailResp detailResp) {
        String commodityName = detailResp.getUserCommodityVOList().get(0).getCommodityVOList().get(0).getName();
        if(SkinTypeEnum.needMerge(commodityName)) {
            SkinItem existSkinItem = skinItemMapper.selectBySkinName(commodityName);
            if(existSkinItem != null) {
                existSkinItem.setPurchasePrice(String.valueOf((new BigDecimal(existSkinItem.getPurchasePrice()).add(new BigDecimal(detailResp.getAmount().getAmount())))));
                existSkinItem.setQuantity(existSkinItem.getQuantity() + detailResp.getCommodityNum() - detailResp.getOrderFailNum() );
                skinItemMapper.updateById(existSkinItem);
            }else {
                SkinItem skinItem = new SkinItem();
                skinItem.setMerge(true);
                skinItem.setName(commodityName);
                skinItem.setQuantity(detailResp.getCommodityNum() - detailResp.getOrderFailNum());
                skinItem.setPurchasePrice(detailResp.getAmount().getAmount());
                skinItemMapper.insert(skinItem);
            }
        }else {
            List<SkinItem> list = new ArrayList<>();
            for (BuyDetailResp.CommodityVO commodityVO : detailResp.getUserCommodityVOList().get(0).getCommodityVOList()) {
                SkinItem skinItem = new SkinItem();
                skinItem.setMerge(false);
                skinItem.setName(commodityVO.getName());
                skinItem.setQuantity(1);
                skinItem.setAbrade(commodityVO.getAbrade());
                skinItem.setPurchasePrice(commodityVO.getPrice());
                list.add(skinItem);
            }
            skinItemMapper.insertBatch(list);
        }
    }

    public void recordConsumerOrder(Long timestamp, List<String> lastOrderList) {
        ProcessedOrder processedOrderNew = new ProcessedOrder();
        processedOrderNew.setOrderNoList(new Gson().toJson(lastOrderList));
        processedOrderNew.setTimestamp(timestamp);
        processedOrderMapper.insert(processedOrderNew);
    }

    public void updateConsumerOrder(ProcessedOrder existProcessOrder,List<String> lastOrderList) {
        existProcessOrder.setOrderNoList(new Gson().toJson(lastOrderList));
        processedOrderMapper.updateById(existProcessOrder);
    }

}
