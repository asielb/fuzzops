package applet;


import java.io.Serializable;

@SuppressWarnings("serial")
public class FuzzResponseBean implements Serializable {

	boolean result;
	
	String email;
	String name;
	int timeCrawl;
	int depth;
	int ttl;
	
	public FuzzResponseBean(boolean result){
		this.result = result;
	}

	public FuzzResponseBean(String email, String name, int timeCrawl, int depth, int ttl) {
		this.email = email;
		this.name = name;
		this.timeCrawl = timeCrawl;
		this.depth = depth;
		this.ttl = ttl;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		// TODO Auto-generated method stub
		return email;
	}

	public int getDepth() {
		// TODO Auto-generated method stub
		return depth;
	}

	public int getTimeCrawl() {
		// TODO Auto-generated method stub
		return timeCrawl;
	}

	public int getTtl() {
		// TODO Auto-generated method stub
		return ttl;
	}
	
}
