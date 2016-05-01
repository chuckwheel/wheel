package io.wheel.engine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class RpcContext implements Serializable {

	private static final long serialVersionUID = 824031350880522209L;

	private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
		protected RpcContext initialValue() {
			return new RpcContext();
		}
	};

	// 是否异步
	private boolean async;
	// 隐藏参数
	private Map<String, Object> attributes;
	// 异步结果
	private Future<RpcResponse> future;
	// 同步结果
	private RpcResponse response;

	public static void remove() {
		LOCAL.remove();
	}

	public static RpcContext get() {
		return LOCAL.get();
	}

	public RpcResponse getResponse() {
		return this.response;
	}

	public void setResponse(RpcResponse response) {
		this.response = response;
	}

	public Future<RpcResponse> getFuture() {
		return this.future;
	}

	public void setFuture(Future<RpcResponse> future) {
		this.future = future;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void setAttribute(String name, Object value) {
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
		}
		attributes.put(name, value);
	}

	public Object getAttribute(String name) {
		if (attributes == null) {
			return null;
		}
		return attributes.get(name);
	}

	public void clearAttributes() {
		if (attributes != null) {
			this.attributes.clear();
			this.attributes = null;
		}
	}

	public boolean isAsync() {
		return async;
	}

	public void async() {
		this.setAsync(true);
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	@Override
	public String toString() {
		return "[" + hashCode() + "]" + ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
