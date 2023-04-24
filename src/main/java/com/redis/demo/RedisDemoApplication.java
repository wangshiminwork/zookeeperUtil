package com.redis.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class RedisDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(RedisDemoApplication.class, args);
        ZKOperateMain redisOperateMain = run.getBean("ZKOperateMain", ZKOperateMain.class);
        redisOperateMain.run(args);
        System.exit(SpringApplication
                .exit(run));
    }

    @Bean
    public ExitCodeGenerator exitCodeGenerator() {
        return () -> 1;
    }
}
