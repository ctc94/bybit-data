package com.bybit.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

//@Configuration
//@EnableKafka
public class KafkaConfig {

	public class Listener {

//		@KafkaListener(id = "listen1", topicPartitions =
//	        { @org.springframework.kafka.annotation.TopicPartition(topic = "instrument_info.100ms.BTCUSDT", partitions = { "0-100"},
//            partitionOffsets = @org.springframework.kafka.annotation.PartitionOffset(partition = "*", initialOffset = "0"))
//       })
		@KafkaListener(id = "listen1",topics="instrument_info.100ms.BTCUSDT")
		public void listen1(@Payload String in,
		        @Header(name = KafkaHeaders.RECEIVED_MESSAGE_KEY, required = false) String key, 
		        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
		        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, 
		        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts,
		        @Header(KafkaHeaders.OFFSET) String offset
				) {
			System.out.println("@Payload : " + in);
			System.out.println("key : " + key);
			System.out.println("partition : " + partition);
			System.out.println("topic : " + topic);
			System.out.println("offset : " + offset);
			System.out.println("ts : " + ts);
		}

	}

	@Bean
	public Listener listener() {
		return new Listener();
	}

	@Bean
	public NewTopic topic() {
		return TopicBuilder.name("instrument_info.100ms.BTCUSDT").partitions(5).replicas(1).build();
	}
}
