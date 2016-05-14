package io.wheel.registry;

import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * ServiceExp
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class ServiceExp {
	// 服务码
	private String serviceCode;
	// 服务分组
	private String serviceGroup;
	// 本地执行时超时时间
	private int timeout = 5;
	// 发布的注册中心列表
	private String registry = "*";
	// 发布的协议类型列表
	private String protocol = "*";
	// 本地执行对象
	private Object targetObject;
	// 本地方法缓存
	private Method targetMethod;

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

	public Object getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
	}

	public Method getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(Method targetMethod) {
		this.targetMethod = targetMethod;
	}

	public String getServiceGroup() {
		return serviceGroup;
	}

	public void setServiceGroup(String serviceGroup) {
		this.serviceGroup = serviceGroup;
	}

	public boolean needRegistry(String registryName) {
		if (StringUtils.isBlank(registry) || StringUtils.equals(registry, "*")) {
			return true;
		}
		String[] registrys = registry.split(",");
		if (ArrayUtils.contains(registrys, registryName)) {
			return true;
		}
		return false;
	}
}
