package com.project.course.configs.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class kafkaTopicsConfig {
    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("user-added").build();
    }
    
}

