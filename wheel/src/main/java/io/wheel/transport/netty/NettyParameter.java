package io.wheel.transport.netty;

import io.wheel.config.Parameter;

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
