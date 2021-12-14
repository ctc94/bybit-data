package com.bybit.schedule;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
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
	
	//@Scheduled(fixedRate = 2000, initialDelayString = "5000")
	public void checkSession() throws DeploymentException, IOException {
		
		log.info(">>>>>>>> checkSession");
		
		if (Client.session == null || !Client.session.isOpen()) {
			log.info("session is connetting..");
			runWebsocket();
		}
	}
	
	private void runWebsocket() throws DeploymentException, IOException {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		String uri = "wss://stream.bybit.com/realtime_public";
		
		container.connectToServer(new BybitWebsocketHandler(template,templateKafka), URI.create(uri));
		// Client.session.getBasicRemote().sendText("{\"op\":\"ping\"}");
		// Client.session.getBasicRemote().sendText(Client.getAuthMessage());
		// Client.session.getBasicRemote().sendText(Client.subscribe("subscribe",
		// "trade.BTCUSD"));
		Client.session.setMaxIdleTimeout(2000);
		Client.session.getBasicRemote()
				.sendText(Client.subscribe("subscribe", "instrument_info.100ms.BTCUSDT")); 
		//Client.session.getBasicRemote().sendText(Client.subscribe("subscribe", "klineV2.1.BTCUSD"));
		//Client.session.getBasicRemote().sendText(Client.subscribe("subscribe", "candle.5.BTCUSDT"));
		//Client.session.getBasicRemote().sendText(Client.subscribe("subscribe", "candle.5.BTCUSDT"));
		//Client.session.getBasicRemote().sendText(Client.subscribe("subscribe", "candle.30.BTCUSDT"));
		//Client.session.getBasicRemote().sendText(Client.subscribe("subscribe", "candle.60.BTCUSDT"));
		//Client.session.getBasicRemote().sendText(Client.subscribe("subscribe", "candle.240.BTCUSDT"));
		//Client.session.getBasicRemote().sendText(Client.subscribe("subscribe", "candle.D.BTCUSDT"));
	}
	
}
