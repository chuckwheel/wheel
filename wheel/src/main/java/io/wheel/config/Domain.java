package io.wheel.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
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

import io.wheel.Initializable;

public class Domain implements ApplicationContextAware, ApplicationListener<ApplicationEvent> {

	private Logger logger = LoggerFactory.getLogger(Domain.class);

	private Application application;

	private Map<String, Registry> registrys = new HashMap<String, Registry>();

	private Map<String, Protocol> protocols = new HashMap<String, Protocol>();

	private ApplicationContext applicationContext;

	private AtomicBoolean initialized = new AtomicBoolean(false);

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			logger.warn("Context refreshed!");
			if (initialized.get()) {
				logger.warn("Context initialized!");
				return;
			} else {
				logger.warn("Begin initialize!");
				this.init();
			}
		} else if (event instanceof ContextStartedEvent) {
			logger.warn("Context started!");
			logger.warn("Wheel startup success! Version info={}", this.getVersion());
		} else if (event instanceof ContextClosedEvent) {
			logger.warn("Context closed!");
		} else if (event instanceof ContextStoppedEvent) {
			logger.warn("Context stopped!");
		}
	}

	private void init() {

		if (application == null) {
			application = applicationContext.getBean(Application.class);
		}
		logger.warn(application.toString());

		Map<String, Registry> registrys = applicationContext.getBeansOfType(Registry.class);
		if (!CollectionUtils.isEmpty(registrys)) {
			for (Registry registry : registrys.values()) {
				this.registrys.put(registry.getName(), registry);
				logger.warn(registry.toString());
			}
		}
		Map<String, Protocol> protocols = applicationContext.getBeansOfType(Protocol.class);
		if (!CollectionUtils.isEmpty(protocols)) {
			for (Protocol protocol : protocols.values()) {
				this.protocols.put(protocol.getName(), protocol);
				logger.warn(protocol.toString());
			}
		}

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
				if(initializable.abortOnError()){
					logger.error("Container will be exit!");
					System.exit(1);
				}
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public String getAppId() {
		return this.getApplication().getAppId();
	}

	public String getNodeId() {
		return this.getApplication().getNodeId();
	}

	public String getVersion() {
		return this.getApplication().getVersion();
	}

	public String getOwner() {
		return this.getApplication().getOwner();
	}

	public int getDefaultTimeout() {
		return this.getApplication().getDefaultTimeout();
	}

	public Map<String, Registry> getRegistrys() {
		return registrys;
	}

	public void setRegistrys(Map<String, Registry> registrys) {
		this.registrys = registrys;
	}

	public Map<String, Protocol> getProtocols() {
		return protocols;
	}

	public void setProtocols(Map<String, Protocol> protocols) {
		this.protocols = protocols;
	}

	public Application getApplication() {
		if (application == null) {
			application = applicationContext.getBean(Application.class);
		}
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

}
