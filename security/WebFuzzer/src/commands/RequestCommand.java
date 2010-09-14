package commands;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import applet.CommunicationBean;
import applet.FuzzRequestBean;
import applet.FuzzResponseBean;
import applet.ResultBean;
import applet.UtilBean;
import controller.FuzzCommandInterface;

public class RequestCommand implements FuzzCommandInterface{

	FileInputStream fInput;
	ObjectInputStream oInput;
	ArrayList<ResultBean> currentFileContents;
	FilePermission perm;
	ResultBean result;
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(ArrayList params, UtilBean utils) {
			File returnFile = new File(utils.getOutputFile().getAbsolutePath()+"\\"+(String)params.get(0));
			perm = new java.io.FilePermission(utils.getOutputFile().getAbsolutePath(), "write,read");
			utils.monitor.log("Found: "+ returnFile.getAbsolutePath());
			ArrayList returnVal = new ArrayList();
			try{
				fInput = new FileInputStream(returnFile);
				oInput = new ObjectInputStream(fInput);
				
				//Sending info about the request
				utils.monitor.log("Sending information about the results...");
				returnVal.add((FuzzResponseBean)oInput.readObject());
				utils.getOutput().writeObject(new CommunicationBean("response",returnVal));
				
				//Sending the results
				utils.monitor.log("Sending " + params.get(0) + "...");
				while((result = (ResultBean) oInput.readObject()) != null){
					returnVal = new ArrayList();
					returnVal.add(result);
					try {
						utils.getOutput().writeObject(new CommunicationBean("response",returnVal));
					} catch (IOException e) {
						utils.monitor.log("Failed: could not send requested file");
						e.printStackTrace();
					}
				}
				
				//currentFileContents = (ArrayList<ResultBean>)oInput.readObject();
				//oInput.close();
				//fInput.close();
			} catch (EOFException ex){
				utils.monitor.log( params.get(0) + "Sent");
				try {
					oInput.close();
					fInput.close();
				} catch (IOException e) {
				}
				try {
					returnVal = new ArrayList();
					returnVal.add(false); //Client needs to check if more results are coming.
					utils.getOutput().writeObject(new CommunicationBean("response",returnVal));
				} catch (IOException e) {
					utils.monitor.log("Failed to alert client of EOF");
					e.printStackTrace();
				}
			} catch(Exception e){
				e.printStackTrace();
				e = new Exception("Did not read in file correctly");
			}
			/*returnVal.add(currentFileContents);
			try {
				utils.getOutput().writeObject(new CommunicationBean("response",returnVal));
				utils.monitor.log("Success: sent " + params.get(0));
			} catch (IOException e) {
				utils.monitor.log("Failed: could not send requested file");
				e.printStackTrace();
			}*/
	}

}
