package com.bybit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.bybit.zzz.MyStompSessionHandler;

@SpringBootApplication
@EnableScheduling
public class BybitDataApplication {

	private static final Logger log = LoggerFactory.getLogger(BybitDataApplication.class);

	@Autowired
	RedisTemplate<String, Object> template;
	
	private final TaskExecutor exec = new SimpleAsyncTaskExecutor();

	public static void main(String[] args) {

		SpringApplication.run(BybitDataApplication.class, args).close();
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
	@Profile("default") // Don't run from test(s)
	public ApplicationRunner runner() {
		return args -> {
			this.exec.execute(() -> log.info("Hit Enter to terminate..."));
			System.in.read();
		};
	}

	//@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {

		return args -> {
			this.exec.execute(() -> log.info("Hit Enter to terminate..."));
			System.in.read();
		};
	}
}
