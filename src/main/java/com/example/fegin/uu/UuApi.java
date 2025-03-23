package com.example.fegin.uu;

import com.example.interceptor.ResponseInterceptor;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.Intercept;
import com.example.interceptor.AuthInterceptor;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.example.fegin.uu.dto.*;

@RetrofitClient(baseUrl = "${uu.baseUrl}")
@Intercept(handler = AuthInterceptor.class)
@Intercept(handler = ResponseInterceptor.class)
public interface UuApi {

    @POST("/api/youpin/commodity/user/inventory/price/trend")
    BaseResponse<InventoryResp> list(@Body InventoryReq inventoryReq);

    @POST("/api/youpin/pc/inventory/list")
    BaseResponse<PcInventoryResp> listPc(@Body PcInventoryReq inventoryRequest);

    @POST("/api/youpin/bff/payment/v1/user/account/info")
    BaseResponse<AccountInfoResp> account(@Body AccountInfoReq accountInfoReq);

    @POST("/api/youpin/bff/trade/sale/v1/buy/list")
    BaseResponse<OrderListResp> buyList(@Body QueryOrderListReq queryOrderListReq);

    @POST("/api/youpin/bff/trade/sale/v1/sell/list")
    @Headers({"Host: api.youpin898.com","App-Version: 5.29.0"})
    BaseResponse<OrderListResp> sellList(@Body QueryOrderListReq queryOrderListReq);

    @POST("/api/youpin/bff/trade/v1/order/query/detail")
    @Headers({"Host: api.youpin898.com","App-Version: 5.29.0"})
    BaseResponse<OrderDetailResp> orderDetail(@Body OrderDetailReq orderDetailReq);


}
