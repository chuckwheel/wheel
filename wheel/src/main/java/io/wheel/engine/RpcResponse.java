package io.wheel.engine;

import java.io.Serializable;

public class RpcResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	private long invokeId;
	private Object result;
	private String resultCode;
	private String resultMessage;
	private boolean success = true;

	public long getInvokeId() {
		return invokeId;
	}

	public void setInvokeId(long invokeId) {
		this.invokeId = invokeId;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
	public boolean isSuccess(){
		return this.success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}

}
