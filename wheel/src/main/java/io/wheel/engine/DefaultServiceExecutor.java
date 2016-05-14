package io.wheel.engine;

import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

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
			Object target = serviceExp.getTargetObject();
			Object[] args = request.getArguments();
			Method method = serviceExp.getTargetMethod();
			Object result = ReflectionUtils.invokeMethod(method, target, args);
			response.setResult(result);
			response.setSuccess(true);
			return response;
		} finally {
			ServiceContext.remove();
		}
	}

}
