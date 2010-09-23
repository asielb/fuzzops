package com.google.code.fuzzops.webfuzzer.controller;

/*QueueWatcher.java
 * 
 * This is used to start the threads of fuzz requests.
 * Established the thread management using a queue to allow the throttling of how many threads may run at once.
 */

import java.net.ServerSocket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueWatcher implements Runnable{

	ServerSocket serSock;
	ConcurrentLinkedQueue<Thread> queue;
	
	public QueueWatcher(ServerSocket ser, ConcurrentLinkedQueue<Thread> qu){
		serSock = ser;
		queue = qu;
	}
	
	public void run() {
		while(serSock.isBound()){
			if(!queue.isEmpty()){
				queue.poll().start();
			}
		}
	}

}
