package io.wheel.engine;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.wheel.ErrorCode;
import io.wheel.ErrorCodeException;
import io.wheel.config.Domain;
import io.wheel.exceptions.NoServiceException;
import io.wheel.registry.ServiceExp;
import io.wheel.registry.ServiceRepository;

/**
 * DefaultServiceGateway
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class DefaultServiceGateway implements ServiceGateway {

	private static Logger logger = LoggerFactory.getLogger(DefaultServiceGateway.class);

	private Domain domain;

	private ServiceExecutor serviceExecutor;

	private ServiceRepository serviceRepository;

	private ThreadPoolTaskExecutor coreThreadPool;

	@Override
	public RpcResponse service(final RpcRequest request) {
		RpcResponse response = null;
		try {
			response = this.process(request);
		} catch (Throwable t) {
			logger.error("Process service error,request={}", request, t);
			response = new RpcResponse();
			response.setSuccess(false);
			if (t instanceof ErrorCodeException) {
				ErrorCodeException ece = (ErrorCodeException) t;
				response.setResultCode(ece.getErrorCode());
				response.setResultMessage(ece.getErrorMessage());
			} else if (t.getCause() instanceof ErrorCodeException) {
				ErrorCodeException ece = (ErrorCodeException) t.getCause();
				response.setResultCode(ece.getErrorCode());
				response.setResultMessage(ece.getErrorMessage());
			} else {
				response.setResultCode(ErrorCode.FAILURE);
				response.setResultMessage(t.getMessage());
			}
		}
		return response;
	}

	private RpcResponse process(final RpcRequest request) throws Throwable {
		Future<RpcResponse> future = null;
		String serviceCode = request.getServiceCode();
		final ServiceExp serviceExp = serviceRepository.getServiceExp(serviceCode);
		if (serviceExp == null) {
			throw new NoServiceException(serviceCode);
		}
		int timeout = this.getTimeout(serviceExp, request);
		try {
			future = coreThreadPool.submit(new Callable<RpcResponse>() {
				public RpcResponse call() throws Exception {
					return serviceExecutor.execute(serviceExp, request);
				}
			});
			return future.get(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new ErrorCodeException(ErrorCode.SERVICE_INTERRUPTED);
		} catch (TimeoutException e) {
			if (future != null) {
				future.cancel(true);
			}
			throw new ErrorCodeException(ErrorCode.SERVICE_TIMEOUT);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			throw (cause != null) ? cause : e;
		}
	}

	private int getTimeout(ServiceExp serviceExp, RpcRequest request) {
		int timeout = request.getTimeout();
		if (timeout == 0) {
			timeout = serviceExp.getTimeout();
		}
		if (timeout == 0) {
			timeout = domain.getDefaultTimeout();
		}
		return timeout;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public void setServiceExecutor(ServiceExecutor serviceExecutor) {
		this.serviceExecutor = serviceExecutor;
	}

	public void setCoreThreadPool(ThreadPoolTaskExecutor coreThreadPool) {
		this.coreThreadPool = coreThreadPool;
	}

	public void setServiceRepository(ServiceRepository serviceRepository) {
		this.serviceRepository = serviceRepository;
	}
}
