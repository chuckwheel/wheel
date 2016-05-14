package io.wheel.transport.http;

import java.util.concurrent.TimeUnit;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wheel.ErrorCodeException;
import io.wheel.config.Protocol;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.registry.ServiceInfo;
import io.wheel.utils.HessianUtils;

/**
 * HttpClient
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class HttpClient {

	private static Logger logger = LoggerFactory.getLogger(HttpClient.class);

	private org.eclipse.jetty.client.HttpClient httpClient;

	private Protocol protocol;

	private String servicePath;

	public HttpClient(Protocol protocol) {
		this.protocol = protocol;
		this.servicePath = protocol.getParameterValue(HttpParameter.SERVICE_PATH);
	}

	public void start() throws Exception {
		httpClient = new org.eclipse.jetty.client.HttpClient();
		int maxConnections = protocol.getParameterValue(HttpParameter.MAX_CONNECTIONS);
		httpClient.setMaxConnectionsPerDestination(maxConnections);
		int connectTimeout = protocol.getParameterValue(HttpParameter.CONNECT_TIMEOUT);
		httpClient.setConnectTimeout(connectTimeout);
		httpClient.start();
	}

	private String getUrl(ServiceProvider<ServiceInfo> provider) throws Exception {
		ServiceInstance<ServiceInfo> target = provider.getInstance();
		ServiceInfo serviceInfo = target.getPayload();
		return serviceInfo.getProtocol(protocol.getName()) + servicePath;
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
				throw new ErrorCodeException();
			}
			int resultCode = response.getStatus();
			if (resultCode != 200) {
				logger.error("Send and Receive http request failed! resultCode{}", resultCode);
				throw new ErrorCodeException();
			}
			return this.toRpcResponse(response);
		} catch (Throwable t) {
			logger.error("Send and Receive http request failed!", t);
			throw new ErrorCodeException("", t);
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
