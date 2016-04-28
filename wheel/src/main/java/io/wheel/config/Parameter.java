package io.wheel.config;

public interface Parameter {
	
	String getKey();

	Class<?> getType();

	Object getDefaultValue();
}
