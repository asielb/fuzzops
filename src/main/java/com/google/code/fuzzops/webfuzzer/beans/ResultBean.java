package com.google.code.fuzzops.webfuzzer.beans;


import java.io.Serializable;
import java.util.ArrayList;

public class ResultBean implements Serializable{

	
	private static final long serialVersionUID = 8467692930085077504L;
	int code;
	String message;
	String method;
	String url;
	String body;
	ArrayList badChars;
	//Only used for output data
	public String email;
	public String name;
	public String originalUrl;
	//boolean dictFlag = false;
	
	public ResultBean(String  string, int code, String message, String method) {
		this.url = string; //May be the wrong method
		this.code = code;
		this.method = method;
		this.message = message;
	}
	
	public ResultBean(String  string, int code, String message, String method, ArrayList badChars) {
		this.url = string; //May be the wrong method
		this.code = code;
		this.method = method;
		this.message = message;
		this.badChars = badChars;
	}
	/*public ResultBean(String  string, int code, String message, String method, boolean bool) {
		this.url = string; //May be the wrong method
		this.code = code;
		this.method = method;
		this.message = message;
		this.dictFlag = bool;
	}*/

	/*public boolean isDict(){
		return dictFlag;
	}
	
	public void toggleDict(){
		dictFlag = !dictFlag;
	}
	
	public void setDict(boolean bool){
		dictFlag = bool;
	}*/
	
	public void setBadChars(ArrayList bc){
		badChars = bc;
	}
	
	public ArrayList getBadChars(){
		return badChars;
	}
	
	public void addBadChar(char bc){
		badChars.add(bc);
	}
	
	public String getBadCharsAsString(){
		StringBuilder bcString = new StringBuilder();
		for(int i = 0; i < badChars.size(); i++){
			bcString.append(badChars.get(i)+", ");
		}
		return new String(bcString);
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setHeader(String name, String email, String url) {
		this.name = name;
		this.email = email;
		this.originalUrl = url;
	}
	
	
	
}
