package io.wheel.engine;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MethodInvoker;

import io.wheel.registry.ServiceExp;

/**
 * RpcContext
 * 
 * @author chuck
 * @since 2013-10-10
 * @version 1.0
 */
public class DefaultServiceExecutor implements ServiceExecutor {

	private static Logger logger = LoggerFactory.getLogger(DefaultServiceExecutor.class);

	@Override
	public RpcResponse execute(ServiceExp serviceExp, RpcRequest request) throws Exception {
		ServiceContext serviceContext = ServiceContext.get();
		serviceContext.setRequest(request);
		RpcResponse response = new RpcResponse();
		serviceContext.setResponse(response);
		try {
			Object obj = serviceExp.getTargetObject();
			Object[] args = request.getArguments();
			Object result = serviceExp.getTargetMethod().invoke(obj, args);
			response.setResult(result);
			response.setSuccess(true);
			return response;
		} catch (Exception e) {
			Throwable cause = e;
			if (e.getCause() != null)
				cause = e.getCause();
			logger.error("Invoke local service failed! Local service info:" + serviceExp, cause);
			response.setSuccess(false);
			throw (Exception) cause;
		} finally {
			ServiceContext.remove();
		}
	}

}
