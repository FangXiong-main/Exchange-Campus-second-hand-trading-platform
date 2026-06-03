package com.exchange;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.util.TimeZone;

@ServletComponentScan //扫描Servlet、Filter、Listener,让spring支持Servlet、Filter、Listener
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ExchangeCampusSecondHandTradingPlatform {
	@PostConstruct
	void setTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
	}
	public static void main(String[] args) {
		SpringApplication.run(ExchangeCampusSecondHandTradingPlatform.class, args);
	}

}
