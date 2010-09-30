package com.google.code.fuzzops.webfuzzer.applet;

public class FuzzerInfo {

	String host;
	String port;
	
	public FuzzerInfo(String host, String port){
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	public Integer getPortAsInt(){
		return Integer.parseInt(port);
	}
}
