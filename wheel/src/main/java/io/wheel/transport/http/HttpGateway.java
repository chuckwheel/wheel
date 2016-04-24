package io.wheel.transport.http;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import io.wheel.engine.ServiceGateway;
import io.wheel.registry.DefaultServiceDiscovery;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;

public class HttpGateway extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(HttpGateway.class);
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 7899257600641509254L;

	/**
	 * 到服务网关具体实现的引用
	 */
	private ServiceGateway serviceGateway;

	public HttpGateway() {
		super();
	}

	public HttpGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}

	/**
	 * 初始化
	 */
	@Override
	public void init() throws ServletException {
		if (serviceGateway == null) {
			ServletContext servletContext = getServletContext();
			ApplicationContext applicationContext = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			serviceGateway = applicationContext.getBean(ServiceGateway.class);
		}
	}

	/**
	 * 处理由get方法发起的服务请求
	 * 
	 * @param request
	 *            请求消息
	 * @param response
	 *            应答消息
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		process(request, response);
	}

	/**
	 * 处理由post方法发起的服务请求
	 * 
	 * @param request
	 *            请求消息
	 * @param response
	 *            应答消息
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		process(request, response);
	}

	/**
	 * 对Http格式的服务请求的处理
	 * 
	 * @param request
	 *            请求消息
	 * @param response
	 *            应答消息
	 * @throws IOException
	 */
	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {

			// 构造服务请求
			RpcRequest rpcRequest = this.toRpcRequest(request);

			// 调用服务网关
			RpcResponse result = serviceGateway.service(rpcRequest);

			// 发送结果
			if (result != null) {
				this.writeHttpResponse(result, response);
			}
			// 刷新缓存
			response.flushBuffer();
		} catch (Exception e) {
			// 打印异常
			logger.error("Process http request error!", e);
			// 异常抛出，由通讯协议框架处理
			// throw e;
		}
	}

	private RpcRequest toRpcRequest(HttpServletRequest request) {
		HessianInput hi = null;
		try {
			hi = new HessianInput(request.getInputStream());
			return (RpcRequest) hi.readObject();
		} catch (Exception e) {
			logger.error("Bytes to object failed!", e);
			return null;
		} finally {
			if (hi != null) {
				try {
					hi.close();
				} catch (Exception e) {
					logger.error("Close HessianInput failed!", e);
				}
			}
		}
	}

	private void writeHttpResponse(RpcResponse result, HttpServletResponse httpResponse) throws IOException {
		HessianOutput ho = null;
		try {
			ho = new HessianOutput(httpResponse.getOutputStream());
			ho.writeObject(result);
		} catch (Exception e) {
			logger.error("Object to bytes failed!", e);
		} finally {
			if (ho != null) {
				try {
					ho.close();
				} catch (Exception e) {
					logger.error("Close HessianOutput failed!", e);
				}
			}
		}
	}

	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}
}
