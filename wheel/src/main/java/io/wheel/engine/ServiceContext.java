package io.wheel.engine;

import java.io.Serializable;

public class ServiceContext implements Serializable {

	private static final long serialVersionUID = 824031350880522209L;

	private static final ThreadLocal<ServiceContext> LOCAL = new ThreadLocal<ServiceContext>() {
		@Override
		protected ServiceContext initialValue() {
			return new ServiceContext();
		}
	};

	public static void remove() {
		LOCAL.remove();
	}

	public static ServiceContext get() {
		return LOCAL.get();
	}

	private RpcRequest request;

	private RpcResponse response;

	public RpcRequest getRequest() {
		return request;
	}

	public void setRequest(RpcRequest request) {
		this.request = request;
	}

	public RpcResponse getResponse() {
		return response;
	}

	public void setResponse(RpcResponse response) {
		this.response = response;
	}

}
