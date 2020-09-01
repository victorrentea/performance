package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

@RestController
class R1 {
	@GetMapping
	public String hello() throws InterruptedException {
		CheckStockEvent event = new CheckStockEvent();
		event.begin();

		int[] ints = new int[1000];
		System.out.println(ints.length);

		Thread.sleep(1000);
		if (event.isEnabled()) {
			event.commit();
		}
		return "Hello";
	}
}
