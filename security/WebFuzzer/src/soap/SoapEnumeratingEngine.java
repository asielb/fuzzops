package soap;


import java.io.IOException;
import java.util.ArrayList;

import org.apache.xmlbeans.XmlException;

import applet.FuzzRequestBean;

import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.WsdlSubmit;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.iface.Request.SubmitException;
import com.eviware.soapui.support.SoapUIException;
import controller.FuzzEngine;
import crawler.CrawlerThread;


public class SoapEnumeratingEngine implements Runnable{

	final String replaceRegex = "\\>\\?\\<";
	
	ArrayList<WsdlRequest> soapRequests;
	ArrayList<String> soapRequestsAsStrings;
	String wsdl;
	//Used to enumerate the WSDL
	WsdlProject project;
	WsdlInterface[] ifaces;
	WsdlInterface iface;
	WsdlOperation operation;
	WsdlRequest request;
	@SuppressWarnings("unchecked")
	WsdlSubmit submit;
	Response response;
	String file;
	
	//Request information
	FuzzRequestBean fuzzRequest;
	FuzzEngine fuzzer;
	
	public SoapEnumeratingEngine(String wsdl){
		this.wsdl = wsdl;
		soapRequests = new ArrayList<WsdlRequest>();
		soapRequestsAsStrings = new ArrayList<String>();
		project = null;
		ifaces = null;
		iface = null;
		operation = null;
		request = null;
		submit = null;
		response = null;
		fuzzer = null;
	}
	
	public SoapEnumeratingEngine(String wsdl, FuzzRequestBean fuzzRequest){
		this(wsdl);
		this.fuzzRequest = fuzzRequest;
		soapRequests = new ArrayList<WsdlRequest>();
		soapRequestsAsStrings = new ArrayList<String>();
		fuzzer = new FuzzEngine(fuzzRequest);
	}
	
	public void run(){
		this.setUrl(wsdl);
		for (int i = 0; i < ifaces.length; i++){
			this.setInterface(i);
			for ( int ops = 0; ops < iface.getOperationCount(); ops++ ){
				this.setOperation(ops);
				for (int req = 0; req < operation.getRequestCount(); req++){
					this.setRequest(req);
					//Checks if credentials are set, if they are, sets them for ws-security.
					if((!fuzzRequest.getPassword().equals("")) || !(fuzzRequest == null)){
						request.setWssPasswordType(WsdlRequest.PW_TYPE_DIGEST);//TODO: Default is Digest.
						request.setPassword(fuzzRequest.getPassword());
					}
					if((!fuzzRequest.getUsername().equals("")) || !(fuzzRequest == null)){
						request.setUsername(fuzzRequest.getUsername());
					}
					soapRequests.add(request);
					soapRequestsAsStrings.add(operation.createRequest(true));
				}
			}
		}
		fuzzRequest.getUtils().monitor.log("WSDL Operations Found: " + soapRequests.size());
		fuzzer.fuzz(soapRequests);
	}
	
	
	//Sets the url of the wsdl
	void setUrl(String str){
		try{
			project = new WsdlProject();
			ifaces = WsdlInterfaceFactory.importWsdl(project, str, true);
		} catch (SoapUIException e){
			System.out.println(e.getMessage());
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Sets the current interface out of all the interfaces in the wsdl
	void setInterface(int num){
		iface = ifaces[num];
	}
	
	//Sets current operation to a specific operation within an interface
	void setOperation(int num){
		operation = (WsdlOperation) iface.getOperationAt(num);
	}
	
	
	//Can be used to set the xml of the request
	void setRequest(int num){
		request = operation.getRequestAt(num);
		//request.setRequestContent(operation.createRequest(true).replace(">?<", ">"+CrawlerThread.REPLACE_VALUE+"<"));
		//request.setRequestContent();
	}
	
	//Sends the configured SOAP request
	@SuppressWarnings("unchecked")
	void sendRequest(){
		try{
			submit = (WsdlSubmit)request.submit(new WsdlSubmitContext(request), false);
		} catch (SubmitException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	//Gets the response
	String getResponse(){
		String content = null;
		
		try{
			response = submit.getResponse();
			content = response.getContentAsString();
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return content;
	}
	
	public static void main(String[] args){
		SoapEnumeratingEngine test = new SoapEnumeratingEngine("http://10.118.195.95:9080/sign-in/ldsa-ws/v1.2.0/Services/soap/LdsAccountService?wsdl");
		test.setUrl(test.wsdl);
		test.setInterface(0);
		test.setOperation(0);
		test.setRequest(0);
		test.sendRequest();
		//System.out.println(test.getResponse());
		/*StringToStringMap respHead =  test.response.getResponseHeaders();
		String[] respHeadKeys = respHead.getKeys();
		for(int i = 0; i<respHeadKeys.length;i++){
			System.out.println(respHeadKeys[i]);
			System.out.println(respHead.get(respHeadKeys[i]));
		}*/
	}
	
}
