package com.project.course.configs.kafka;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

public class ConsumerConfig {

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put("interceptor.classes",
                "com.project.course.configs.kafka.KafkaConsumerInterceptor");

        return new DefaultKafkaConsumerFactory<>(props);
    }
}
