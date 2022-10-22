package com.optimagrowth.licensingservice;

import com.optimagrowth.licensingservice.configuration.ServiceConfig;
import com.optimagrowth.licensingservice.models.Organization;
import com.optimagrowth.licensingservice.models.OrganizationChangeModel;
import com.optimagrowth.licensingservice.services.OrganizationRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.annotation.StreamListener;
//import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;

@SpringBootApplication
@RefreshScope
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
//@EnableBinding(Sink.class)
@Slf4j
public class LicensingServiceApplication {

    @Autowired
    private ServiceConfig serviceConfig;

//    @Autowired
//    private OrganizationRedisRepository redisRepository;

    public static void main(String[] args) {
       ConfigurableApplicationContext ctx= SpringApplication.run(LicensingServiceApplication.class, args);

    }

//    @StreamListener(Sink.INPUT)
//    public void loggerSink(OrganizationChangeModel orgChange) {
//        log.debug("Received an {} event for organization id {}", orgChange.getAction(), orgChange.getOrganizationId());
//        Organization organization = redisRepository.findById(orgChange.getOrganizationId()).get();
//        organization.setUpdateToDate(false);
//        if (organization != null) {
//            redisRepository.save(organization);
//        }
//    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        String hostname = serviceConfig.getRedisServer();
        int port = Integer.parseInt(serviceConfig.getRedisPort());
        RedisStandaloneConfiguration redisStandaloneConfiguration
                = new RedisStandaloneConfiguration(hostname, port);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
