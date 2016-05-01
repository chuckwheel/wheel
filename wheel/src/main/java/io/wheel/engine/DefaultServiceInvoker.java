package io.wheel.engine;

import org.apache.curator.x.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wheel.registry.ServiceDiscovery;
import io.wheel.registry.ServiceImp;
import io.wheel.registry.ServiceInfo;
import io.wheel.registry.ServiceRepository;
import io.wheel.transport.TransportService;
import io.wheel.transport.Transporter;

public class DefaultServiceInvoker implements ServiceInvoker {

	private static Logger logger = LoggerFactory.getLogger(DefaultServiceInvoker.class);

	private TransportService transportService;

	private ServiceDiscovery serviceDiscovery;

	private ServiceRepository serviceRepository;

	@Override
	public RpcResponse invoke(RpcRequest request) {

		String serviceCode = request.getServiceCode();
		ServiceImp serviceImp = serviceRepository.getServiceImp(serviceCode);
		if (serviceImp == null) {
			logger.error("Service importer is null!serviceCode={}", serviceCode);
			return null;
		}
		RpcResponse response = null;
		try {
			String registry = serviceImp.getRegistry();
			String serviceGroup = serviceImp.getServiceGroup();
			ServiceProvider<ServiceInfo> provider = serviceDiscovery.getServiceProvider(registry, serviceGroup);
			Transporter transporter = transportService.getTransporter(serviceImp.getProtocol());
			response = transporter.invoke(provider, request);
		} catch (Exception e) {
			logger.error("Call remote service error!serviceCode={},method name={}", e);
			throw new RpcException("", e);
		} finally {
			// logger.info("");
		}

		if (response == null) {
			throw new RpcException("", new NullPointerException());
		}

		if (!response.isSuccess()) {
			RpcException e = new RpcException();
			e.setErrorCode(response.getResultCode());
			e.setErrorMessage(response.getResultMessage());
			throw e;
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
