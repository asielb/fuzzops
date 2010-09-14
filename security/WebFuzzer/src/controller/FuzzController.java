package controller;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import applet.CommunicationBean;
import applet.FuzzRequestBean;
import applet.UtilBean;

/*
 * FuzzController.java
 * 
 * main runnable class to start the fuzzing server. It establishes the server socket, as well as the output directory. It then starts
 * threads for each connection (See FuzzControllerThread). 
 * 
 */

public class FuzzController {
	
	//Defaults for receiving fuzz requests
	public final static SimpleDateFormat FORMAT = new SimpleDateFormat("-yyyy-MM-dd-hh.mm.aa");
	public final static int DEFAULT_TIME = 50;
	public final static int DEFAULT_DEPTH= 3;
	
	//Output folder variables
	static File folder;
	static JFileChooser fc;
	FilePermission perm;
	
	//Socket objects
	ServerSocket serSock;
	Socket cliSock;
	
	//Utilities for the fuzz server
	ConcurrentLinkedQueue<Thread> queue;
	UtilBean utils;
	FuzzerMonitor monitor;
	FuzzApplicationController controller;
	
	//objects used in communication
	Object inc;
	CommunicationBean commBean;
	FuzzRequestBean request;
	QueueWatcher qWatcher;
	
	public FuzzController(int port){
		try {
			//Select output folder
			fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			selectOutputDirectory();
			perm = new java.io.FilePermission(folder.getAbsolutePath(), "write");
			
			//Create a monitor
			monitor = new FuzzerMonitor();
			controller = new FuzzApplicationController(monitor);
			
			//Start Server
			serSock = new ServerSocket(port);
			monitor.log("Server: Listening on " + port );
			monitor.log("Server: Output folder set to " + folder.getAbsolutePath());
			
			//Create queue for fuzzing requests.
			queue = new ConcurrentLinkedQueue<Thread>();			
			
			//Start handler
			startThreadWatcher();
			handler();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void startThreadWatcher() {
		//TODO: Starts a watcher that will run threads as they are added to the queue. I spaced this out separately to allow future
		// throttling of threads. This can be added later by using an incrementing variable, or set that is watched.
		qWatcher = new QueueWatcher(serSock, queue);
		Thread qWatcherThread = new Thread(qWatcher);
		qWatcherThread.start();
	}

	private void handler() throws IOException {
		while(serSock.isBound()){
			
			//Establish connection and IO with connecting client.
			cliSock = serSock.accept();
			monitor.log("Server: Connection received");
			
			//Package current state of needed utilities to be passed to new thread
			utils = new UtilBean(folder,monitor);
			
			//Creates the new thread and runs it.
			FuzzControllerThread newCli = new FuzzControllerThread(cliSock, utils, queue, controller);
			Thread newThread = new Thread(newCli);
			newThread.start();
		}
	}
	
	public static void main(String[] args){
		new FuzzController(Integer.parseInt(JOptionPane.showInputDialog("What port to listen on?:")));
	}

	//Used to select output directory
	private static void selectOutputDirectory() {
		int returnVal = fc.showDialog(null, "Select Output Directory");
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			folder = fc.getSelectedFile();
		}else{
			JOptionPane.showMessageDialog(null, "Failed to select valid output directory!");
			System.exit(0);
		}
	}
	
}
