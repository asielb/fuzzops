

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class CommunicationBean implements Serializable{

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
