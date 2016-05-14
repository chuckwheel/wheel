package io.wheel.transport.netty;

import io.wheel.config.Parameter;

/**
 * NettyParameter
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public enum NettyParameter implements Parameter {

	SERVER_THREADS("serverThreads", Integer.class, 20),

	CLIENT_THREADS("clientThreads", Integer.class, 20);

	private String key;

	private Class<?> type;

	private Object defaultValue;

	private NettyParameter(String key, Class<?> type, Object defaultValue) {
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
