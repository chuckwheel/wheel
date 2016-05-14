package io.wheel.transport;

import org.apache.curator.x.discovery.ServiceProvider;

import io.wheel.config.Protocol;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.registry.ServiceInfo;
/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public interface Transporter {

	void start(Protocol protocol) throws Exception;

	RpcResponse invoke(ServiceProvider<ServiceInfo> provider, RpcRequest request);

}
