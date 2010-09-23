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

import com.google.code.fuzzops.webfuzzer.controller.FuzzController;
import com.google.code.fuzzops.webfuzzer.crawler.CrawlerThread;
import com.google.code.fuzzops.webfuzzer.applet.FuzzRequestBean;
import com.google.code.fuzzops.webfuzzer.applet.FuzzResponseBean;
import com.google.code.fuzzops.webfuzzer.applet.ResultBean;

/* FuzzEngine.java
 * 
 * This class does the bulk of the heavy lifting. It manages the actual fuzzing process.
 */

public class FuzzEngineOld {

	public final static String[] BAD_CHAR_DEFAULT = {"\n"," ","\t"};
	public final static int DATA_INCREMENTING_LENGTH = 5;
	public final static int DATA_INITIAL_LENGTH = 1;
	public final static int URL_MAX_LENGTH = 750;
	public enum Type {Application, SOAP, REST};
	
	//Fuzzing process data
	ArrayList<String> targets;
	ArrayList<ResultBean> results;
	long ttl;
	String fuzzData = "";
	int size;
	
	//Fuzzing process HTTP request data
	URL fuzzUrl = null;
	HttpURLConnection current = null;
	String[] methods = {"GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"};
	@SuppressWarnings("unchecked")
	ArrayList badChars;
	String lastFuzz;
	char[] lastFuzzDiff;
	Type type;
	
	//File IO
	FileOutputStream fStream;
	ObjectOutputStream oStream;
	FileInputStream fiStream;
	
	//Request data
	FuzzRequestBean request;
	int response;
	String url;
	String msg;
	String method;
	String seed;
	
	public FuzzEngineOld(ArrayList<String> urlsArray, FuzzRequestBean request, Type type) {
		this.type = type;
		this.request = request;
		targets = urlsArray;
		results = new ArrayList<ResultBean>();
	}

	//Sets the length of time for the fuzzer to run
	public long equateTime(){
		if(request.getTtl() == 0 || request.getTtl() > 1440){
			request.setTtl(FuzzController.DEFAULT_TIME);
		}
		return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(request.getTtl());
	}

	@SuppressWarnings("unchecked")
	public void fuzz(ArrayList<String> urls) {
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
			fuzz(urls);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Sets up the fuzzing process
		targets = urls;
		ttl = equateTime();
		size = DATA_INITIAL_LENGTH;
		
		//Establish the arraylist of bad characters that should be scraped out of future requests, and ultimately are the analyized results of the fuzz
		badChars = new ArrayList();
		for(int i = 0; i < BAD_CHAR_DEFAULT.length; i++){
			badChars.add(BAD_CHAR_DEFAULT[i]);
		}
		
		//Sets the seed to the name of the request
		seed = request.getName();
		request.getUtils().monitor.log("Fuzzing...");
		
		//Fuzz according to the request length
		while(System.currentTimeMillis() < ttl){
			
			//Sets the last done fuzz equal to the current generated fuzz data
			lastFuzz = fuzzData;
			
			//Generates fuzzing data of a certain size, using the seed, and replacing characters that cause problems in URLs.
			fuzzData = FuzzBuilder.generateData(size, seed).replace("\n", "");
			
			//Scrubs out characters that were identified as "bad characters" in previous fuzz interations
			for(int i =0; i < badChars.size(); i++)	
				fuzzData = fuzzData.replaceAll(String.valueOf(badChars.get(i)),"");
			
			//Fuzz through each target url
			for(int targ = 0; targ < targets.size(); targ++){
				try{
					
					//Sets the URL for the connection equal to the target and the generated fuzz data
					fuzzUrl = new URL(targets.get(targ).replace(CrawlerThread.REPLACE_VALUE, fuzzData));
					current = (HttpURLConnection)fuzzUrl.openConnection();
					
					//Go through all the POST,GET,OPTIONS, etc methods for each request
					for(int meth = 0; meth < methods.length; meth++){
						try{
							current.setRequestMethod(methods[meth]);
						} catch (ProtocolException pe){
							current.disconnect();
							current.setRequestMethod(methods[meth]);
						}
						
						//Does the actual connection
						if(doConnection()){
							//If the connection gave anything but a 200 request, then determine what new character caused the issue
							lastFuzzDiff = fuzzData.substring(lastFuzz.length()).toCharArray();
							if(lastFuzzDiff.length <= 0){
								lastFuzzDiff = new char[]{'a'};
							}
							
							//Sends connections, testing each possible problem character one-by-one until it is determined. 
							for(int bc =0; bc < lastFuzzDiff.length; bc++){
								fuzzUrl = new URL(targets.get(targ).replace(CrawlerThread.REPLACE_VALUE, String.valueOf(lastFuzzDiff[bc])));
								current = (HttpURLConnection) fuzzUrl.openConnection();
								current.setRequestMethod(methods[meth]);
								if(doConnection()){
									badChars.add(lastFuzzDiff[bc]);
									request.getUtils().monitor.log("Found Bad Character");
								}
							}
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			//If the length of the url is too long, then ressed and regenerated data.
			//TODO: In the future, you may want to change the use of the HttpURLConnection. This would allow you to bypass the 750 character URL
			//      restriction by generating requests in a manner other than using a URL.
			if(size >= URL_MAX_LENGTH){
				seed = FuzzBuilder.generateData(5, seed).replace("\n", "");
				size = 0;
			}
			//Increases the length of the fuzz data to be generated
			size += DATA_INCREMENTING_LENGTH;
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

	private boolean doConnection(){
		try {
			
			//connect to the url
			current.connect();
			
			//get the results
			response = current.getResponseCode();
			url = "" + fuzzUrl;
			msg = current.getResponseMessage();
			method = current.getRequestMethod();
			
			//If the url works, record the responses
			if(!(url == null || url.equals("")));{
				ResultBean rr = new ResultBean(url, response,msg, method);
				rr.setBadChars(badChars);
				oStream.writeObject(rr);
			}
			current.disconnect();
			if(current.getResponseCode() != 200){ //Will setting this to =! 200 cause bad analysis when requests just arnt formatted correctly?s
				return true;
			}else 
				return false;
		}catch (SocketException se){
			current.disconnect();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	//Setters and getters
	public FuzzRequestBean getRequest() {
		return request;
	}

	public void setRequest(FuzzRequestBean request) {
		this.request = request;
	}

	public ArrayList<String> getTargets() {
		return targets;
	}

	public void setTargets(ArrayList<String> targets) {
		this.targets = targets;
	}



}
