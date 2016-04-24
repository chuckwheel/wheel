package io.wheel.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.util.CollectionUtils;

public class Domain implements ApplicationContextAware, ApplicationListener<ApplicationEvent> {

	private Logger logger = LoggerFactory.getLogger(Domain.class);

	private String appId = "wheel";

	private String nodeId = "wheel-1";

	private String version = "v1.0-20140401";

	private String owner = "chuck";

	private int defaultTimeout = 5;

	private ApplicationContext applicationContext;

	private AtomicBoolean initialized = new AtomicBoolean(false);

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			logger.info("Context refreshed!");
			if (initialized.get()) {
				logger.info("Context initialized!");
				return;
			} else {
				logger.info("Begin initialize!");
				this.init();
			}
		} else if (event instanceof ContextStartedEvent) {
			logger.info("Context started!");
		} else if (event instanceof ContextClosedEvent) {
			logger.info("Context closed!");
		} else if (event instanceof ContextStoppedEvent) {
			logger.info("Context stopped!");
		}
	}

	private void init() {
		Map<String, Initializable> initializables = applicationContext.getBeansOfType(Initializable.class);
		if (CollectionUtils.isEmpty(initializables)) {
			logger.warn("Nothing to initialize!");
			return;
		}
		List<Initializable> list = new ArrayList<Initializable>(initializables.values());
		Collections.sort(list, new Comparator<Initializable>() {
			@Override
			public int compare(Initializable o1, Initializable o2) {
				return o1.index() - o2.index();
			}
		});
		for (Initializable initializable : list) {
			String index = StringUtils.leftPad(initializable.index() + "", 3, " ");
			logger.warn("Pre-initing bean,index={},bean={}", index, initializable);
		}
		for (final Initializable initializable : list) {
			try {
				initializable.init();
			} catch (Exception e) {
				logger.error("Initialize bean error,bean={}", initializable, e);
			}
		}
		logger.warn("Wheel startup success! Version info={}", version);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getDefaultTimeout() {
		return defaultTimeout;
	}

	public void setDefaultTimeout(int defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

}
