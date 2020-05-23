package com.faiop.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.faiop.core.mapper")
@EnableScheduling
public class FaiopApplication {
    public static void main(String[] args) {
        SpringApplication.run(FaiopApplication.class, args);
    }
}
