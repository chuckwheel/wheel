package io.wheel.transport.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServer {

	private Logger logger = LoggerFactory.getLogger(HttpServer.class);

	private HttpTransporter httpTransporter;

	public HttpServer(HttpTransporter httpTransporter) {
		this.httpTransporter = httpTransporter;
	}

	public void start() throws Exception {
		Server server = new Server(httpTransporter.getPort());
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		context.addServlet(new ServletHolder(new HttpGateway(httpTransporter.getServiceGateway())),
				"/" + httpTransporter.getPath());
		logger.info("Http server start!port={}", httpTransporter.getPort());
		server.start();
	}

}
