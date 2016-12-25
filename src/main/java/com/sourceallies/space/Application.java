package com.sourceallies.space;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableOAuth2Sso
public class Application {
	
	@RequestMapping("/users/me")
	public @ResponseBody String getCurrentUser() {
		return "Hello";
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
