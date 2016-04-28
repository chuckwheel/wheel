package io.wheel.transport;

import org.apache.curator.x.discovery.ServiceProvider;

import io.wheel.config.Protocol;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.registry.ServiceInfo;

public interface Transporter {

	String getName();
	
	void start(Protocol protocol) throws Exception;

	RpcResponse invoke(ServiceProvider<ServiceInfo> provider,RpcRequest request);
	
}
