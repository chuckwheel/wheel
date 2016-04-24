package io.wheel.engine;

import java.io.Serializable;

public class RpcRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	private long invokeId;
	private int timeout;
	private String serviceCode;
	private Object[] arguments;

	public long getInvokeId() {
		return invokeId;
	}

	public void setInvokeId(long invokeId) {
		this.invokeId = invokeId;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
