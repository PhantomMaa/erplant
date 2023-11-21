package com.hellocorp.example.kv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication(
        scanBasePackages = {
                "com.hellocorp.example.kv"
        }
)
@Slf4j
public class KvApplication {
    public static void main(String[] args) {
        SpringApplication.run(KvApplication.class, args);
    }
}
