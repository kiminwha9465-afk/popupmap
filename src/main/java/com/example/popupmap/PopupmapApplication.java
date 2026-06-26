package com.example.popupmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PopupmapApplication {

    public static void main(String[] args) {
        SpringApplication.run(PopupmapApplication.class, args);
    }

}
