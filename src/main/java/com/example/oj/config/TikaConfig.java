package com.example.oj.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TikaConfig {
    @Bean
    public Tika tika() {
        return new Tika();
    }
}