package org.oddjob.spring.config;

import org.oddjob.spring.Apple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppleConfig {

    @Value("${apple.colour}")
    private String colour;

    @Bean
    Apple apple() {
        Apple apple = new Apple();
        apple.setColour(colour);
        return apple;
    }

}
