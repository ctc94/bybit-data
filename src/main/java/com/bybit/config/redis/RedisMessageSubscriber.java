package com.bybit.config.redis;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bybit.schedule.ScheduledSession;
import com.bybit.websocket.client.BybitWebsocketHandler;
import com.bybit.websocket.client.Client;
import com.google.common.collect.Maps;

@Service
public class RedisMessageSubscriber implements MessageListener {

	private static final Logger log = LoggerFactory.getLogger(RedisMessageSubscriber.class);

	Map<String, Session> sessionMap = Maps.newHashMap();

	@Autowired
	RedisTemplate<String, Object> template;
	@Autowired
	KafkaTemplate<String, String> templateKafka;
	Jackson2JsonRedisSerializer<Map<String, String>> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(
			Map.class);

	@Scheduled(fixedRate = 60000, initialDelayString = "60000")
	public void checkSession() throws DeploymentException, IOException {

		log.info(">>>>>>>> checkSession");
		//데이터 받고 있는 도중에 세션이 끊겼을 때 재 접속을 위한 장치
		sessionMap.forEach((key, session) -> {
			System.out.println("key : "+key);
			System.out.println("isOpen : "+session.isOpen());

			if (!session.isOpen()) {
				this.subscribe(key);
			}
		});
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String topic = new String(pattern);
		Map<String, String> m = jackson2JsonRedisSerializer.deserialize(message.getBody());
		String symbol = m.get("symbol");

		log.info("topic : " + topic);
		log.info("symbol : " + symbol);
		String subscribe = "instrument_info.100ms." + symbol;
		if ("receive.bybit.symbol".equals(topic)) {// bybit 특정코인 데이터 받기
			this.subscribe(subscribe);
		} else if ("halt.bybit.symbol".equals(topic)) {// bybit 툭정코인 데이터 정지
			try {

				Session session = null;
				if (sessionMap.containsKey(subscribe)) {
					session = sessionMap.get(subscribe);
				}

				if (session == null || !session.isOpen()) {
					log.info("session이 없거나 이미 close 됬습니다.");
				} else {
					session.getBasicRemote()
							.sendText(Client.subscribe("unsubscribe", "instrument_info.100ms." + symbol));
					session.close();
					sessionMap.remove(subscribe);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void subscribe(String subscribe) {
		try {
			Session session = null;
			if (sessionMap.containsKey(subscribe)) {
				session = sessionMap.get(subscribe);
			}

			if (session == null || !session.isOpen()) {
				session = this.getSession(template, templateKafka);
				log.info("session이 없거나 close 됬습니다. 새로 생성됨");
				sessionMap.put(subscribe, session);
			}

			log.info("session.getId() : " + session.getId());

			session.getBasicRemote().sendText(Client.subscribe("subscribe", subscribe));
			session.setMaxIdleTimeout(30000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// vault에서 가져옴 /bybit-data/config에 세팅
	@Value("${bybit.socket.uri}")
	String bybitSocketUri;

	Session getSession(RedisTemplate<String, Object> template, KafkaTemplate<String, String> templateKafka) {
		// if (Client.session != null && Client.session.isOpen()) return;
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		// String uri = "wss://stream.bybit.com/realtime_public";
		Session session = null;
		try {
			session = container.connectToServer(new BybitWebsocketHandler(template, templateKafka),
					URI.create(bybitSocketUri));
		} catch (DeploymentException | IOException e) {
			e.printStackTrace();
		}
		return session;
	}
}
