package com.google.code.fuzzops.webfuzzer.controller;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.code.fuzzops.webfuzzer.applet.CommunicationBean;
import com.google.code.fuzzops.webfuzzer.applet.FuzzRequestBean;
import com.google.code.fuzzops.webfuzzer.applet.FuzzResponseBean;
import com.google.code.fuzzops.webfuzzer.applet.UtilBean;
import com.google.code.fuzzops.webfuzzer.crawler.CrawlerThread;
import com.google.code.fuzzops.webfuzzer.soap.SoapEnumeratingEngine;

/*
 * FuzzControllerThread.java
 * 
 * This class is a runnable thread. It is used to deal with new connections of a FuzzingApplet instance. Theoretically there should only be one,
 * although there may be more than one.
 * 
 */

public class FuzzControllerThread implements Runnable{

	//Connection and I/O Objects
	Socket cliSock;
	ObjectInputStream input;
	ObjectOutputStream output;
	
	//Utilities regarding the fuzzer
	UtilBean utils;
	ConcurrentLinkedQueue<Thread> queue;
	FuzzApplicationController controller;
	File folder;
	FuzzerMonitor monitor;

	//Objects used in communication
	Object inc;
	FuzzRequestBean request;
	CommunicationBean commBean;
	
	public FuzzControllerThread(Socket sock, UtilBean fuzzUtils, ConcurrentLinkedQueue<Thread> queue, FuzzApplicationController controller){
		this.folder = fuzzUtils.getOutputFile();
		this.monitor = fuzzUtils.monitor;
		this.queue = queue;
		this.controller = controller;
		this.cliSock = sock;
	}
	
	public void run() {
		try{
		//Establish I/O with connected client
		input = new ObjectInputStream(cliSock.getInputStream());
		output = new ObjectOutputStream(cliSock.getOutputStream());

		
		//import the fuzzing servers utilities into the local thread
		utils = new UtilBean(output, input,folder);
		utils.monitor = this.monitor;
		
		monitor.log("Server: Waiting for communication");
		while(cliSock.isConnected()){
			try {
				//Recieve commands
				inc = input.readObject();
				monitor.log("Server: incoming data...");
				
				/* If its a FuzzRequest, handle it here. If not, send it to the application controller. 
				 * 
				 * Added the application controller after initially creating the class. Did not feel it was necessary to completely refactor, 
				 * although it could be done in the future.
				 */
				if(inc.getClass().equals(FuzzRequestBean.class)){
					//Receive the request
					request = (FuzzRequestBean)inc;
					monitor.log("Server: Received request [" + request.getName() + " : " + request.getUrl() + "]");
					//export the fuzz servers utilities into the request bean
					request.setUtils(utils);
					try{
					//Add the request to the fuzzing queue, then send success or failure response
					addRequestToQueue(request);
						output.writeObject(new FuzzResponseBean(true));
					}catch (Exception e){
						output.writeObject(new FuzzResponseBean(false));
					}
				} else {
					//handle all non-fuzz request commands
					monitor.log("Server: Received command");
					commBean = (CommunicationBean)inc;
					controller.handleRequest(commBean.getCommand(), commBean.getParams(), utils);
				}
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {				
				System.out.println("ERROR WITH COMM BEAN");
				e1.printStackTrace();
			}
		}
		} catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void addRequestToQueue(FuzzRequestBean request){
		//TODO: Possibly replace with a class method for WSDL objects
		if(request.getUrl().contains("wsdl")){
			SoapEnumeratingEngine enumerator = new SoapEnumeratingEngine(request.getUrl(), request);
			Thread newSoapThread = new Thread(enumerator);
			queue.add(newSoapThread);
		} else if (request.getUrl().contains("wadl")){
			//HANDLE REST HERE
		}
		else {
			//Create a new crawling thread to the queue. The queue watcher will start the thread.
			CrawlerThread newCrawler = new CrawlerThread(request);
			Thread newCrawlerThread = new Thread(newCrawler);
			queue.add(newCrawlerThread);
		}
	}

}
