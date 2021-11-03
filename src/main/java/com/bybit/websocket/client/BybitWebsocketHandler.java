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
    	 System.out.println(message);
    	this.template.convertAndSend("bybit-data",message);
		/*
		JSONArray jArray = jObject.getJSONArray("data");
		for (int i = 0; i < jArray.length(); i++) {
	        JSONObject obj = jArray.getJSONObject(i);
	        LOGGER.info(obj.getString("start"));
		}*/
    	
    	
		//
    }

    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }


}