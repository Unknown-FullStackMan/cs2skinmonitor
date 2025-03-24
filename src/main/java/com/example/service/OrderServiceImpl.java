package com.example.service;

import com.example.common.SkinTypeEnum;
import com.example.entity.ProcessedOrder;
import com.example.entity.SkinItem;
import com.example.entity.Wallet;
import com.example.fegin.uu.UuApi;
import com.example.fegin.uu.dto.OrderDetailReq;
import com.example.fegin.uu.dto.OrderDetailResp;
import com.example.fegin.uu.dto.QueryOrderListReq;
import com.example.fegin.uu.dto.OrderListResp;
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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        OrderListResp resp = uuApi.buyList(new QueryOrderListReq(50)).getData();
        //获取今日凌晨时间戳
        LocalDateTime midnight = LocalDate.now().atStartOfDay();
        long timestamp = midnight.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        ProcessedOrder order = processedOrderMapper.selectById(1);
        Long init = order.getTimestamp();
        if(init == null) {
            log.info("项目没有初始化本地库存,不进行购买订单的处理,请先初始化");
            return;
        }
        List<String> lastOrderList = resp.getOrderList().stream().filter(e -> e.getFinishOrderTime() >= timestamp && e.getFinishOrderTime() > init).map(OrderListResp.Order::getOrderNo).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(lastOrderList)) {
            log.info("今日没有购买订单");
            return;
        }

        log.info("今日新增购买订单:{}", lastOrderList);
        ProcessedOrder existProcessOrder = processedOrderMapper.selectByDate(timestamp);
        if(existProcessOrder != null && StringUtils.hasText(existProcessOrder.getBuyOrderNoList())) {
            Gson gson = new Gson();
            List<String> orderNoList = gson.fromJson(existProcessOrder.getBuyOrderNoList(), new TypeToken<List<String>>(){}.getType());
            //获取未处理订单
            lastOrderList.removeAll(orderNoList);
            log.info("本次处理订单:{}", lastOrderList);
            if(CollectionUtils.isEmpty(lastOrderList)) {
                log.info("今日已经处理完购买订单");
                return;
            }
            List<String> alreadyHandlerList = handlerBuyOrder(lastOrderList);
            //新增已处理的订单
            orderNoList.addAll(alreadyHandlerList);
            updateConsumerOrder(existProcessOrder, orderNoList,1);
        }else {
            log.info("第一次出来当天订单:{}", lastOrderList);
            List<String> alreadyHandlerList = handlerBuyOrder(lastOrderList);
            recordConsumerOrder(timestamp,alreadyHandlerList,1);
        }

    }

    private List<String> handlerBuyOrder(List<String> OrderList) {
        List<String> alreadyHandlerList = new ArrayList<>(OrderList);
        for (String orderNo : OrderList) {
            OrderDetailResp detailResp;
            try{
                detailResp = uuApi.orderDetail(new OrderDetailReq(orderNo)).getData();
            }catch (Exception e) {
                log.error("订单详情获取失败:{}",orderNo);
                break;
            }
            updateWalletWhenBuy(detailResp);
            updateInventoryWhenBuy(detailResp);
            alreadyHandlerList.add(orderNo);
        }
       return alreadyHandlerList;
    }

    public void updateWalletWhenBuy(OrderDetailResp detailResp) {
        Wallet myWallet = walletMapper.selectById(1);
        log.info("支付方式={},金额={}",detailResp.getPaymentTypeVO().getName(),detailResp.getAmount().getAmount());
        if(!"余额".equalsIgnoreCase(detailResp.getPaymentTypeVO().getName())) {
            log.info("不是余额支付，总成本和当前成本都需要增加");
            myWallet.setCurrentCost(String.valueOf(new BigDecimal(myWallet.getCurrentCost()).add(new BigDecimal(detailResp.getAmount().getAmount()))));
            myWallet.setCost(String.valueOf(new BigDecimal(myWallet.getCost()).add(new BigDecimal(detailResp.getAmount().getAmount()))));
            walletMapper.updateById(myWallet);
        }else {
            log.info("余额支付，需要减去balance,总成本不变,当前成本增加");
            myWallet.setBalance(String.valueOf(new BigDecimal(myWallet.getBalance()).subtract(new BigDecimal(detailResp.getAmount().getAmount()))));
            myWallet.setCurrentCost(String.valueOf(new BigDecimal(myWallet.getCurrentCost()).add(new BigDecimal(detailResp.getAmount().getAmount()))));
            walletMapper.updateById(myWallet);
        }
    }

    public void updateInventoryWhenBuy(OrderDetailResp detailResp) {
        String commodityName = detailResp.getUserCommodityVOList().get(0).getCommodityVOList().get(0).getName();
        if(SkinTypeEnum.needMerge(commodityName)) {
            SkinItem existSkinItem = skinItemMapper.selectBySkinName(commodityName);
            if(existSkinItem != null) {
                existSkinItem.setPurchasePrice(String.valueOf((new BigDecimal(existSkinItem.getPurchasePrice()).add(new BigDecimal(detailResp.getAmount().getAmount())))));
                existSkinItem.setQuantity(existSkinItem.getQuantity() + detailResp.getCommodityNum() - detailResp.getOrderFailNum() );
                existSkinItem.setPurchaseAvgPrice(new BigDecimal(existSkinItem.getPurchasePrice()).divide(new BigDecimal(existSkinItem.getQuantity()), 2, RoundingMode.HALF_UP).toString());
                existSkinItem.setRemainingQuantity(existSkinItem.getRemainingQuantity() + detailResp.getCommodityNum() - detailResp.getOrderFailNum());
                skinItemMapper.updateById(existSkinItem);
            }else {
                SkinItem skinItem = new SkinItem();
                skinItem.setMerge(true);
                skinItem.setName(commodityName);
                skinItem.setQuantity(detailResp.getCommodityNum() - detailResp.getOrderFailNum());
                skinItem.setPurchasePrice(detailResp.getAmount().getAmount());
                skinItem.setPurchaseAvgPrice(new BigDecimal(skinItem.getPurchasePrice()).divide(new BigDecimal(skinItem.getQuantity()), 2, RoundingMode.HALF_UP).toString());
                skinItemMapper.insert(skinItem);
            }
        }else {
            List<SkinItem> list = new ArrayList<>();
            for (OrderDetailResp.CommodityVO commodityVO : detailResp.getUserCommodityVOList().get(0).getCommodityVOList()) {
                SkinItem skinItem = new SkinItem();
                skinItem.setMerge(false);
                skinItem.setName(commodityVO.getName());
                skinItem.setQuantity(1);
                skinItem.setAbrade(commodityVO.getAbrade());
                skinItem.setPurchasePrice(commodityVO.getPrice());
                skinItem.setPurchaseAvgPrice(commodityVO.getPrice());
                list.add(skinItem);
            }
            skinItemMapper.insertBatch(list);
        }
    }

    public void recordConsumerOrder(Long timestamp, List<String> lastOrderList,int orderType) {
        ProcessedOrder processedOrderNew = new ProcessedOrder();
        if(orderType == 1){
            processedOrderNew.setBuyOrderNoList(new Gson().toJson(lastOrderList));
        }else {
            processedOrderNew.setSaleOrderNoList(new Gson().toJson(lastOrderList));
        }
        processedOrderNew.setTimestamp(timestamp);
        processedOrderMapper.insert(processedOrderNew);
    }

    public void updateConsumerOrder(ProcessedOrder existProcessOrder,List<String> lastOrderList,int orderType) {
        //1是购买订单
        if(orderType == 1){
            existProcessOrder.setBuyOrderNoList(new Gson().toJson(lastOrderList));
        }else {
            //出售订单
            existProcessOrder.setSaleOrderNoList(new Gson().toJson(lastOrderList));
        }
        processedOrderMapper.updateById(existProcessOrder);
    }

    @Override
    public void saleOrderHandler() {
        OrderListResp resp = uuApi.sellList(new QueryOrderListReq(100)).getData();
        //获取今日凌晨时间戳
        LocalDateTime midnight = LocalDate.now().atStartOfDay();
        long timestamp = midnight.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        ProcessedOrder order = processedOrderMapper.selectById(1);
        Long init = order.getTimestamp();
        if(init == null) {
            log.info("项目没有初始化本地库存,不进行购买订单的处理,请先初始化");
            return;
        }
        List<String> lastOrderList = resp.getOrderList().stream().filter(e -> e.getFinishOrderTime() >= timestamp && e.getFinishOrderTime() > init).map(OrderListResp.Order::getOrderNo).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(lastOrderList)) {
            log.info("今日没有出售订单");
            return;
        }

        log.info("今日新增出售订单:{}", lastOrderList);
        ProcessedOrder existProcessOrder = processedOrderMapper.selectByDate(timestamp);
        if(existProcessOrder != null){
            Gson gson = new Gson();
            List<String> orderNoList = gson.fromJson(existProcessOrder.getSaleOrderNoList(), new TypeToken<List<String>>(){}.getType());
            List<String> handlerList = new ArrayList<>(lastOrderList);
            //获取未处理订单
            lastOrderList.removeAll(orderNoList);
            log.info("本次处理订单:{}", lastOrderList);
            for (String orderNo : lastOrderList) {
                OrderDetailResp detailResp = uuApi.orderDetail(new OrderDetailReq(orderNo)).getData();
                updateWalletWhenSale(detailResp);
                updateInventoryWhenSale(detailResp);
            }
            updateConsumerOrder(existProcessOrder, handlerList,2);
        }else{
            for (String orderNo : lastOrderList) {
                OrderDetailResp detailResp = uuApi.orderDetail(new OrderDetailReq(orderNo)).getData();
                updateWalletWhenSale(detailResp);
                updateInventoryWhenSale(detailResp);
            }
            recordConsumerOrder(timestamp,lastOrderList,2);
        }

    }

    public void updateWalletWhenSale(OrderDetailResp detailResp) {
        Wallet myWallet = walletMapper.selectById(1);
        log.info("出售金额转入余额");
        log.info("余额balance增加,总成本不变,当前成本减少");
        log.info("orderId={},出售金额={}",detailResp.getOrderNumber(),detailResp.getAmount().getAmount());
        myWallet.setBalance(String.valueOf(new BigDecimal(myWallet.getBalance()).add(new BigDecimal(detailResp.getAmount().getAmount()))));
        myWallet.setCurrentCost(String.valueOf(new BigDecimal(myWallet.getCurrentCost()).subtract(new BigDecimal(detailResp.getAmount().getAmount()))));
        walletMapper.updateById(myWallet);

    }

    public void updateInventoryWhenSale(OrderDetailResp detailResp) {
        String commodityName = detailResp.getUserCommodityVOList().get(0).getCommodityVOList().get(0).getName();
        if(SkinTypeEnum.needMerge(commodityName)) {
            SkinItem existSkinItem = skinItemMapper.selectBySkinName(commodityName);
            existSkinItem.setSaleQuantity(existSkinItem.getSaleQuantity() + detailResp.getCommodityNum() - detailResp.getOrderFailNum());
            existSkinItem.setSalePrice(StringUtils.hasText(existSkinItem.getSalePrice()) ?
                    new BigDecimal(existSkinItem.getSalePrice()).add(new BigDecimal(detailResp.getAmount().getAmount())).toString() : detailResp.getAmount().getAmount());
            existSkinItem.setRemainingQuantity(existSkinItem.getQuantity() - existSkinItem.getSaleQuantity());
            existSkinItem.setSaleAvgPrice(new BigDecimal(existSkinItem.getSalePrice()).divide(new BigDecimal(existSkinItem.getSaleQuantity()), 2, RoundingMode.HALF_UP).toString());
            if(existSkinItem.getRemainingQuantity() == 0) {
                skinItemMapper.updateById(existSkinItem);
                skinItemMapper.deleteById(existSkinItem);
            }
            skinItemMapper.updateById(existSkinItem);
        }else {
            for (OrderDetailResp.CommodityVO commodityVO : detailResp.getUserCommodityVOList().get(0).getCommodityVOList()) {
                SkinItem existSkinItem = skinItemMapper.selectBySkinNameAndAbrade(commodityName,commodityVO.getAbrade());
                existSkinItem.setRemainingQuantity(0);
                existSkinItem.setSaleQuantity(1);
                existSkinItem.setSalePrice(commodityVO.getPrice());
                existSkinItem.setSaleAvgPrice(commodityVO.getPrice());
                skinItemMapper.updateById(existSkinItem);
                skinItemMapper.deleteById(existSkinItem);
                //TODO 优化：批量更新
            }
        }
    }
}
