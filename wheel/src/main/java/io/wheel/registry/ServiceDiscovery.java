package io.wheel.registry;

import org.apache.curator.x.discovery.ServiceProvider;

/**
 * Service discovery
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public interface ServiceDiscovery {

	/**
	 * Get service provider by registry and service group
	 * 
	 * @param registry
	 * @param serviceGroup
	 * @return
	 * @throws Exception
	 */
	ServiceProvider<ServiceInfo> getServiceProvider(String registry, String serviceGroup) throws Exception;

}
