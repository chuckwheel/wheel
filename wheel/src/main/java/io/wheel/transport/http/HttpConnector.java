package io.wheel.transport.http;

import java.util.concurrent.TimeUnit;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wheel.engine.RpcException;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.registry.ServiceInfo;
import io.wheel.utils.HessianUtils;

public class HttpConnector {

	// 日志常量
	private static Logger logger = LoggerFactory.getLogger(HttpConnector.class);

	private HttpClient httpClient;

	private HttpTransporter httpTransporter;

	public HttpConnector(HttpTransporter httpTransporter) {
		this.httpTransporter = httpTransporter;
	}

	public void start() throws Exception {
		httpClient = new HttpClient();
		httpClient.setMaxConnectionsPerDestination(200);
		httpClient.setConnectTimeout(500);
		httpClient.start();
	}

	private String getUrl(ServiceProvider<ServiceInfo> provider) throws Exception {
		ServiceInstance<ServiceInfo> target = provider.getInstance();
		ServiceInfo serviceInfo = target.getPayload();
		return serviceInfo.getProtocol(httpTransporter.getName());
	}

	public RpcResponse invoke(ServiceProvider<ServiceInfo> provider, RpcRequest request) {
		String url = null;
		try {
			url = getUrl(provider);
			Request httpRequest = httpClient.POST(url);
			httpRequest.content(this.buildContent(request));
			httpRequest.timeout(request.getTimeout(), TimeUnit.SECONDS);
			ContentResponse response = httpRequest.send();
			if (response == null) {
				logger.error("Send and Receive http request failed! resultCode=");
				throw new RpcException();
			}
			int resultCode = response.getStatus();
			if (resultCode != 200) {
				logger.error("Send and Receive http request failed! resultCode=" + resultCode);
				throw new RpcException();
			}
			return this.toRpcResponse(response);
		} catch (Throwable t) {
			logger.error("Send and Receive http request failed!", t);
			throw new RpcException("", t);
		}
	}

	private RpcResponse toRpcResponse(ContentResponse response) throws Exception {
		return (RpcResponse) HessianUtils.bytesToObject(response.getContent());
	}

	private ContentProvider buildContent(RpcRequest request) throws Exception {
		byte[] bytes = HessianUtils.objectToBytes(request);
		return new BytesContentProvider(bytes);
	}
}
