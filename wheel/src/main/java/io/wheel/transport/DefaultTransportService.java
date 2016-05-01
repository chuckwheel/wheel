package io.wheel.transport;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wheel.config.Domain;
import io.wheel.config.Protocol;
import io.wheel.engine.Initializable;

public class DefaultTransportService implements TransportService, Initializable {

	private Logger logger = LoggerFactory.getLogger(DefaultTransportService.class);

	private Map<String, Transporter> transporters = new HashMap<String, Transporter>();

	private Domain domain;

	@Override
	public int index() {
		return 2;
	}

	@Override
	public void init() throws Exception {
		for (Protocol protocol : domain.getProtocols().values()) {
			Transporter transporter = transporters.get(protocol.getName());
			if (transporter != null) {
				transporter.start(protocol);
			} else {
				logger.warn("Transporter not found!protocol={}", protocol);
			}
		}
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
