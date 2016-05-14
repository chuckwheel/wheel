package io.wheel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import io.wheel.utils.MessageSourceHelper;

/**
 * ErrorCodeException
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class ErrorCodeException extends RuntimeException {

	private static final long serialVersionUID = -4496194597047711613L;

	private String errorCode;

	private String errorMessage;

	private Object[] arguments;

	public ErrorCodeException() {
		this(ErrorCode.FAILURE);
	}

	public ErrorCodeException(String errorCode) {
		this(errorCode, (Object[]) null);
	}

	public ErrorCodeException(String errorCode, Object[] arguments) {
		this(errorCode, (Throwable) null, arguments);
	}

	public ErrorCodeException(String errorCode, Throwable cause) {
		this(errorCode, cause, (Object[]) null);
	}

	public ErrorCodeException(String errorCode, Throwable cause, Object[] arguments) {
		super("Error code exception!", cause);
		this.errorCode = errorCode;
		this.arguments = arguments;
		this.errorMessage = MessageSourceHelper.getMessage(errorCode, arguments, super.getMessage());
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Object[] getArguments() {
		return this.arguments;
	}

	@Override
	public String getMessage() {
		String msg = (this.errorMessage == null) ? super.getMessage() : this.errorMessage;
		return (errorCode == null) ? msg : "[" + errorCode + "] " + msg;
	}

	public ErrorCodeException getRootErrorCodeException() {
		ErrorCodeException root = this;
		while (root.getCause() != null && root.getCause() instanceof ErrorCodeException) {
			root = (ErrorCodeException) root.getCause();
		}
		return root;
	}

	public Throwable getRootCause() {
		Throwable root = this;
		while (!(root.getClass().getName().startsWith("java.") || root.getClass().getName().startsWith("javax."))
				&& root.getCause() != null) {
			root = root.getCause();
		}
		if (root instanceof InvocationTargetException) {
			root = ((InvocationTargetException) root).getTargetException();
		} else if (root instanceof UndeclaredThrowableException) {
			root = ((UndeclaredThrowableException) root).getUndeclaredThrowable();
		}
		return root;
	}

}
