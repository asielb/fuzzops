package controller;

import java.util.ArrayList;
import java.util.HashMap;

import commands.DeleteCommand;
import commands.RefreshCommand;
import commands.RequestCommand;

import applet.UtilBean;

public class FuzzApplicationController {

	@SuppressWarnings("unchecked")
	HashMap<String, Class> commands;
	FuzzCommandInterface receivedCommand;
	FuzzerMonitor monitor;
	
	@SuppressWarnings("unchecked")
	public FuzzApplicationController(FuzzerMonitor monitor){
		this.monitor = monitor;
		commands = new HashMap<String, Class>();
		commands.put("refresh", RefreshCommand.class);
		commands.put("request", RequestCommand.class);
		commands.put("delete", DeleteCommand.class);
		//ETC
	}
	
	@SuppressWarnings("unchecked")
	public void handleRequest(String string, ArrayList params, UtilBean utils){
		Class commandClass = (Class) commands.get(string.toLowerCase());
		try{
			receivedCommand = (FuzzCommandInterface) commandClass.newInstance();
			receivedCommand.execute(params, utils);
			monitor.log(string + " recieved and executed");
		} catch (Exception e){
			monitor.log(string + " not a recognized command");
		}
	}
	
}
