package io.wheel.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Protocol {

	// 协议名称
	private String name = "http";
	// 监听主机
	private String host;
	// 监听端口
	private int port;
	// 协议参数
	private String parameter;
	// 是否默认
	private boolean isDefault = false;

	private Map<String, String> parameterValues = new HashMap<String, String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
		this.wrapParameterValues();
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	private void wrapParameterValues() {
		if (StringUtils.isBlank(this.parameter)) {
			return;
		}
		parameterValues.clear();
		String[] values = parameter.split("&");
		if (!ArrayUtils.isEmpty(values)) {
			for (String value : values) {
				String[] pairs = value.split("=");
				parameterValues.put(pairs[0], pairs[1]);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getParameterValue(Parameter parameter) {
		String key = parameter.getKey();
		Class<?> type = parameter.getType();
		String value = parameterValues.get(key);
		if (StringUtils.isBlank(value)) {
			return (T) parameter.getDefaultValue();
		}
		if (type.equals(String.class)) {
			return (T) value;
		} else if (type.equals(Long.class)) {
			return (T) new Long(value);
		} else if (type.equals(Integer.class)) {
			return (T) new Integer(value);
		} else if (type.equals(Boolean.class)) {
			return (T) new Boolean(value);
		} else {
			return (T) value;
		}
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
