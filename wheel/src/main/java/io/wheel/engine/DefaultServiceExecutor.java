package io.wheel.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		} catch (Exception t) {
			//logger.error("Invoke local service failed! serviceExp={}", serviceExp, t);
			Exception cause = (Exception) t.getCause();
			throw (cause != null) ? cause : t;
		} finally {
			ServiceContext.remove();
		}
	}

}
