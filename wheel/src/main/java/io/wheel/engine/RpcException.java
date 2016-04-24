package io.wheel.engine;

public class RpcException extends RuntimeException {

	private static final long serialVersionUID = -4496194597047711613L;

	private String errorCode;

	private String errorMessage;

	public RpcException() {
		super("RPC error happened!");
		this.errorCode = RpcErrorCodes.FAILURE;
	}

	public RpcException(String errorCode) {
		super("RPC error happened! ErrorCode is: " + errorCode);
	}

	public RpcException(String errorCode,Throwable cause) {
		super("RPC error happened! ErrorCode is: " + errorCode, cause);
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

	@Override
	public String getMessage() {
		String msg = super.getMessage();
		msg += "\n" + "errorCode=" + errorCode + ",errorMessage=" + errorMessage;
		return msg;
	}
}
