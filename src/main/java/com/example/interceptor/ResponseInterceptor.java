package com.example.interceptor;

import com.example.fegin.uu.dto.BaseResponse;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import com.google.gson.Gson;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @Author Simple.Mu
 * @Date 2025/3/22 19:02
 * @Description
 */
@Component
@Setter
@Slf4j
public class ResponseInterceptor extends BasePathMatchInterceptor {

    @Override
    protected Response doIntercept(Chain chain) throws IOException {
        // 获取原始请求
        Request request = chain.request();

        // 执行请求，获取响应
        Response response = chain.proceed(request);
        // 检查响应是否成功
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseBodyString = responseBody.string();
                Gson gson = new Gson();
                BaseResponse<?> content = gson.fromJson(responseBodyString, BaseResponse.class);
                // 检查业务逻辑是否成功
                if(!content.isSuccess()) {
                    log.error("请求uu接口报错: {}", responseBodyString);
                    throw new RuntimeException("请求uu接口报错");
                }else if(Objects.isNull(content.getData())){
                    log.error("请求数据为空: {}", responseBodyString);
                    throw new RuntimeException("请求数据为空");
                }
                log.info("请求uu接口成功");
                return response.newBuilder()
                        .body(ResponseBody.create(responseBody.contentType(), responseBodyString))
                        .build();
            }
        }
        return null;
    }
}
