package io.wheel;

/**
 * ErrorCode
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public interface ErrorCode {

	String SUCCCESS = "RPC-000";
	String FAILURE = "RPC-999";

	String NO_PROTOCOL = "RPC-001";// 协议未定义
	String NO_SERVICE = "RPC-002";// 服务未定义
	String NO_PROVIDER = "RPC-003";// 服务未定义

	String SERVICE_INVOKE_ERROR = "RPC-004";
	String SERVICE_TIMEOUT = "RPC-005";
	String SERVICE_INTERRUPTED = "RPC-006";

}
