package com.bybit.redis.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubscriber implements MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageSubscriber.class);

	@Override
	public void onMessage(Message message, byte[] pattern) {
		
		//
		try {
			JSONObject jObject = new JSONObject(new String(message.getBody()));		
			JSONArray jArray = jObject.getJSONArray("data");
			
			JSONObject obj = jArray.getJSONObject(0);
			LOGGER.info(obj.toString());
			//LOGGER.info(obj.getBigDecimal("volume").toString());
		}catch (JSONException e) {
			// TODO: handle exception
		}
	}
}
