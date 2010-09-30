package com.google.code.fuzzops.webfuzzer.jFuzz;

import java.util.ArrayList;

import com.google.code.fuzzops.webfuzzer.beans.CrawlerOutputBean;

public class FuzzThread implements Runnable {

	/*Socket userConnection;
	ObjectInputStream crawlerInput;
	ObjectOutputStream crawlerOutput;
	ObjectInputStream reportInput;
	ObjectOutputStream reportOutput;*/
	CrawlerOutputBean fuzzTarget;

	public FuzzThread(ArrayList<String> urlsArray) {
		
	}

	public void run() {
		fuzzProcess(fuzzTarget);
	}

	private void fuzzProcess(CrawlerOutputBean fuzzTarget2) {
		//Do stuff with the results from the crawler
	}

	//Method for setting up I/O with the crawler. May not need the output to the crawler. Depends.
	/*private void establishCrawlerIO() throws Exception {
		try {
			crawlerInput = new ObjectInputStream(userConnection.getInputStream());
			System.out.println("SERVER: Session Input established with Crawler");
			try {
				crawlerOutput = new ObjectOutputStream(userConnection.getOutputStream());
				System.out.println("SERVER: Session Output established with Crawler");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception ev) {
				throw ev = new Exception("SERVER: Session output cannot be established with crawler");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception en) {
			throw en = new Exception("SERVER: Session input cannot be established with crawler");
		}
	}*/

}
