package com.bybit;

import java.util.Arrays;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.bybit.zzz.MyStompSessionHandler;

@SpringBootApplication
@EnableScheduling
public class BybitDataApplication {

	private static final Logger log = LoggerFactory.getLogger(BybitDataApplication.class);

//	@Autowired
//	RedisTemplate<String, Object> template;

	private final TaskExecutor exec = new SimpleAsyncTaskExecutor();

	public static void main(String[] args) {

		SpringApplication.run(BybitDataApplication.class, args);
	}
	
	@Autowired
	Environment env;
	
	@PostConstruct
	private void postConstruct() {
		System.out.println("##########################");
		System.out.println("@PostConstruct");
		System.out.println("##########################");
		System.out.println("# spring.redis.host : " + env.getProperty("spring.redis.host"));//valut에서 가져옴
		System.out.println("# spring.redis.port : " + env.getProperty("spring.redis.port"));//valut에서 가져옴
		
	}

	/*
	 * @Component public class Bean2 implements DisposableBean {
	 * 
	 * @Override public void destroy() throws Exception {
	 * log.info("Callback triggered - DisposableBean.============");
	 * 
	 * } }
	 */

	@Bean
	public StompSessionHandler getStompSessionHandler() throws Exception {
		return new MyStompSessionHandler();
	}

	@Bean
	@Profile("default") // Don't run from test(s)
	public ApplicationRunner runner(KafkaTemplate<String, String> template) {
		return args -> {
			//this.exec.execute(() -> log.info("instrument_info.100ms.BTCUSDT"));
			//template.send("instrument_info.100ms.BTCUSDT","6112", "1112");
			//Collection<TopicPartitionOffset> requested = null;
			//ConsumerRecords<String, String> cr = template.receive(requested);
			//receiveFromKafka();
		};
	}
	
	public void receiveFromKafka() { 
        Properties props = new Properties();
        props.put("bootstrap.servers", "15.164.117.97:9092");
        props.put("group.id", "instrument_info.100ms.BTCUSDT");
        //props.put("enable.auto.commit", "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.CLIENT_DNS_LOOKUP_CONFIG, "use_all_dns_ips");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList("instrument_info.100ms.BTCUSDT")); 
        //consumer.assignment();
        

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(5000);
                boolean flag = true;
                if (flag) {
//                    consumer.seekToBeginning(
//                        Stream.of(new TopicPartition("instrument_info.100ms.BTCUSDT", 0)
//                        		,new TopicPartition("instrument_info.100ms.BTCUSDT", 1)
//                        		,new TopicPartition("instrument_info.100ms.BTCUSDT", 2)
//                        		,new TopicPartition("instrument_info.100ms.BTCUSDT", 3)
//                        		,new TopicPartition("instrument_info.100ms.BTCUSDT", 4)
//                        		,new TopicPartition("instrument_info.100ms.BTCUSDT", 5)
//                        		,new TopicPartition("instrument_info.100ms.BTCUSDT", 6)
//                        		,new TopicPartition("instrument_info.100ms.BTCUSDT", 7)
//                        		,new TopicPartition("instrument_info.100ms.BTCUSDT", 8)
//                        		,new TopicPartition("instrument_info.100ms.BTCUSDT", 9)                        		
//                        		).collect(toList()));
                	consumer.seek(new TopicPartition("instrument_info.100ms.BTCUSDT", 0), 0);
                	consumer.seek(new TopicPartition("instrument_info.100ms.BTCUSDT", 1), 0);
                	consumer.seek(new TopicPartition("instrument_info.100ms.BTCUSDT", 2), 0);
                	consumer.seek(new TopicPartition("instrument_info.100ms.BTCUSDT", 3), 0); 
                	consumer.seek(new TopicPartition("instrument_info.100ms.BTCUSDT", 4), 0);
                    flag = false;
                }
                for (ConsumerRecord<String, String> record : records) {
//                	System.out.println("aaaa"+record.value());
                    System.out.printf("Topic: %s, Partition: %s, Offset: %d, Key: %s, Value: %s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
                break;
            }
        } finally {
            consumer.close();
        }
    }

	// @Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {

		return args -> {
			this.exec.execute(() -> log.info("Hit Enter to terminate..."));
			System.in.read();
		};
	}
}
