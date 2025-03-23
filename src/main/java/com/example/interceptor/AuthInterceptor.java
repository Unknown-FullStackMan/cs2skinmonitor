package com.example.interceptor;

import java.io.IOException;

import com.example.mapper.AuthMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;

import lombok.Setter;
import okhttp3.Response;
import okhttp3.Request;

@Component
@Setter
@Slf4j
public class AuthInterceptor extends BasePathMatchInterceptor{

    @Autowired
    private AuthMapper authMapper;

    @Override
    public Response doIntercept(Chain chain) throws IOException {
      Request request = chain.request();
      String authorization = "";
      if(request.url().toString().contains("youpin")) {
          authorization =  authMapper.selectById(1).getAuthorization();
      }

      Request newReq = request.newBuilder()
              .addHeader("Authorization", authorization)
              .build();
      return chain.proceed(newReq);
   }
}
