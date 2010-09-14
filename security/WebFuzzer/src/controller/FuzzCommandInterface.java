package controller;

import java.util.ArrayList;
import java.util.HashMap;

import applet.UtilBean;

public interface FuzzCommandInterface {

	HashMap<String, UtilBean> connectedUsers = null;
	UtilBean utils = null;
	
	@SuppressWarnings("unchecked")
	public void execute(ArrayList params, UtilBean utils);

}
