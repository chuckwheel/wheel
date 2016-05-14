package io.wheel;

/**
 * AppException
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class AppException extends ErrorCodeException {

	private static final long serialVersionUID = 1L;

	public AppException(String errorCode){
		super(errorCode);
	}
	
	public AppException(String errorCode, Object[] arguments){
		super(errorCode,arguments);
	}
	
	public AppException(String errorCode, Throwable cause, Object[] arguments) {
		super(errorCode, cause, arguments);
	}
	
	
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
