package com.example;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RetrofitScan(basePackages = "com.example.fegin.*")
public class Cs2SkinMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(Cs2SkinMonitorApplication.class, args);
	}

}
