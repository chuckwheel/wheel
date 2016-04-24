package io.wheel.transport;

import java.util.Map;

public interface TransportService {

	Transporter getTransporter(String name);
	
	Map<String, Transporter> getTransporters();
}
