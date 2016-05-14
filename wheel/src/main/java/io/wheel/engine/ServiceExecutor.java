/**
 * 
 */
package io.wheel.engine;

import io.wheel.registry.ServiceExp;

/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public interface ServiceExecutor {

	/**
	 * 
	 * @param serviceExp
	 * @param request
	 * @return
	 * @throws Exception
	 */
	RpcResponse execute(ServiceExp serviceExp,RpcRequest request) throws Exception;
}
