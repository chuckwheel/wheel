package io.wheel.registry;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.ServiceProviderBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import io.wheel.config.Domain;
import io.wheel.config.Protocol;
import io.wheel.config.Registry;
import io.wheel.engine.Initializable;

/**
 * The default DefaultServiceDiscovery implement
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class DefaultServiceDiscovery implements io.wheel.registry.ServiceDiscovery, Initializable {

	private static Logger logger = LoggerFactory.getLogger(DefaultServiceDiscovery.class);

	private ProviderStrategy<ServiceInfo> providerStrategy = new RandomStrategy<ServiceInfo>();

	private JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<ServiceInfo>(ServiceInfo.class);

	private Map<String, ServiceProvider<ServiceInfo>> serviceProviders = new HashMap<String, ServiceProvider<ServiceInfo>>();

	private Domain domain;

	private ServiceRepository serviceRepository;

	@Override
	public int index() {
		return 1;
	}

	@Override
	public void init() throws Exception {
		if (CollectionUtils.isEmpty(domain.getRegistrys())) {
			logger.warn("Service registory is empyt!");
			return;
		}
		for (Registry registry : domain.getRegistrys().values()) {

			RetryPolicy retryPolicy = new ExponentialBackoffRetry(registry.getSleepTimeMs(), registry.getMaxRetries());
			CuratorFramework client = CuratorFrameworkFactory.newClient(registry.getAddress(), retryPolicy);
			client.start();

			ServiceDiscoveryBuilder<ServiceInfo> builder = ServiceDiscoveryBuilder.builder(ServiceInfo.class);
			builder.client(client);
			builder.basePath(registry.getPath());
			builder.serializer(serializer);
			ServiceDiscovery<ServiceInfo> serviceDiscovery = builder.build();
			serviceDiscovery.start();

			this.registerService(registry.getName(), serviceDiscovery);
			this.initServiceProvider(registry.getName(), serviceDiscovery);

			logger.warn("Start service registory!registry={}", registry);
		}
	}

	private void registerService(String registryName, ServiceDiscovery<ServiceInfo> serviceDiscovery) throws Exception {
		Collection<ServiceExp> serviceExps = serviceRepository.getAllServiceExps();
		Set<String> serviceGroups = new HashSet<String>();
		for (ServiceExp serviceExp : serviceExps) {
			if (serviceGroups.contains(serviceExp.getServiceGroup())) {
				continue;
			}
			serviceGroups.add(serviceExp.getServiceGroup());
			if (serviceExp.needRegistry(registryName)) {
				ServiceInfo serviceInfo = new ServiceInfo();
				serviceInfo.setServiceCode(serviceExp.getServiceCode());
				serviceInfo.setTimeout(serviceExp.getTimeout());
				serviceInfo.setProtocols(this.getProtocols(serviceExp));
				ServiceInstanceBuilder<ServiceInfo> builder = ServiceInstance.<ServiceInfo> builder();
				builder.name(serviceExp.getServiceGroup());
				builder.payload(serviceInfo);
				ServiceInstance<ServiceInfo> serviceInstance = builder.build();
				serviceDiscovery.registerService(serviceInstance);
			}
		}
	}

	private void initServiceProvider(String registry, ServiceDiscovery<ServiceInfo> serviceDiscovery) throws Exception {
		Collection<ServiceImp> serviceImps = serviceRepository.getAllServiceImps();
		Set<String> serviceGroups = new HashSet<String>();
		for (ServiceImp serviceImp : serviceImps) {
			String serviceGroup = serviceImp.getServiceGroup();
			if (serviceGroups.contains(serviceGroup)) {
				continue;
			}
			serviceGroups.add(serviceGroup);
			if (StringUtils.equals(serviceImp.getRegistry(), registry)) {
				ServiceProviderBuilder<ServiceInfo> builder = serviceDiscovery.serviceProviderBuilder();
				builder.serviceName(serviceImp.getServiceGroup()).providerStrategy(providerStrategy);
				ServiceProvider<ServiceInfo> provider = builder.build();
				provider.start();
				String key = getServiceProviderKey(registry, serviceGroup);
				serviceProviders.put(key, provider);
			}
		}
	}

	private Map<String, String> getProtocols(ServiceExp serviceExp) throws Exception {

		Map<String, String> result = new HashMap<String, String>();
		String protocols = serviceExp.getProtocol();
		if (StringUtils.isBlank(protocols) || StringUtils.equals(protocols, "*")) {
			for (Protocol p : domain.getProtocols().values()) {
				result.put(p.getName(), this.getUrl(p));
			}
			return result;
		}
		for (String protocol : protocols.split(",")) {
			Protocol p = domain.getProtocols().get(protocol);
			if (p == null) {
				continue;
			}
			result.put(p.getName(), this.getUrl(p));
		}
		return result;
	}

	private String getUrl(Protocol protocol) throws Exception {
		String localAddress = null;
		if (StringUtils.isBlank(protocol.getHost()) || StringUtils.equals(protocol.getHost(), "0.0.0.0")) {
			Collection<InetAddress> ips = ServiceInstanceBuilder.getAllLocalIPs();
			if (ips.size() > 0) {
				localAddress = ips.iterator().next().getHostAddress();
			}
		} else {
			localAddress = protocol.getHost();
		}
		return localAddress + ":" + protocol.getPort();
	}

	@Override
	public ServiceProvider<ServiceInfo> getServiceProvider(String registry, String serviceGroup) throws Exception {
		String key = getServiceProviderKey(registry, serviceGroup);
		return serviceProviders.get(key);
	}

	private String getServiceProviderKey(String registry, String serviceGroup) {
		return registry + "|" + serviceGroup;
	}

	public void setServiceRepository(ServiceRepository serviceRepository) {
		this.serviceRepository = serviceRepository;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}
}
