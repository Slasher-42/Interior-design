package com.ced.User.Client.Service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * Consumer-side events are read as plain strings and parsed explicitly into this service's
 * own local event classes (see com.ced.User.Client.Service.event), rather than relying on
 * Kafka type-info headers written by the producing service's own (different) classes.
 */
@Configuration
public class KafkaConsumerJsonMapper {

    @Bean
    public JsonMapper kafkaEventJsonMapper() {
        return JsonMapper.builder().build();
    }
}
