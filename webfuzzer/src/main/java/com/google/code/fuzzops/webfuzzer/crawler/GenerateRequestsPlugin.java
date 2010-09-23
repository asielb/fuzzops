package com.google.code.fuzzops.webfuzzer.crawler;

import java.util.ArrayList;
import java.util.Set;

import com.crawljax.core.CrawlSession;
import com.crawljax.core.plugin.OnNewStatePlugin;

/*GenerateRequestsPlugin.java
 * 
 * this class is used to collect exercised urls as the DOM object state changes during crawling process.
 */

public class GenerateRequestsPlugin implements OnNewStatePlugin {

	Set<String> urls;
	
	public GenerateRequestsPlugin(Set<String> urls2) {
		this.urls = urls2;
	}

	public void onNewState(CrawlSession arg0) {
		//Modifies and adds discovered urls to the result set.
		ArrayList<String> results = prepareUrl(arg0.getCurrentState().getUrl());
		for(int i = 0; i < results.size() ; i++){
			urls.add(results.get(i));
		}
		
	}

	//This processes modualrizes the URL to allow you to insert the replace value into parameter value areas.
	private static ArrayList<String> prepareUrl(String url){
		ArrayList<String> results = new ArrayList<String>();
		ModularUrl modUrl = new ModularUrl(url);
		if (modUrl.getParams() != null){
			for(int i=0; i < modUrl.getParams().size(); i++){
				modUrl.getParams().get(i).setValue(CrawlerThread.REPLACE_VALUE);
				results.add(modUrl.getCompleteUrl());
			}
		}
		return results;
	}

}
