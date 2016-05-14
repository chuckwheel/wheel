package io.wheel.registry;

import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonRootName;

/**
 * ServiceInfo
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
@JsonRootName("ServiceInfo")
public class ServiceInfo {

	private String serviceCode;
	
	private int timeout;
	
	private Map<String, String> protocols;

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public Map<String, String> getProtocols() {
		return protocols;
	}

	public void setProtocols(Map<String, String> protocols) {
		this.protocols = protocols;
	}

	public String getProtocol(String name) {
		if (protocols != null) {
			return protocols.get(name);
		} else {
			return null;
		}
	}
}
