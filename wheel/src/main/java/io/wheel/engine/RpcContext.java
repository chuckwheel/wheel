package io.wheel.engine;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class RpcContext implements Serializable {

	private static final long serialVersionUID = 824031350880522209L;

	private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
		@Override
		protected RpcContext initialValue() {
			return new RpcContext();
		}
	};
	
	public static void remove() {
		LOCAL.remove();
	}

	public static RpcContext get() {
		return LOCAL.get();
	}

	private InetSocketAddress localAddress;

	private InetSocketAddress remoteAddress;

    private final Map<String, Object> values = new HashMap<String, Object>();
	
}
