package io.wheel;

/**
 * ErrorCode
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public interface ErrorCode {

	String SUCCCESS = "SYS-000";
	String FAILURE = "SYS-999";

	String NO_PROTOCOL = "SYS-001";// 协议未定义
	String NO_SERVICE = "SYS-002";// 服务未定义
	String NO_PROVIDER = "SYS-003";// 服务未定义

	String SERVICE_INVOKE_ERROR = "SYS-004";
	String SERVICE_TIMEOUT = "SYS-005";
	String SERVICE_INTERRUPTED = "SYS-006";

}
