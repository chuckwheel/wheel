package io.wheel.registry;

import java.util.Collection;

/**
 * Service repository
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public interface ServiceRepository {

	/**
	 * 取单个发布的服务
	 * 
	 * @param serviceCode
	 * @return
	 */
	ServiceExp getServiceExp(String serviceCode);

	/**
	 * 取所有发布的服务
	 * 
	 * @return
	 */
	Collection<ServiceExp> getAllServiceExps();

	/**
	 * 取单个引入的服务
	 * 
	 * @param serviceCode
	 * @return
	 */
	ServiceImp getServiceImp(String serviceCode);

	/**
	 * 取所有引入的服务
	 * 
	 * @return
	 */
	Collection<ServiceImp> getAllServiceImps();
}
