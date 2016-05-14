package io.wheel.engine;

/**
 * ServiceGateway
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public interface ServiceGateway {

	/**
	 * 
	 * @param request
	 * @return RpcResponse 此方法不会抛出异常，总会返回RpcResponse
	 */
	RpcResponse service(RpcRequest request);

}
