package io.wheel.transport.http;

import io.wheel.config.Parameter;

/**
 * HttpParameter
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public enum HttpParameter implements Parameter {

	SERVICE_PATH("servicePath", String.class, "/wheel"),

	CONNECT_TIMEOUT("connectTimeout", Integer.class, 500),

	MAX_CONNECTIONS("maxConnections", Integer.class, 500);

	private String key;

	private Class<?> type;

	private Object defaultValue;

	private HttpParameter(String key, Class<?> type, Object defaultValue) {
		this.key = key;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String getKey() {
		return key;
	}

	public Class<?> getType() {
		return type;
	}
}
