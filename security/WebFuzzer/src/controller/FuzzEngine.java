package controller;

import jFuzz.AbstractDataGenModule;
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

import org.xml.sax.SAXParseException;

import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.WsdlSubmit;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.iface.Request.SubmitException;
import com.eviware.soapui.support.types.StringToStringMap;
import crawler.CrawlerThread;
import applet.FuzzRequestBean;
import applet.FuzzResponseBean;
import applet.ResultBean;

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
	WsdlRequest convertedRequest;
	URL fuzzUrl = null;
	HttpURLConnection current = null;
	String[] methods = {"GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"};
	/*RestRequestInterface.RequestMethod[] soapMethods = {RestRequestInterface.RequestMethod.DELETE
														, RestRequestInterface.RequestMethod.GET
														, RestRequestInterface.RequestMethod.HEAD
														, RestRequestInterface.RequestMethod.OPTIONS
														, RestRequestInterface.RequestMethod.POST
														, RestRequestInterface.RequestMethod.PUT
														, RestRequestInterface.RequestMethod.TRACE};*/
	@SuppressWarnings("unchecked")
	ArrayList badChars;
	String lastFuzz;
	char[] lastFuzzDiff;
	@SuppressWarnings("unchecked")
	WsdlSubmit submit;
	Response wsdlResponse;
	StringToStringMap respHeaders;
	String[] splitStatus;

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
				request.getUtils().monitor.log("Fuzzer: Generating Data -> " + fuzzData);
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
						try{
							convertedRequest = (WsdlRequest) targets.get(targ);
							
							//TODO: Better intelligence can be done here or during enumeration. Separate requests can be cloned, iterating
							// through the instances of the REPLACE_VALUE present in a request. Thus having the same effect as when testing
							// Applications. 
							//convertedRequest.setRequestContent(convertedRequest.getRequestContent().replace(CrawlerThread.REPLACE_VALUE, fuzzData));
							convertedRequest.setRequestContent(convertedRequest.getRequestContent().replace(">?<", ">"+fuzzData+"<"));
							System.out.println(convertedRequest.getRequestContent());
							//for(int meth = 0; meth < soapMethods.length; meth++){
								response = doConnection(Type.SOAP);
							//}
						} catch(Exception e){
							
						}
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
				writeResponse(url, response,msg, method);
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
			try {
				sendRequest();
			} catch (SAXParseException e) {
				badChars.add(fuzzData);
			}
			response = getResponse();
			url = fuzzData;//convertedRequest.getRequestContent();
			msg = splitStatus[2];
			method = convertedRequest.getMethod().name();
			//System.out.println(url);
			writeResponse(url, response,msg, method);
			return response;
		}
		return 400;
	}
	
	void writeResponse(String rurl, int rresponse, String rmsg, String rmethod){
		if(!(url == null || url.equals(""))){
			try {
				ResultBean rr = new ResultBean(rurl, rresponse,rmsg, rmethod);
				rr.setBadChars(badChars);
				oStream.writeObject(rr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//SOAP REQUESTS AND RESPONSES
	@SuppressWarnings("unchecked")
	void sendRequest() throws SAXParseException{
		try{
			submit = (WsdlSubmit)convertedRequest.submit(new WsdlSubmitContext(convertedRequest), false);
		}  catch (SubmitException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	int getResponse(){		
		try{
			wsdlResponse = submit.getResponse();
			//System.out.println("RESPONSE: " + wsdlResponse.getContentAsString());
			respHeaders = wsdlResponse.getResponseHeaders();
			splitStatus = respHeaders.get("#status#").split(" ");
			return Integer.parseInt(splitStatus[1]);
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
			return 200;
		}
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
