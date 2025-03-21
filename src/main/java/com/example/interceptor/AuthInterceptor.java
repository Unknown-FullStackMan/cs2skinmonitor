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
              .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5ODY1MTQ2NTgwNDA0ZmJmOGI1ZWJhNzcyYzJiNjc0ZCIsIm5hbWVpZCI6IjE5MjQyNzQiLCJJZCI6IjE5MjQyNzQiLCJ1bmlxdWVfbmFtZSI6IuWQg-i-o-aXoOaVjCIsIk5hbWUiOiLlkIPovqPml6DmlYwiLCJ2ZXJzaW9uIjoiczlBIiwibmJmIjoxNzQyMjk3NzgwLCJleHAiOjE3NDMxNjE3ODAsImlzcyI6InlvdXBpbjg5OC5jb20iLCJkZXZpY2VJZCI6IjI1OTg3NTgzLWI1N2EtNDA3Zi05ZDI4LTQ2YWNlZDlmNGMwNCIsImF1ZCI6InVzZXIifQ.3cJoxQj16DehH0ULJ7F86huAioJhNp65kixlId2K8R8")
              .build();
      return chain.proceed(newReq);
   }
}
