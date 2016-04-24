package io.wheel.transport.netty;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.x.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.wheel.config.Initializable;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.engine.ServiceGateway;
import io.wheel.registry.ServiceInfo;
import io.wheel.transport.Transporter;
import io.wheel.transport.http.HttpTransporter;

public class NettyTransporter implements Transporter, Initializable, ApplicationContextAware {

	private static Logger logger = LoggerFactory.getLogger(HttpTransporter.class);

	private String name = "netty";
	private String host;
	private int port;
	private int serverThreads = 10;
	private int clientThreads = 10;

	private NettyServer nettyServer;
	private NettyClient nettyClient;
	private ServiceGateway serviceGateway;

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public int index() {
		return 3;
	}

	@Override
	public void init() throws Exception {
		if (serviceGateway == null) {
			serviceGateway = this.applicationContext.getBean(ServiceGateway.class);
		}
		if (this.port != 0) {
			this.nettyServer = new NettyServer(this);
			this.nettyServer.start();
		}
		{
			this.nettyClient = new NettyClient(this);
			this.nettyClient.start();
		}
	}

	@Override
	public String getUrl(String defaultAddress) {
		String address;
		if (StringUtils.isBlank(host) || StringUtils.equals(host, "0.0.0.0")) {
			address = defaultAddress;
		} else {
			address = host;
		}
		return address + ":" + port;
	}

	@Override
	public RpcResponse invoke(ServiceProvider<ServiceInfo> provider, RpcRequest request) {
		return nettyClient.invoke(provider, request);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getServerThreads() {
		return serverThreads;
	}

	public void setServerThreads(Integer serverThreads) {
		this.serverThreads = serverThreads;
	}

	public Integer getClientThreads() {
		return clientThreads;
	}

	public void setClientThreads(Integer clientThreads) {
		this.clientThreads = clientThreads;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ServiceGateway getServiceGateway() {
		return serviceGateway;
	}

	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}

}
