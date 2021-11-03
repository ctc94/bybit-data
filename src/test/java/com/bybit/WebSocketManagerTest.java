package com.bybit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import com.bybit.zzz.WebSocketManager;

@SpringBootTest
class WebSocketManagerTest {
	private static String URL = "http://localhost:8081/chart";
	@Autowired
	WebSocketManager wsm;

	@Autowired
	StompSessionHandler sessionHandler;

	@Test
	void connectTest() throws Exception {
		assertNotNull(wsm);
		wsm.connect(URL);
	}

	@Test
	void disConnectTest() throws Exception {
		assertNotNull(wsm);
		wsm.connect(URL);
		wsm.disConnect();
	}

	@Test
	void subscribeTest() {
		assertNotNull(wsm);

		wsm.connect(URL).subscribe("/topic/chart");

		wsm.send("/app/chart", wsm.getMessage());

		new Scanner(System.in).nextLine(); // Don't close immediately.
	}

	
	
	

	@Test
	public void completableFuture() {
		
		Runnable task = () -> {
			try {
				TimeUnit.SECONDS.sleep(5l);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			System.out.println("TASK completed : " + Thread.currentThread().getName());
		};
		
		CompletableFuture.runAsync(task).thenCompose(aVoid -> CompletableFuture.runAsync(task))
				.thenAcceptAsync(aVoid -> System.out.println("all tasks completed!!")).exceptionally(throwable -> {
					System.out.println("exception occurred!!");
					return null;
				});
		
		System.out.println("난 계속 진행할게요 고고~~");
		new Scanner(System.in).nextLine(); // Don't close immediately.
	}
	@Test
	public void completableFutureTest() {
		CompletableFuture.runAsync(()->{
			System.out.println("before awake!");
			try {
		    	Thread.sleep(1000);
		    } catch (Exception e) {
		    	System.out.println("EXCEPTION");
		    }
		    
		    System.out.println("I'm awake!");
		})
		.thenRun(()->System.out.println("Next Job"));

		System.out.println("Hello world!");
		new Scanner(System.in).nextLine(); // Don't close immediately.
	}

}
