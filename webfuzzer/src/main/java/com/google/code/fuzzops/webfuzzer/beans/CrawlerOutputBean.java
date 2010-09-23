package com.google.code.fuzzops.webfuzzer.beans;

import java.util.ArrayList;

public class CrawlerOutputBean {

	ArrayList<String> urls;
	
	public CrawlerOutputBean(ArrayList<String> urlsArray) {
		urls = urlsArray;
	}

}
