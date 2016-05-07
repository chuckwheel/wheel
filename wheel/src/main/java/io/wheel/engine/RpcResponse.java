package io.wheel.engine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
/**
 * 
 * 
 * @author chuan.huang
 * @since 2014-3-5
 * @version 1.0
 */
public class RpcResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	// 返回序号
	private long invokeId;
	// 结果内容
	private Object result;
	// 结果码
	private String resultCode;
	// 结果描述
	private String resultMessage;
	// 是否成功
	private boolean success = true;
	// 隐藏返回值
	private Map<String, Object> attributes;

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

	@SuppressWarnings("unchecked")
	public <T> T getResult() {
		return (T) result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void setAttribute(String name, Object value) {
		if (this.attributes == null) {
			this.attributes = new HashMap<String, Object>();
		}
		attributes.put(name, value);
	}

	public Object getAttribute(String name) {
		if (this.attributes == null) {
			return null;
		}
		return attributes.get(name);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
