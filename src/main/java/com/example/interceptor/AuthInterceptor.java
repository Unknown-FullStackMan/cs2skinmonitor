package com.example.interceptor;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;

import lombok.Setter;
import okhttp3.Response;
import okhttp3.Request;

@Component
@Setter
public class AuthInterceptor extends BasePathMatchInterceptor{

    @Override
    public Response doIntercept(Chain chain) throws IOException {
      Request request = chain.request();
      Request newReq = request.newBuilder()
              .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlZjdjNTg0ZTFkZTA0MDJlYTcwMjEyMWNiZTVmYTc0MCIsIm5hbWVpZCI6IjE5MjQyNzQiLCJJZCI6IjE5MjQyNzQiLCJ1bmlxdWVfbmFtZSI6IuWQg-i-o-aXoOaVjCIsIk5hbWUiOiLlkIPovqPml6DmlYwiLCJ2ZXJzaW9uIjoiMXpsIiwibmJmIjoxNzQyNjI4MjQ1LCJleHAiOjE3NDM0OTIyNDUsImlzcyI6InlvdXBpbjg5OC5jb20iLCJkZXZpY2VJZCI6IjkwYmJmMTMzLWRkM2UtNDMyZC04ZDQxLTZkMmQwZDIyMWFmOCIsImF1ZCI6InVzZXIifQ.cDi3gBaUyeUerf_0tZw-HF4IdOqDfVLkFp_Ef7tRI9w")
              .build();
      return chain.proceed(newReq);
   }
}
