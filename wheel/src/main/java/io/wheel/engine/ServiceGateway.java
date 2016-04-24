package io.wheel.engine;

/**
 * 
 * @author chuckhuang
 *
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
