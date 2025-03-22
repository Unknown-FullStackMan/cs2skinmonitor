package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.SkinTypeEnum;
import com.example.entity.SkinItem;
import com.example.fegin.uu.UuApi;
import com.example.fegin.uu.dto.BaseResponse;
import com.example.fegin.uu.dto.InventoryRequest;
import com.example.fegin.uu.dto.InventoryResponse;
import com.example.mapper.SkinItemMapper;
import com.example.service.PullInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class PullInventoryServiceImpl extends ServiceImpl<SkinItemMapper,SkinItem> implements PullInventoryService {

    @Autowired
    private UuApi uuApi;

    @Override
    @Transactional
    public void pullInventory() {
        BaseResponse<InventoryResponse> inventoryResponse = uuApi.list(new InventoryRequest(1000));
        InventoryResponse inventory = inventoryResponse.getData();
        log.info("库存数量:{} ,页数数量：{}", inventory.getTotalCount(),inventory.getTotalPages());
        saveToDB(inventory);

//        if(inventory.getTotalPages() > 1 ) {
//            int pages = ( inventory.getItemCount() % 50 )==0 ?  inventory.getItemCount() : (inventory.getItemCount() / 50) + 1;
//            for (int i = 0; i < pages -1 ; i++) {
//                InventoryRequest inventoryRequest = new InventoryRequest(50);
//                inventoryRequest.setPageIndex(i+2);
//                saveToDB(uuApi.list(inventoryRequest).getData());
//            }
//        }
    }

    public void saveToDB(InventoryResponse inventory) {
        log.info("save inventory from uu, size={}",inventory.getItemsInfos().size());
        List<SkinItem> skinItemLists = inventory.getItemsInfos().stream().collect(Collectors.groupingBy(InventoryResponse.ItemsInfo::getCommodityName))
                .entrySet()
                .stream()
                .flatMap(entry -> {
                    List<SkinItem> skinItems = new ArrayList<>();
                    List<InventoryResponse.ItemsInfo> itemsInfos = entry.getValue();
                    if(needMerge(entry.getKey())) {
                        SkinItem skinItem = new SkinItem();
                        skinItem.setName(entry.getKey());
                        skinItem.setMerge(true);
                        skinItem.setSteamAssetId(itemsInfos.get(0).getSteamAssetId());
                        skinItem.setQuantity(itemsInfos.size());
                        Optional<InventoryResponse.ItemsInfo> optionalItem = itemsInfos.stream().filter(e -> Objects.nonNull(e.getAssetBuyPrice())).findFirst();
                        BigDecimal purchasePrice = optionalItem.map(e -> BigDecimal.valueOf(e.getAssetBuyPrice()).multiply(new BigDecimal(skinItem.getQuantity()))).orElse(BigDecimal.ZERO);
                        skinItem.setPurchasePrice(purchasePrice.toString());
                        skinItems.add(skinItem);
                        return skinItems.stream();
                    }else {
                        for (InventoryResponse.ItemsInfo itemsInfo : itemsInfos) {
                            SkinItem skinItem = new SkinItem();
                            skinItem.setName(itemsInfo.getCommodityName());
                            skinItem.setMerge(false);
                            skinItem.setAbrade(itemsInfo.getAbrade());
                            skinItem.setSteamAssetId(itemsInfo.getSteamAssetId());
                            skinItem.setQuantity(1);
                            skinItem.setPurchasePrice(Objects.isNull(itemsInfo.getAssetBuyPrice()) ? "" : String.valueOf(itemsInfo.getAssetBuyPrice()));
                            skinItems.add(skinItem);
                        }
                        return skinItems.stream();
                    }
                }).collect(Collectors.toList());

        saveBatch(skinItemLists);
    }

    public boolean needMerge(String name) {
        return SkinTypeEnum.noAbradeSkinList().stream().anyMatch(name::contains);
    }


}
