package com.ced.Auth.Security.Service.config;

import com.ced.Auth.Security.Service.event.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(KafkaTopics.USER_REGISTERED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic userVerifiedTopic() {
        return TopicBuilder.name(KafkaTopics.USER_VERIFIED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic passwordResetRequestedTopic() {
        return TopicBuilder.name(KafkaTopics.PASSWORD_RESET_REQUESTED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic authAuditLoggedTopic() {
        return TopicBuilder.name(KafkaTopics.AUTH_AUDIT_LOGGED).partitions(1).replicas(1).build();
    }
}
