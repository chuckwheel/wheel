package io.wheel;

/**
 * SysException
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class SysException extends ErrorCodeException {

	private static final long serialVersionUID = 1L;

	public SysException(String errorCode){
		super(errorCode);
	}
	
	public SysException(String errorCode, Object[] arguments){
		super(errorCode,arguments);
	}
	
	public SysException(String errorCode, Throwable cause, Object[] arguments) {
		super(errorCode, cause, arguments);
	}
	
}
