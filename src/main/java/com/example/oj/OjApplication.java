package com.example.oj;

import com.example.oj.filesystem.FileConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@Slf4j
@EnableConfigurationProperties
public class OjApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjApplication.class, args);
        log.info("server started");
    }

}
