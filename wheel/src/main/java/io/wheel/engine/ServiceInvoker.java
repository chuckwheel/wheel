package io.wheel.engine;

/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public interface ServiceInvoker {

	/**
	 * 
	 * @param request
	 * @return
	 */
	RpcResponse invoke(RpcRequest request);
	
}
