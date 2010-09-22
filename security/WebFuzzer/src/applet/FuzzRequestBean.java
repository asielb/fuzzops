package applet;


import java.io.Serializable;

import controller.FuzzController;

@SuppressWarnings("serial")
public class FuzzRequestBean implements Serializable{

	String name;
	String url;
	String email;
	String username = "";
	String password = "";
	String unameField = "";
	String passField = "";
	String browser = "";
	int ttl = 0;
	UtilBean utils;
	int depth = 0;
	int timeCrawl = 60;
	
	public FuzzRequestBean(String name, String url, String email, int ttl, int depth, int timeCrawl, String browser){
		this.name = name;
		this.url = url;
		this.email = email;
		//this.username = un;
		//this.password = pw;
		this.ttl = ttl;
		this.depth = depth;
		this.browser = browser;
		this.timeCrawl = timeCrawl;
	}

	
	
	public String getUnameField() {
		return unameField;
	}



	public void setUnameField(String unameField) {
		this.unameField = unameField;
	}

	public String getBrowser(){
		return browser;
	}

	public String getPassField() {
		return passField;
	}



	public void setPassField(String passField) {
		this.passField = passField;
	}



	public void setTimeCrawl(int timeCrawl) {
		this.timeCrawl = timeCrawl;
	}



	public String getOutputFile(){
		return utils.getOutputFile().getAbsolutePath()
		+ "\\"
		+ name.replaceAll(" ", "_") 
		+ FuzzController.FORMAT.format(new java.util.Date());
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	@Override
	public String toString() {
		return "FuzzRequestBean [email=" + email + ", name=" + name
				+ ", password=" + password + ", ttl=" + ttl + ", url=" + url
				+ ", username=" + username + "]";
	}

	public void setUtils(UtilBean utils) {
		this.utils = utils;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getTimeCrawl() {
		return timeCrawl;
	}

	public void setTime_crawl(int timeCrawl) {
		this.timeCrawl = timeCrawl;
	}

	public UtilBean getUtils() {
		return utils;
	}
	
}
