package io.wheel.engine;

import java.io.Serializable;
/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class ServiceContext implements Serializable {

	private static final long serialVersionUID = 824031350880522209L;

	// 请求对象
	private RpcRequest request;
	// 应答对象
	private RpcResponse response;

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
		System.out.println();
		return LOCAL.get();
	}

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
