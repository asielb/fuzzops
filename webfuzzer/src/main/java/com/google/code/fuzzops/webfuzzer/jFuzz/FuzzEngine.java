package com.google.code.fuzzops.webfuzzer.jFuzz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.eviware.soapui.impl.wsdl.WsdlRequest;

import com.google.code.fuzzops.webfuzzer.controller.DataGenerationController;
import com.google.code.fuzzops.webfuzzer.controller.FuzzController;

import com.google.code.fuzzops.webfuzzer.crawler.CrawlerThread;
import com.google.code.fuzzops.webfuzzer.applet.FuzzRequestBean;
import com.google.code.fuzzops.webfuzzer.applet.FuzzResponseBean;
import com.google.code.fuzzops.webfuzzer.applet.ResultBean;

public class FuzzEngine {


	public final static String[] BAD_CHAR_DEFAULT = {"\n"," ","\t"};
	public final static int DATA_INCREMENTING_LENGTH = 5;
	public final static int DATA_INITIAL_LENGTH = 1;
	public final static int URL_MAX_LENGTH = 750;
	public enum Type {Application, SOAP, REST};

	//Fuzzing process data
	ArrayList<ResultBean> results;
	long ttl;
	String fuzzData = "";
	int size;
	DataGenerationController dataGenController;
	long moduleTime;
	AbstractDataGenModule currentModule;
	Type type;

	//Fuzzing process HTTP request data
	URL fuzzUrl = null;
	HttpURLConnection current = null;
	String[] methods = {"GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"};
	@SuppressWarnings("unchecked")
	ArrayList badChars;
	String lastFuzz;
	char[] lastFuzzDiff;

	//File IO
	FileOutputStream fStream;
	ObjectOutputStream oStream;
	FileInputStream fiStream;

	//Request data
	FuzzRequestBean request;
	int response = 200;
	String url;
	String msg;
	String method;
	String seed;

	@SuppressWarnings("unchecked")
	public FuzzEngine(FuzzRequestBean request) {
		this.request = request;
		results = new ArrayList<ResultBean>();
		badChars = new ArrayList(); //Default bad characters need to be scrubbed in a different fashion
		ttl = equateTime();
		establishIO();
		dataGenController = new DataGenerationController();
	}

	@SuppressWarnings("unchecked")
	public void fuzz(ArrayList targs){
		request.getUtils().monitor.log("Fuzzing...");
		ArrayList targets = targs;

		while(dataGenController.hasNext()){

			moduleTime = System.currentTimeMillis() + equateTimeForModules();

			currentModule = dataGenController.getNextModule();
			currentModule.seed = request.getName();
			currentModule.badChars = this.badChars;
			
			if(targets.get(0).getClass() == String.class){
				type = Type.Application;
			} if(targets.get(0).getClass() == WsdlRequest.class){
				type = Type.SOAP;
			}//Check for REST
			
			while(System.currentTimeMillis() < moduleTime){
				fuzzData = currentModule.generate(response);

				for(int targ = 0; targ < targets.size(); targ++){

					/*
					 * FUZZ APPLICATIONS
					 * 
					 */
					if(type == Type.Application){
						//If the targets are Strings, its an Application
						try {
							fuzzUrl = new URL(((String) targets.get(targ)).replace(CrawlerThread.REPLACE_VALUE, fuzzData));
							current = (HttpURLConnection)fuzzUrl.openConnection();

							//Go through all the POST,GET,OPTIONS, etc methods for each request
							for(int meth = 0; meth < methods.length; meth++){
								try{
									current.setRequestMethod(methods[meth]);
								} catch (ProtocolException pe){
									current.disconnect();
									current.setRequestMethod(methods[meth]);
								}
								response = doConnection(Type.Application);
							}

						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						/*
						 * 
						 * FUZZ SOAP
						 * 	
						 */
					} if(type == Type.SOAP){
						//If the targets are WsdlRequests, its SOAP


						response = doConnection(Type.SOAP);

					}
				}
			}
		}
		try {
			//Closes the file streams
			oStream.close();
			fStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.getUtils().monitor.log("Fuzzing Complete!");
	}





	private int doConnection(Type type) {
		//DO URL CONNECTION FOR WEB APPS
		if(type == Type.Application){
			try{
				//connect to the url
				current.connect();
				
				//get the results
				response = current.getResponseCode();
				url = "" + fuzzUrl;
				msg = current.getResponseMessage();
				method = current.getRequestMethod();
				System.out.println(url);
				//If the url works, record the responses
				if(!(url == null || url.equals("")));{
					ResultBean rr = new ResultBean(url, response,msg, method);
					rr.setBadChars(badChars);
					oStream.writeObject(rr);
				}
				current.disconnect();
				return current.getResponseCode();
			}catch (SocketException se){
				current.disconnect();
				return 400;
			} catch (IOException e) {
				e.printStackTrace();
				return 400;
			} 
		} if(type == Type.SOAP){
			
		}
		return 400;
	}

	//Method to establish the IO with results file
	private void establishIO() {
		try {
			//Set-up I/O with results file
			fStream = new FileOutputStream(request.getOutputFile());
			oStream = new ObjectOutputStream(fStream);

			//Create a response bean containing all pertinent information relating to this fuzz instance. Used by client to get info of results.
			FuzzResponseBean responseBean = new FuzzResponseBean(request.getEmail(), request.getName(), request.getTimeCrawl(), request.getDepth(), request.getTtl());
			oStream.writeObject(responseBean);

		} catch (FileNotFoundException e) {
			//If the output file does not exist, create it and rerun the fuzzing process
			File oFile = new File(request.getOutputFile());
			try {
				oFile.createNewFile();
				request.getUtils().monitor.log("Creating output file");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			establishIO();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	//Sets the length of time for the fuzzer to run
	public long equateTime(){
		if(request.getTtl() == 0 || request.getTtl() > 1440){
			request.setTtl(FuzzController.DEFAULT_TIME);
		}
		return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(request.getTtl());
	}

	//Sets the length of time for the fuzzer to run
	public long equateTimeForModules(){
		if(request.getTtl() == 0 || request.getTtl() > 1440){
			request.setTtl(FuzzController.DEFAULT_TIME);
		}
		return TimeUnit.MINUTES.toMillis(request.getTtl())/dataGenController.getModuleListSize();
	}

}
