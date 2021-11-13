package com.bybit.websocket.client;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;

@ClientEndpoint
public class BybitWebsocketHandler {
	private RedisTemplate<String, Object> template;
    public BybitWebsocketHandler(RedisTemplate<String, Object> template) {
    	this.template = template;
	}

	@OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
        try {
            Client.session=session;
            System.out.println(Client.session);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @OnMessage
    public void processMessage(String message) {
    	//if(!(message.indexOf("last_price")!=-1 ||  message.indexOf("klineV2")!=-1)) return;   
    	//System.out.println(message);
    	if(message.indexOf("instrument_info.100ms.BTCUSDT")!=-1){
    		this.template.convertAndSend("instrument_info.100ms.BTCUSDT",message);
    	}
    	
    	if(message.indexOf("candle.5.BTCUSDT")!=-1){
    		this.template.convertAndSend("candle.5.BTCUSDT",message);
    	}
    	
    	if(message.indexOf("candle.30.BTCUSDT")!=-1){
    		this.template.convertAndSend("candle.30.BTCUSDT",message);
    	}
    	
    	if(message.indexOf("candle.60.BTCUSDT")!=-1){
    		this.template.convertAndSend("candle.60.BTCUSDT",message);
    	}
    	
    	if(message.indexOf("candle.240.BTCUSDT")!=-1){
    		this.template.convertAndSend("candle.240.BTCUSDT",message);
    	}
    	
    	if(message.indexOf("candle.D.BTCUSDT")!=-1){
    		this.template.convertAndSend("candle.D.BTCUSDT",message);
    	}
    }

    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }


}