package com.optimagrowth.licensingservice.configuration;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ServiceConfig {

    @Value("${redis.server}")
    private String redisServer = "";
    @Value("${redis.port}")
    private String redisPort = "";
}
