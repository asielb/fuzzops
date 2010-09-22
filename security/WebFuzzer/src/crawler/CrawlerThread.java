package crawler;


import java.util.ArrayList;
import java.util.HashSet;
import javax.naming.ConfigurationException;
import applet.FuzzRequestBean;
import com.crawljax.browser.EmbeddedBrowser.BrowserType; 
import com.crawljax.core.CrawljaxController;
import com.crawljax.core.CrawljaxException;
import com.crawljax.core.configuration.CrawlSpecification;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.InputSpecification;

import controller.FuzzEngine;


/*CrawlerThread.java
 * 
 * Crawljax recommends that instances of the crawler should be their own thread. This thread therefore handles the crawling of a web application.
 * 
 */

public class CrawlerThread implements Runnable{

	public static final String REPLACE_VALUE = "TESTHERE";
	public static final String[] ELEMENTS = {"span","div"};
	
	//Crawljax settings
	private CrawlSpecification crawlerSpec;
	private CrawljaxConfiguration crawlerConfig;
	CrawljaxController crawler;
	
	//Objects relating to the request, the results, and the fuzzer (respectively)
	private FuzzRequestBean request;
	private HashSet<String> urls;
	ArrayList<String> urlsArray;
	FuzzEngine fuzzer;
	
	//Cross Object references to main AppCrawler
	protected ArrayList<String> resultsList;	
	
	public CrawlerThread(FuzzRequestBean request){
		this.request = request;
		this.crawlerConfig = new CrawljaxConfiguration();
		urls = new HashSet<String>();
		fuzzer = new FuzzEngine(request);
	}
	
	@Override
	public void run() {
		//Get the next request
		if(request == null){
			return;
		} else {
			//Process request
			try {
				configCrawler(request.getUrl());
			} catch (ConfigurationException e) {
				e.printStackTrace();
			} catch (CrawljaxException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void configCrawler(String url) throws Exception{
		
		//Set the specification of which HTML elements to click
		crawlerSpec = new CrawlSpecification(url);
		crawlerSpec.clickDefaultElements();
		for(int i = 0; i < ELEMENTS.length; i++){
			crawlerSpec.click(ELEMENTS[i]);
		}
		//Dont exercise any logout buttons
		crawlerSpec.dontClick("a").withText("Logout");
		//TODO: blacklist/whitelist. Same Origin
		
		//Only set depth if user does so. If none supplied, it will go for a loooong time.
		if(request.getDepth() != 0){
			crawlerSpec.setDepth(request.getDepth());
		}
		
		//Only set time if user does so. If none suppolied, it will go until it has completely crawled the site.
		if(request.getTimeCrawl() != 0){
			crawlerSpec.setMaximumRuntime(request.getTimeCrawl());
		}
		
		//Compile the Spec and Input into the Config
		if(!(request.getUsername().equals("") && request.getPassword().equals(""))){
			crawlerConfig.addPlugin(new LoginPlugin(request.getUsername(), request.getPassword()));
			InputSpecification input = new InputSpecification();
			input.field(request.getUnameField()).setValue(request.getUsername());
			input.field(request.getPassField()).setValue(request.getPassword());
			crawlerSpec.setInputSpecification(input);
		}
		
		
		//Sets the browser to use
		
		if(request.getBrowser().equals("IE")){
			crawlerConfig.setBrowser(BrowserType.ie);
		}else if(request.getBrowser().equals("Chrome")){
			crawlerConfig.setBrowser(BrowserType.chrome);
		}else{
			crawlerConfig.setBrowser(BrowserType.firefox);
		}
		//Add the plugins to:
		//   - Retrieve requests
		crawlerConfig.addPlugin(new GenerateRequestsPlugin(urls));
		crawlerConfig.setCrawlSpecification(crawlerSpec);
		
		try {
			crawler = new CrawljaxController(crawlerConfig);
			//Run the crawler
			request.getUtils().monitor.log("Crawling...");
			crawler.run();
			urlsArray = new ArrayList<String>(urls);
			for(int i = 0; i < urls.size(); i++){
				request.getUtils().monitor.log(urlsArray.get(i));
			}
			//take the results, and start fuzzing them
			initFuzzer(urlsArray);
			
		} catch (CrawljaxException e) {
		    e.printStackTrace();
		    request.getUtils().monitor.log("Failed to crawl");
		} catch (org.apache.commons.configuration.ConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void initFuzzer(ArrayList<String> urlsArray) {
		fuzzer.fuzz(urlsArray);
	}

	
}
