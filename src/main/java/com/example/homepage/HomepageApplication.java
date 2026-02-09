package com.example.homepage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class HomepageApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(HomepageApplication.class, args);
    }
}
