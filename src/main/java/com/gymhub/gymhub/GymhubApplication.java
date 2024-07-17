package com.gymhub.gymhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@SpringBootApplication
@RestController
public class GymhubApplication {

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to Java";
	}
	
	public static void main(String[] args) {
		SpringApplication.run(GymhubApplication.class, args);
	}

}
