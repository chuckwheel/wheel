package io.wheel.registry;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.FactoryBean;

public class ServiceExporter<T> implements FactoryBean<T> {

	private int timeout = 0;

	private String registry;

	private String protocol;

	private T object;

	private Class<?> objectType;

	private Map<String, MethodConfig> methods = new HashMap<String, MethodConfig>();

	@Override
	public T getObject() throws Exception {
		return object;
	}

	@Override
	public Class<?> getObjectType() {
		return objectType;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setBean(T object) {
		this.object = object;
	}
	
	public void setReference(T object) {
		this.object = object;
	}

	public void setInterface(Class<?> objectType) {
		this.objectType = objectType;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Map<String, MethodConfig> getMethods() {
		return methods;
	}

	public void setMethods(Map<String, MethodConfig> methods) {
		this.methods = methods;
	}

	public MethodConfig getMethod(String methodName) {
		return methods.get(methodName);
	}

}
