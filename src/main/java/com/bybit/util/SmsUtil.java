package com.bybit.util;

import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;

public class SmsUtil {
	
	static List<String> ret = null;
	static {
		ret = FileUtil.readFile(".api-sms");
	}

	public static String getApiKey() {
		String line = ret.get(0);
		String key[] = line.split(":");
		return key[0];
	}

	public static String getSecret() {
		String line = ret.get(0);
		String key[] = line.split(":");
		return key[1];
	}
	
	//나에게 보내기
	public static void sendMySms(String msg) {
		sendSms("01035246305",msg);
	}
	
	public static void sendSms(String from,String msg) {
		 String api_key = getApiKey();
	        String api_secret = getSecret();
	        Message coolsms = new Message(api_key, api_secret);
	        HashMap<String, String> params = new HashMap<String, String>();

	        params.put("to", "01035246305");
	        params.put("from", from);
	        params.put("type", "SMS");
	        params.put("text", msg);

	        try {
	            JSONObject obj = (JSONObject) coolsms.send(params);
	            System.out.println(obj.toString());
	        } catch (CoolsmsException e) {
	            System.out.println(e.getMessage());
	            System.out.println(e.getCode());
	        }
	}
}
