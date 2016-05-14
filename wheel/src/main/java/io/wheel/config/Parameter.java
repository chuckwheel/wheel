package io.wheel.config;

/**
 * Parameter
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
