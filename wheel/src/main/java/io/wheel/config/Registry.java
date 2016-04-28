package io.wheel.config;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Service Registry
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class Registry {

	private String name = "master";

	private String address = "127.0.0.1:2181";

	private String path = "/wheel/service";

	private int sleepTimeMs = 3000;

	private int maxRetries = 3;

	private boolean isDefault = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getSleepTimeMs() {
		return sleepTimeMs;
	}

	public void setSleepTimeMs(int sleepTimeMs) {
		this.sleepTimeMs = sleepTimeMs;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public Boolean isDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
