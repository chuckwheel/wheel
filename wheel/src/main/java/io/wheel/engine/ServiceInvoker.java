package io.wheel.engine;

/**
 * 
 * @author chuckhuang
 *
 */
public interface ServiceInvoker {

	/**
	 * 
	 * @param request
	 * @return
	 */
	RpcResponse invoke(RpcRequest request);
	
}
