package com.bybit.zzz;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MyStompSessionHandler implements StompSessionHandler {
	private Logger logger = LogManager.getLogger(MyStompSessionHandler.class);	

	@Override
	public Type getPayloadType(StompHeaders headers) {
		// TODO Auto-generated method stub
		return List.class;
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		List<Map> list = (List<Map>) payload;
		
		if(list == null) return;
		
		ObjectMapper mapper = new ObjectMapper();
		list.forEach((m) -> {
			OutputMessage msg = mapper.convertValue(m, OutputMessage.class);
			logger.info("Received : " + msg.getText() + " from : " + msg.getFrom() + " time : " + msg.getTime());
		});
	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		logger.info("afterConnected ===============>");
		logger.info("New session established : " + session.getSessionId());
	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		// TODO Auto-generated method stub
		logger.error("handleException ===============>");
		logger.error(exception.getCause());
	}

	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		// TODO Auto-generated method stub
		logger.error("handleTransportError ===============>");
		logger.error(exception.getCause());
		//exception.printStackTrace();
		
		if(exception.getMessage().equals("Connection closed")) {
			logger.error(exception.getMessage());
//			while(true) {
//				try {
//					session.
//					Thread.sleep(10000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		}
		
		
	}

}
