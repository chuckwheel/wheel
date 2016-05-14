package io.wheel.transport.netty;

import org.apache.curator.x.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wheel.config.Protocol;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.engine.ServiceGateway;
import io.wheel.registry.ServiceInfo;
import io.wheel.transport.Transporter;
/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class NettyTransporter implements Transporter {

	private static Logger logger = LoggerFactory.getLogger(NettyTransporter.class);

	private NettyServer nettyServer;

	private NettyClient nettyClient;

	private ServiceGateway serviceGateway;

	@Override
	public void start(Protocol protocol) throws Exception {
		{
			this.nettyClient = new NettyClient(protocol);
			this.nettyClient.start();
		}
		if (protocol.getPort() != 0) {
			this.nettyServer = new NettyServer(protocol, serviceGateway);
			this.nettyServer.start();
		}
		logger.warn("Start netty transporter!protocol={}", protocol);
	}

	@Override
	public RpcResponse invoke(ServiceProvider<ServiceInfo> provider, RpcRequest request) {
		return nettyClient.invoke(provider, request);
	}

	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}

}
