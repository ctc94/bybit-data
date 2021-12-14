package com.bybit.websocket.client;

import java.time.Instant;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

@ClientEndpoint
public class BybitWebsocketHandler {
	private RedisTemplate<String, Object> template;
	private KafkaTemplate<String, String> templateKafka;
	private static final Logger log = LoggerFactory.getLogger(BybitWebsocketHandler.class);
    public BybitWebsocketHandler(RedisTemplate<String, Object> template,KafkaTemplate<String, String> templateKafka) {
    	this.template = template;
    	this.templateKafka = templateKafka;
	}

	@OnOpen
    public void onOpen(Session session) {
		log.info("Connected to endpoint: " + session.getBasicRemote());
        try {
            Client.session=session;
            log.info(Client.session.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	@OnClose
    public void onClose(Session session) {
		log.info("Closeed to endpoint: " + session.getBasicRemote());
		Client.session=null;
    }

    @OnMessage
    public void processMessage(String message) {
    	//if(!(message.indexOf("last_price")!=-1 ||  message.indexOf("klineV2")!=-1)) return;   
    	//log.info(message);
    	if(message.indexOf("instrument_info.100ms.BTCUSDT")!=-1){
    		log.info("instrument_info.100ms.BTCUSDT");
    		this.template.convertAndSend("instrument_info.100ms.BTCUSDT",message);
    		this.templateKafka.send("instrument_info.100ms.BTCUSDT",Instant.now().toEpochMilli()+"",message);
    	}
    	
		/*
		 * if(message.indexOf("candle.5.BTCUSDT")!=-1){ log.info("candle.5.BTCUSDT");
		 * this.template.convertAndSend("candle.5.BTCUSDT",message); }
		 * 
		 * if(message.indexOf("candle.30.BTCUSDT")!=-1){ log.info("candle.30.BTCUSDT");
		 * this.template.convertAndSend("candle.30.BTCUSDT",message); }
		 * 
		 * if(message.indexOf("candle.60.BTCUSDT")!=-1){ log.info("candle.60.BTCUSDT");
		 * this.template.convertAndSend("candle.60.BTCUSDT",message); }
		 * 
		 * if(message.indexOf("candle.240.BTCUSDT")!=-1){
		 * log.info("candle.240.BTCUSDT");
		 * this.template.convertAndSend("candle.240.BTCUSDT",message); }
		 * 
		 * if(message.indexOf("candle.D.BTCUSDT")!=-1){ log.info("candle.D.BTCUSDT");
		 * this.template.convertAndSend("candle.D.BTCUSDT",message); }
		 */
    }

    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }

}