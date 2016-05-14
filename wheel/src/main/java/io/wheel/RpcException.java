package io.wheel;

/**
 * RpcException
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class RpcException extends ErrorCodeException {

	private static final long serialVersionUID = 1L;

	public RpcException() {
		super();
	}
	
	public RpcException(String errorCode) {
		super(errorCode);
	}

	public RpcException(String errorCode, Object[] arguments) {
		super(errorCode, arguments);
	}

	public RpcException(String errorCode, Throwable cause, Object[] arguments) {
		super(errorCode, cause, arguments);
	}
}
