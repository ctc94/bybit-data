package com.bybit.schedule;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bybit.websocket.client.BybitWebsocketHandler;
import com.bybit.websocket.client.Client;
@Service
public class ScheduledSession {
	
	private static final Logger log = LoggerFactory.getLogger(ScheduledSession.class);
	@Autowired
	RedisTemplate<String, Object> template;
	@Autowired
	KafkaTemplate<String, String> templateKafka;
	
	@Scheduled(fixedRate = Long.MAX_VALUE)
	public void sendSms() {
	//	SmsUtil.sendMySms("안녕하세요");
	}
	
}
