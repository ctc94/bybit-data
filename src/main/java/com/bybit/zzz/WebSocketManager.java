package com.bybit.zzz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@Component
public class WebSocketManager {

	private static final Logger log = LoggerFactory.getLogger(WebSocketManager.class);

	private StompSessionHandler sessionHandler;
	private ListenableFuture<StompSession> listenableFuture;
	private StompSession stompSession;
	private String url;
	private WebSocketStompClient stompClient;
	private Map<String, StompSessionHandler> topics = new ConcurrentHashMap<String, StompSessionHandler>();

	public WebSocketManager(StompSessionHandler sessionHandler) {
		this.sessionHandler = sessionHandler;
		List<Transport> transports = new ArrayList<>(2);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		transports.add(new RestTemplateXhrTransport());
		SockJsClient sockJsClient = new SockJsClient(transports);

		// WebSocketClient client = new StandardWebSocketClient();
		stompClient = new WebSocketStompClient(sockJsClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	}

	public WebSocketManager connect(String url) {
		log.info("connecting.... ===>");
		this.url = url;
		this.stompSession = null;
		listenableFuture = stompClient.connect(url, this.sessionHandler);

		try {
			this.stompSession = listenableFuture.get();
			log.info("connected..." + this.stompSession.getSessionId());
			
			// Create Thread 1
			Thread thread1 = new Thread(() -> {
				log.info("Check websocket connection Thread  ===> ");
				while (true) {
					try {

						Thread.sleep(10000);
						if (!this.isConnected()) {
							this.reConnect();
						}
					} catch (InterruptedException e) {
						log.info("Exiting Thread");
						throw new IllegalStateException(e);
					}
				}

			});
			thread1.start();			
			
		} catch (InterruptedException | ExecutionException e1) {
			log.error("connect failed ====>" + e1.getCause());
		}
		return this;
	}

	public void disConnect() {
		if (this.stompSession == null)
			return;
		log.info("disConnected..." + this.stompSession.getSessionId());
		if (!this.stompSession.isConnected())
			return;
		if (listenableFuture == null)
			return;
		listenableFuture.cancel(false);
		this.stompSession.disconnect();
	}

	public void subscribe(String topic) {

		if (this.topics.containsKey(topic))
			return;
		this.topics.put(topic, this.sessionHandler);

		if (this.stompSession == null)
			return;
		this.stompSession.subscribe(topic, this.sessionHandler);
	}

	public void send(String app, Message msg) {
		if (this.stompSession == null)
			return;

		this.stompSession.send(app, msg);
	}

	private void reConnect() {
		log.info("Entered reConnect ===>");
		if(this.url == null) return;
		this.connect(this.url).reSubscribes();

		// 테스팅
		this.send("/app/chart", this.getMessage());
	}

	private boolean isConnected() {
		if (this.stompSession == null)
			return false;

		return this.stompSession.isConnected();
	}

	private void reSubscribes() {

		if (this.stompSession == null)
			return;

		topics.forEach((k, v) -> {
			log.info(k);
			this.stompSession.subscribe(k, v);
		});

	}

	public Message getMessage() {
		Message aa = new Message();
		aa.setFrom("테스터");
		aa.setText("구독테스트메세지");
		return aa;
	}

}
