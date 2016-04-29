package io.wheel.engine;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.wheel.config.Domain;
import io.wheel.registry.ServiceExp;
import io.wheel.registry.ServiceRepository;

/**
 * 默认网关实现类
 * 
 * @author chuan.huang
 * @since 2014-3-5
 * @version 1.0
 */
public class DefaultServiceGateway implements ServiceGateway {

	private static Logger logger = LoggerFactory.getLogger(DefaultServiceExecutor.class);

	private Domain domain;

	private ServiceExecutor serviceExecutor;

	private ServiceRepository serviceRepository;

	private ThreadPoolTaskExecutor coreThreadPool;

	@Override
	public RpcResponse service(final RpcRequest request) throws Exception {
		String serviceCode = request.getServiceCode();
		final ServiceExp serviceExp = serviceRepository.getServiceExp(serviceCode);
		int timeout = getTimeout(serviceExp, request);
		Future<RpcResponse> future = null;
		try {
			future = coreThreadPool.submit(new Callable<RpcResponse>() {
				public RpcResponse call() throws Exception {
					return serviceExecutor.execute(serviceExp, request);
				}
			});
			return future.get(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RpcException("", e);
		} catch (TimeoutException e) {
			if (future != null) {
				future.cancel(true);
			}
			throw new RpcException("", e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause != null) {
				throw (Exception) cause;
			} else {
				throw e;
			}
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
