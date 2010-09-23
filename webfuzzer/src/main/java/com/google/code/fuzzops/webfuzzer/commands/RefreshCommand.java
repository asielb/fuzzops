package com.google.code.fuzzops.webfuzzer.commands;

import java.io.IOException;
import java.util.ArrayList;

import com.google.code.fuzzops.webfuzzer.controller.FuzzCommandInterface;

import com.google.code.fuzzops.webfuzzer.applet.CommunicationBean;
import com.google.code.fuzzops.webfuzzer.applet.UtilBean;

public class RefreshCommand implements FuzzCommandInterface{

	@SuppressWarnings("unchecked")
	public void execute(ArrayList params, UtilBean utils) {
		try {
			ArrayList returnVal = new ArrayList();
			returnVal.add(utils.getOutputFile().list());
			utils.getOutput().writeObject(new CommunicationBean("response",returnVal));
			utils.monitor.log("Sending list of files");
		} catch (IOException e) {
			utils.monitor.log("Failed to send list of files");
			e.printStackTrace();
		}
	}

}
