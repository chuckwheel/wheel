package io.wheel.engine;

/**
 * 
 * 
 * @author chuan.huang
 * @since 2014-3-5
 * @version 1.0
 */
public interface ServiceGateway {

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	RpcResponse service(RpcRequest request) throws Exception;

}
