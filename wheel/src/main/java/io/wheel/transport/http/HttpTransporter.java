package io.wheel.transport.http;

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
 * HttpTransporter
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class HttpTransporter implements Transporter {

	private static Logger logger = LoggerFactory.getLogger(HttpTransporter.class);

	private HttpServer httpServer;

	private HttpClient httpClient;

	private ServiceGateway serviceGateway;

	@Override
	public void start(Protocol protocol) throws Exception {
		{
			this.httpClient = new HttpClient(protocol);
			this.httpClient.start();
		}
		if (protocol.getPort() != 0) {
			this.httpServer = new HttpServer(protocol, serviceGateway);
			this.httpServer.start();
		}
		logger.warn("Start http transporter!protocol={}", protocol);
	}

	@Override
	public RpcResponse invoke(ServiceProvider<ServiceInfo> provider, RpcRequest request) {
		return httpClient.invoke(provider, request);
	}

	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}

}