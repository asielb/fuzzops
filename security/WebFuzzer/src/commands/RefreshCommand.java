package commands;

import java.io.IOException;
import java.util.ArrayList;

import controller.FuzzCommandInterface;

import applet.CommunicationBean;
import applet.UtilBean;

public class RefreshCommand implements FuzzCommandInterface{

	@SuppressWarnings("unchecked")
	@Override
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
