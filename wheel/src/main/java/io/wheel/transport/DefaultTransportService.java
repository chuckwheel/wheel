package io.wheel.transport;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wheel.config.Domain;
import io.wheel.config.Protocol;
import io.wheel.engine.Initable;

public class DefaultTransportService implements TransportService, Initable {

	private Logger logger = LoggerFactory.getLogger(DefaultTransportService.class);

	private Map<String, Transporter> transporters = new HashMap<String, Transporter>();

	private Domain domain;

	@Override
	public int index() {
		return 0;
	}

	@Override
	public void init() throws Exception {
		for (Protocol protocol : domain.getProtocols().values()) {
			Transporter transporter = transporters.get(protocol.getName());
			transporter.start(protocol);
		}
		logger.warn("Init TransportService!");
	}

	public Transporter getTransporter(String name) {
		return transporters.get(name);
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public void setTransporters(Map<String, Transporter> transporters) {
		this.transporters = transporters;
	}

}
