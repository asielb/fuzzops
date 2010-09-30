package com.google.code.fuzzops.webfuzzer.applet;


import java.io.Serializable;
import java.util.ArrayList;


public class CommunicationBean implements Serializable{

	private static final long serialVersionUID = 7936446348154469298L;
	String command;
	ArrayList<Object> params;
	
	public CommunicationBean(String request){
		this.command = request;
		params = new ArrayList<Object>();
	}
	
	@SuppressWarnings("unchecked")
	public CommunicationBean(String request, ArrayList params){
		this.command = request;
		this.params = params;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public ArrayList<Object> getParams() {
		return params;
	}

	public void setParams(ArrayList<Object> params) {
		this.params = params;
	}
	
}
