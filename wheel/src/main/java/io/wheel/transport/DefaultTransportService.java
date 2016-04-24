package io.wheel.transport;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import io.wheel.config.Initializable;

public class DefaultTransportService implements TransportService, Initializable, ApplicationContextAware {

	private Logger logger = LoggerFactory.getLogger(DefaultTransportService.class);

	private Map<String, Transporter> transporters = new HashMap<String, Transporter>();

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public int index() {
		return 0;
	}

	@Override
	public void init() throws Exception {
		Map<String, Transporter> transporters = applicationContext.getBeansOfType(Transporter.class);
		if (!CollectionUtils.isEmpty(transporters)) {
			for (Transporter transporter : transporters.values()) {
				this.transporters.put(transporter.getName(), transporter);
				logger.warn("Register transporter,name={}", transporter.getName());
			}
		}
	}

	public Transporter getTransporter(String name) {
		return transporters.get(name);
	}

	public Map<String, Transporter> getTransporters() {
		return transporters;
	}

	public void setTransporters(Map<String, Transporter> transporters) {
		this.transporters = transporters;
	}

}
