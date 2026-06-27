package com.ced.Project.Task.Service.config;

import com.ced.Project.Task.Service.event.KafkaTopics;
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
    public NewTopic projectCreatedTopic() {
        return TopicBuilder.name(KafkaTopics.PROJECT_CREATED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic taskAssignedTopic() {
        return TopicBuilder.name(KafkaTopics.TASK_ASSIGNED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic taskCompletedTopic() {
        return TopicBuilder.name(KafkaTopics.TASK_COMPLETED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic projectCompletedTopic() {
        return TopicBuilder.name(KafkaTopics.PROJECT_COMPLETED).partitions(1).replicas(1).build();
    }
}
