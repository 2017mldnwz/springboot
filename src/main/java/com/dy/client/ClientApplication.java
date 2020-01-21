package com.dy.client;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dy.client.dao")
public class ClientApplication{
	
	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

}