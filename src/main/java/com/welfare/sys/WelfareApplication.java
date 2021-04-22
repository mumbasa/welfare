package com.welfare.sys;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@ComponentScan("com.welfare.*")
public class WelfareApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {

		SpringApplication.run(WelfareApplication.class, args);

	}
	

@PostConstruct
    public void init(){
        ApiContextInitializer.init();
    }
	 @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	        return builder.sources(WelfareApplication.class);
	    }

		@Bean
		public FileStorageProperties getFileStorage() {

			return new FileStorageProperties();

		}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}
