package com.ecommerce.inventoryservice.config;

import java.util.HashMap;
import java.util.Map;


import com.ecommerce.events.InventoryEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;



@Configuration
public class KafkaConfig {

	@Bean
	public ProducerFactory<String, InventoryEvent> inventoryEventProducerFactory() {
	    Map<String, Object> props = new HashMap<>();
	    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
	    props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
	    return new DefaultKafkaProducerFactory<>(props);
	}

	@Bean
	public KafkaTemplate<String, InventoryEvent> inventoryEventKafkaTemplate() {
	    return new KafkaTemplate<>(inventoryEventProducerFactory());
	}

}