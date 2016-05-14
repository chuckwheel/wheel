package io.wheel.transport.http;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.engine.ServiceGateway;
/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class HttpGateway extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(HttpGateway.class);

	private static final long serialVersionUID = 7899257600641509254L;

	private ServiceGateway serviceGateway;

	public HttpGateway() {
		super();
	}

	public HttpGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}

	@Override
	public void init() throws ServletException {
		if (serviceGateway == null) {
			ServletContext servletContext = getServletContext();
			ApplicationContext applicationContext = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			serviceGateway = applicationContext.getBean(ServiceGateway.class);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		process(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		process(request, response);
	}

	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			RpcRequest rpcRequest = this.toRpcRequest(request);
			RpcResponse result = serviceGateway.service(rpcRequest);
			if (result != null) {
				this.writeHttpResponse(result, response);
			}
			response.flushBuffer();
		} catch (Exception e) {
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
