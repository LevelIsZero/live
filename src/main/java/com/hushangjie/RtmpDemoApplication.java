package com.hushangjie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication/*(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})*/
//@MapperScan("com.hushangjie.dao")
public class RtmpDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(RtmpDemoApplication.class, args);
	}
}
