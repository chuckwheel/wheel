package io.wheel.engine;

import org.apache.curator.x.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wheel.RpcException;
import io.wheel.exceptions.NoProviderException;
import io.wheel.exceptions.NoServiceException;
import io.wheel.exceptions.NoTranspoterException;
import io.wheel.registry.ServiceDiscovery;
import io.wheel.registry.ServiceImp;
import io.wheel.registry.ServiceInfo;
import io.wheel.registry.ServiceRepository;
import io.wheel.transport.TransportService;
import io.wheel.transport.Transporter;

/**
 * DefaultServiceInvoker
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class DefaultServiceInvoker implements ServiceInvoker {

	private static Logger logger = LoggerFactory.getLogger(DefaultServiceInvoker.class);

	private TransportService transportService;

	private ServiceDiscovery serviceDiscovery;

	private ServiceRepository serviceRepository;

	@Override
	public RpcResponse invoke(RpcRequest request) throws Exception {
		String serviceCode = request.getServiceCode();
		ServiceImp serviceImp = serviceRepository.getServiceImp(serviceCode);
		if (serviceImp == null) {
			logger.error("Service not definition,serviceCode={}", serviceCode);
			throw new NoServiceException(serviceCode);
		}
		String registry = serviceImp.getRegistry();
		String serviceGroup = serviceImp.getServiceGroup();
		Transporter transporter = transportService.getTransporter(serviceImp.getProtocol());
		if (transporter == null) {
			logger.error("Transporter not definition,protocol={}", serviceImp.getProtocol());
			throw new NoTranspoterException(serviceImp.getProtocol());
		}
		ServiceProvider<ServiceInfo> provider = serviceDiscovery.getServiceProvider(registry, serviceGroup);
		if (provider == null) {
			logger.error("ServiceProvider not definition,serviceCode={}", serviceCode);
			throw new NoProviderException(serviceCode);
		}
		RpcResponse response = transporter.invoke(provider, request);
		if (!response.isSuccess()) {
			RpcException exception = new RpcException();
			exception.setErrorCode(response.getResultCode());
			exception.setErrorMessage(response.getResultMessage());
			logger.error("Invoke service error,resultCode={}", response.getResultCode());
			throw exception;
		}
		return response;
	}

	public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}

	public void setServiceRepository(ServiceRepository serviceRepository) {
		this.serviceRepository = serviceRepository;
	}

	public void setTransportService(TransportService transportService) {
		this.transportService = transportService;
	}

}
