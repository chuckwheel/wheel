/**
 * 
 */
package io.wheel.engine;

import io.wheel.registry.ServiceExp;

/**
 * 请求执行器
 * 
 * @author chuan.huang
 * @since 2014-4-1
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
