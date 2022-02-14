package com.bybit.config.redis;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {
	
	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,RedisMessageSubscriber redisMessageSubscriber) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		// container.addMessageListener(listenerAdapter, new PatternTopic("chart"));
		List<PatternTopic> topics = List.of(new PatternTopic("receive.bybit.symbol"),new PatternTopic("halt.bybit.symbol"));
		container.addMessageListener(messageListener(redisMessageSubscriber), topics);
		return container;
	}

	@Bean
	MessageListenerAdapter messageListener(RedisMessageSubscriber redisMessageSubscriber) {
		return new MessageListenerAdapter(redisMessageSubscriber);
	}

	@Bean
	public RedisTemplate<String, Object> template(RedisConnectionFactory connectionFactory) {
		final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(connectionFactory);
		template.setValueSerializer(new Jackson2JsonRedisSerializer(Map.class));
		return template;
	}
}
