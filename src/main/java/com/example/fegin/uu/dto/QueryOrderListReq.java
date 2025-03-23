package com.example.fegin.uu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 14:00
 * @Description
 */
@Data
@NoArgsConstructor
public class QueryOrderListReq extends BaseRequest{

    //购买成功码
    private int orderStatus = 340;
    //关键字搜索
    private String keys;

    public QueryOrderListReq(int pageSize) {
        super.setPageSize(pageSize);
    }
}
