package com.ergo.Springserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.ergo.Springserver")
@EnableScheduling
public class SpringserverApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringserverApplication.class, args);
	}

}
