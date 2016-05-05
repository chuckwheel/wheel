package io.wheel.registry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import io.wheel.engine.RpcContext;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.engine.ServiceInvoker;
import io.wheel.utils.ClassHelper;

public class ServiceImporter
		implements MethodInterceptor, FactoryBean<Object>, InitializingBean, ApplicationContextAware {

	private static Logger logger = LoggerFactory.getLogger(ServiceImporter.class);

	private int timeout = 5;

	private String registry = "master";

	private String protocol = "netty";

	private Object serviceProxy;

	private Class<?> serviceInterface;

	private ApplicationContext applicationContext;

	private ServiceInvoker serviceInvoker;

	private ThreadPoolTaskExecutor asyncThreadPool;

	private Map<String, MethodConfig> methods = new HashMap<String, MethodConfig>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void afterPropertiesSet() {
		ProxyFactory proxy = new ProxyFactory(serviceInterface, this);
		this.serviceProxy = proxy.getProxy(ClassUtils.getDefaultClassLoader());
		if (serviceInvoker == null) {
			serviceInvoker = applicationContext.getBean(ServiceInvoker.class);
		}
		if (asyncThreadPool == null) {
			asyncThreadPool = applicationContext.getBean("asyncThreadPool", ThreadPoolTaskExecutor.class);
		}
	}

	@Override
	public Object getObject() throws Exception {
		return serviceProxy;
	}

	@Override
	public Class<?> getObjectType() {
		return serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		RpcContext rpcContext = RpcContext.get();
		rpcContext.setFuture(null);
		rpcContext.setResponse(null);
		try {
			Method method = invocation.getMethod();
			String serviceCode = ClassHelper.getMethodFullName(method);
			RpcRequest request = new RpcRequest();
			request.setServiceCode(serviceCode);
			request.setArguments(invocation.getArguments());
			request.setTimeout(getTimeout());
			this.setAttributes(request, rpcContext);
			if (rpcContext.isAsync()) {
				return this.acall(request);
			} else {
				return this.call(request);
			}
		} finally {
			rpcContext.setAsync(false);
			rpcContext.clearAttributes();
		}
	}

	private void setAttributes(RpcRequest request, RpcContext rpcContext) {
		Map<String, Object> attributes = rpcContext.getAttributes();
		if (!CollectionUtils.isEmpty(attributes)) {
			request.setAttributes(new HashMap<String, Object>(attributes));
		}
	}

	private Object call(final RpcRequest request) {
		if (logger.isDebugEnabled()) {
			logger.debug("Process sync invoke,request={}", request);
		}
		RpcResponse response = serviceInvoker.invoke(request);
		RpcContext.get().setResponse(response);
		return response.getResult();
	}

	private Object acall(final RpcRequest request) {
		if (logger.isDebugEnabled()) {
			logger.debug("Process async invoke,request={}", request);
		}
		Future<RpcResponse> future = asyncThreadPool.submit(new Callable<RpcResponse>() {
			@Override
			public RpcResponse call() throws Exception {
				return serviceInvoker.invoke(request);
			}
		});
		RpcContext.get().setFuture(future);
		return null;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Map<String, MethodConfig> getMethods() {
		return methods;
	}

	public void setMethods(Map<String, MethodConfig> methods) {
		this.methods = methods;
	}

	public MethodConfig getMethod(String methodName) {
		return methods.get(methodName);
	}

	public void setAsyncThreadPool(ThreadPoolTaskExecutor asyncThreadPool) {
		this.asyncThreadPool = asyncThreadPool;
	}
}
