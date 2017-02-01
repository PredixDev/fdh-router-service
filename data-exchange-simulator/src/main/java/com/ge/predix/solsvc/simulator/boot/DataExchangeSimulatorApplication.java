package com.ge.predix.solsvc.simulator.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages={"com.ge.predix.solsvc"})
public class DataExchangeSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataExchangeSimulatorApplication.class, args);
	}
}
