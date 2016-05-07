package io.wheel.engine;

/**
 * 
 * 
 * @author chuan.huang
 * @since 2014-3-5
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
