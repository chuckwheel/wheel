package io.wheel.config;

public class TransportConfig {

	private String name = "http";
	private String host;
	private int port;
	private String path = "wheel";
	private int serverThreads = 50;
	private int clientThreads = 50;
	private boolean isDefault = false;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getServerThreads() {
		return serverThreads;
	}

	public void setServerThreads(int serverThreads) {
		this.serverThreads = serverThreads;
	}

	public int getClientThreads() {
		return clientThreads;
	}

	public void setClientThreads(int clientThreads) {
		this.clientThreads = clientThreads;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

}
