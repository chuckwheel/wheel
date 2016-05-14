package io.wheel.config;

/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public interface Parameter {

	String getKey();

	Class<?> getType();

	Object getDefaultValue();
}
