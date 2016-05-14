package io.wheel.engine;

import io.wheel.registry.ServiceExp;

/**
 * DefaultServiceExecutor
 * 
 * @author chuck
 * @since 2013-10-10
 * @version 1.0
 */
public class DefaultServiceExecutor implements ServiceExecutor {

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
			Exception cause = (Exception) t.getCause();
			throw (cause != null) ? cause : t;
		} finally {
			ServiceContext.remove();
		}
	}

}
