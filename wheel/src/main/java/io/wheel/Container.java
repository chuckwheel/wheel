package io.wheel;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Container {

	private static final Logger logger = LoggerFactory.getLogger(Container.class);

	private static final String SHUTDOWN_HOOK_KEY = "wheel.shutdown.hook";

	private static final String DEFAULT_SPRING_CONFIG = "classpath*:META-INF/spring/*.xml";

	private static final AtomicBoolean running = new AtomicBoolean(true);

	private static final Object lock = new Object();
	
	private ClassPathXmlApplicationContext context;

	public static void main(String[] args) {
		Container container = null;
		try {
			container = new Container();
			container.addShutdownHook();
			container.start();
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			System.exit(1);
		}
		synchronized(lock){
			while (running.get()) {
				try {
					lock.wait();
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
					System.exit(1);
				}
			}
		}
		
	}

	private void addShutdownHook() {
		final Container container = this;
		if (StringUtils.equalsIgnoreCase(System.getProperty(SHUTDOWN_HOOK_KEY), "true")) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					synchronized(lock){
						container.stop();
						running.set(false);
						lock.notify();
					}
				}
			});
		}
	}

	private void start() {
		context = new ClassPathXmlApplicationContext(DEFAULT_SPRING_CONFIG.split("[,\\s]+"));
		context.start();
	}

	private void stop() {
		try {
			if (context != null) {
				context.stop();
				context.close();
				context = null;
				logger.info("Wheel container stopped!");
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

}