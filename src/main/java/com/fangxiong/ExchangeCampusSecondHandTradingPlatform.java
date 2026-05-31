package com.fangxiong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan //扫描Servlet、Filter、Listener,让spring支持Servlet、Filter、Listener
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ExchangeCampusSecondHandTradingPlatform {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeCampusSecondHandTradingPlatform.class, args);
	}

}
