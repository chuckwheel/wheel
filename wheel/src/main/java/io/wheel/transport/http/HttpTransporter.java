package io.wheel.transport.http;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.x.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.wheel.config.Initable;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.engine.ServiceGateway;
import io.wheel.registry.ServiceInfo;
import io.wheel.transport.Transporter;

public class HttpTransporter implements Transporter, Initable, ApplicationContextAware {

	private static Logger logger = LoggerFactory.getLogger(HttpTransporter.class);

	private String name = "http";
	private String host;
	private int port;
	private String path = "wheel";

	private HttpServer httpServer;
	private HttpConnector httpConnector;
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
			this.httpServer = new HttpServer(this);
			this.httpServer.start();
		}
		{
			this.httpConnector = new HttpConnector(this);
			this.httpConnector.start();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUrl(String defaultAddress) {
		String address = "";
		if (StringUtils.isBlank(host) || StringUtils.equals(host, "0.0.0.0")) {
			address = defaultAddress;
		} else {
			address = host;
		}
		return name + "://" + address + ":" + port + "/" + path;
	}

	@Override
	public RpcResponse invoke(ServiceProvider<ServiceInfo> provider, RpcRequest request) {
		return httpConnector.invoke(provider, request);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ServiceGateway getServiceGateway() {
		return serviceGateway;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}

}