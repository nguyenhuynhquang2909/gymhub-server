package com.gymhub.gymhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@SpringBootApplication
@RestController
public class GymhubApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(GymhubApplication.class, args);
	}

	@GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello, %s!", name);
    }

    @GetMapping("/sum")
    public String sum(@RequestParam(value = "a") int a, @RequestParam(value = "b") int b) {
        int sum = a + b;
        return String.format("The sum of %d and %d is %d", a, b, sum);
    }

    @GetMapping("/square")
    public String square(@RequestParam(value = "number") int number) {
        int square = number * number;
        return String.format("The square of %d is %d", number, square);
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("The application is running smoothly.");
    }

}
