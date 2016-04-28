package io.wheel.transport.http;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wheel.config.Protocol;
import io.wheel.engine.ServiceGateway;

public class HttpServer {

	private Logger logger = LoggerFactory.getLogger(HttpServer.class);

	private Protocol protocol;

	private ServiceGateway serviceGateway;

	public HttpServer(Protocol protocol, ServiceGateway serviceGateway) {
		this.protocol = protocol;
		this.serviceGateway = serviceGateway;
	}

	public void start() throws Exception {
		Server server = new Server(protocol.getPort());
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		String servicePath = protocol.getParameterValue(HttpTransporter.KEY_SERVICE_PATH, String.class);
		if(StringUtils.isBlank(servicePath)){
			servicePath = HttpTransporter.DEFAULT_SERVICE_PATH;
		}
		context.addServlet(new ServletHolder(new HttpGateway(serviceGateway)), servicePath);
		logger.info("Http server start!port={}", protocol.getPort());
		server.start();
	}

}
