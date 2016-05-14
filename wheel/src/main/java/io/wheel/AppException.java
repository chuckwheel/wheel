package io.wheel;

/**
 * 业务异常
 * 
 * @author chuckhuang
 *
 */
public class AppException extends ErrorCodeException {

	private static final long serialVersionUID = 1L;

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
