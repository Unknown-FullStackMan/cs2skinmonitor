package com.example.fegin.uu;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.Intercept;
import com.example.interceptor.AuthInterceptor;

import retrofit2.http.Body;
import retrofit2.http.POST;
import com.example.fegin.uu.dto.*;

@RetrofitClient(baseUrl = "${uu.baseUrl}")
@Intercept(handler  = AuthInterceptor.class)
public interface UuApi {

    @POST("/api/youpin/pc/inventory/list")
    BaseResponse<InventoryResponse> list(@Body InventoryRequest inventoryRequest);
}
