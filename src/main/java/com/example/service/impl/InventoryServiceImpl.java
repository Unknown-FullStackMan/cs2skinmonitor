package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.SkinTypeEnum;
import com.example.common.dto.SkinAssetTotalInfoVo;
import com.example.entity.ProcessedOrder;
import com.example.entity.SkinItem;
import com.example.entity.Wallet;
import com.example.fegin.uu.UuApi;
import com.example.fegin.uu.dto.*;
import com.example.mapper.ProcessedOrderMapper;
import com.example.mapper.SkinItemMapper;
import com.example.mapper.WalletMapper;
import com.example.service.InventoryService;
import com.example.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Simple.Mu
 * @Date 2025/3/22 18:00
 * @Description
 */
@Slf4j
@Service
public class InventoryServiceImpl extends ServiceImpl<SkinItemMapper,SkinItem> implements InventoryService {

    @Autowired
    private UuApi uuApi;

    @Autowired
    private WalletService walletService;
    @Autowired
    private ProcessedOrderMapper processedOrderMapper;
    @Autowired
    private WalletMapper walletMapper;

    @Override
    @Transactional
    public void initLocal() {
        BaseResponse<InventoryResp> inventoryResponse = uuApi.list(new InventoryReq(1000));
        InventoryResp inventory = inventoryResponse.getData();
        log.info("库存数量:{} ,页数数量：{}", inventory.getTotalCount(),inventory.getTotalPages());
        Double currentCost = saveToDB(inventory);

//        if(inventory.getTotalPages() > 1 ) {
//            int pages = ( inventory.getItemCount() % 50 )==0 ?  inventory.getItemCount() : (inventory.getItemCount() / 50) + 1;
//            for (int i = 0; i < pages -1 ; i++) {
//                InventoryRequest inventoryRequest = new InventoryRequest(50);
//                inventoryRequest.setPageIndex(i+2);
//                saveToDB(uuApi.list(inventoryRequest).getData());
//            }
//        }
        Wallet wallet = new Wallet();
        wallet.setBalance(uuApi.account(new AccountInfoReq()).getData().getBalance());
        wallet.setCost(new BigDecimal(currentCost).add(new BigDecimal(wallet.getBalance())).toString());
        wallet.setCurrentCost(String.valueOf(currentCost));
        walletService.initWallet(wallet);

        //借用ProcessedOrder表,记录初始化的时间,避免定时任务拉去订单数据重复
        //TODO 换成redis
        ProcessedOrder order = processedOrderMapper.selectById(1);
        order.setTimestamp(System.currentTimeMillis());
        processedOrderMapper.updateById(order);
    }

    public Double saveToDB(InventoryResp inventory) {
        log.info("save inventory from uu, size={}",inventory.getItemsInfos().size());
        List<SkinItem> skinItemLists = inventory.getItemsInfos().stream().collect(Collectors.groupingBy(InventoryResp.ItemsInfo::getCommodityName))
                .entrySet()
                .stream()
                .flatMap(entry -> {
                    List<SkinItem> skinItems = new ArrayList<>();
                    List<InventoryResp.ItemsInfo> itemsInfos = entry.getValue();
                    if(SkinTypeEnum.needMerge(entry.getKey())) {
                        SkinItem skinItem = new SkinItem();
                        skinItem.setName(entry.getKey());
                        skinItem.setMerge(true);
                        skinItem.setSteamAssetId(itemsInfos.get(0).getSteamAssetId());
                        skinItem.setQuantity(itemsInfos.size());
                        skinItem.setRemainingQuantity(itemsInfos.size());
                        Optional<InventoryResp.ItemsInfo> optionalItem = itemsInfos.stream().filter(e -> Objects.nonNull(e.getAssetBuyPrice())).findFirst();
                        BigDecimal purchasePrice = optionalItem.map(e -> BigDecimal.valueOf(e.getAssetBuyPrice()).multiply(new BigDecimal(skinItem.getQuantity()))).orElse(BigDecimal.ZERO);
                        skinItem.setPurchasePrice(purchasePrice.toString());
                        skinItem.setPurchaseAvgPrice(optionalItem.map(e -> BigDecimal.valueOf(e.getAssetBuyPrice()).toString()).orElse("0"));
                        skinItems.add(skinItem);
                        return skinItems.stream();
                    }else {
                        for (InventoryResp.ItemsInfo itemsInfo : itemsInfos) {
                            SkinItem skinItem = new SkinItem();
                            skinItem.setName(itemsInfo.getCommodityName());
                            skinItem.setMerge(false);
                            skinItem.setAbrade(itemsInfo.getAbrade());
                            skinItem.setSteamAssetId(itemsInfo.getSteamAssetId());
                            skinItem.setQuantity(1);
                            skinItem.setRemainingQuantity(1);
                            skinItem.setPurchasePrice(Objects.isNull(itemsInfo.getAssetBuyPrice()) ? "0" : String.valueOf(itemsInfo.getAssetBuyPrice()));
                            skinItem.setPurchaseAvgPrice(skinItem.getPurchasePrice());
                            skinItems.add(skinItem);
                        }
                        return skinItems.stream();
                    }
                }).collect(Collectors.toList());
        saveBatch(skinItemLists);
        log.info("init skin items to local db finish!");
        return skinItemLists.stream().filter(e-> StringUtils.hasText(e.getPurchasePrice())).mapToDouble(e -> Double.parseDouble(e.getPurchasePrice())).sum();
    }




    @Override
    public String valuation() {
        BaseResponse<PcInventoryResp> inventoryResponse = uuApi.listPc(new PcInventoryReq(1000));
        return inventoryResponse.getData().getValuation();
    }

    @Override
    public SkinAssetTotalInfoVo assetAllInfo() {
        SkinAssetTotalInfoVo skinAssetTotalInfoVo = new SkinAssetTotalInfoVo();
        Wallet wallet = walletMapper.selectById(1);
        String valuation = uuApi.listPc(new PcInventoryReq(1000)).getData().getValuation();
        valuation.substring(1);
        skinAssetTotalInfoVo.setMarketValue(valuation.substring(1));
        skinAssetTotalInfoVo.setCost(wallet.getCost());
        skinAssetTotalInfoVo.setBalance(wallet.getBalance());
        //计算总盈亏：估值+余额-总成本
        skinAssetTotalInfoVo.setTotalProFitAndLoss(new BigDecimal(wallet.getBalance()).add(new BigDecimal(valuation.substring(1))).subtract(new BigDecimal(wallet.getCost())).doubleValue());
        return skinAssetTotalInfoVo;
    }
}
