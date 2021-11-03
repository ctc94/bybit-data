package com.bybit;

import java.net.URI;
import java.util.Arrays;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;

import com.bybit.websocket.client.BybitWebsocketHandler;
import com.bybit.websocket.client.Client;
import com.bybit.zzz.MyStompSessionHandler;

@SpringBootApplication
public class BybitDataApplication {

	private static final Logger log = LoggerFactory.getLogger(BybitDataApplication.class);
	
	@Autowired
	RedisTemplate<String, Object> template;

	public static void main(String[] args) {

		SpringApplication.run(BybitDataApplication.class, args);
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
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {

		return args -> {
//			log.info("Let's inspect the beans provided by Spring Boot:");

//			String[] beanNames = ctx.getBeanDefinitionNames();
//			Arrays.sort(beanNames);
//			for (String beanName : beanNames) {
//				// log.info(beanName);
//			}
			try {
				WebSocketContainer container = ContainerProvider.getWebSocketContainer();
				String uri = "wss://stream.bybit.com/realtime";
//	            String uri = "wss://stream.bytick.com/realtime";
//	            String uri = "wss://stream-testnet.bybit-cn.com/realtime";
//	            String uri = "wss://stream-testnet.bybit.com/realtime";
				// session.getBasicRemote().sendText(subscribe("subscribe", "order"));
				java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

				while (true) {
					String line = r.readLine();
					log.info("===> " + line);

					if (line.equals("quit")) {
						Client.session.close();
						break;
					}

					if (line.equals("close")) {
						Client.session.close();
					}

					if (line.equals("start")) {
						container.connectToServer(new BybitWebsocketHandler(template), URI.create(uri));
						// Client.session.getBasicRemote().sendText("{\"op\":\"ping\"}");
						// Client.session.getBasicRemote().sendText(Client.getAuthMessage());
//	                    Client.session.getBasicRemote().sendText(Client.subscribe("subscribe", "instrument_info.100ms.BTCUSD"));
						//Client.session.getBasicRemote().sendText(Client.subscribe("subscribe", "klineV2.1.BTCUSD"));
						Client.session.getBasicRemote().sendText(Client.subscribe("subscribe", "klineV2.5.BTCUSD"));
						// Client.session.getBasicRemote().sendText(Client.subscribe("subscribe",
						// "trade.BTCUSD"));
					}
				}

				System.exit(0);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		};
	}

}
