package com.google.code.fuzzops.webfuzzer.crawler;

import java.util.ArrayList;
import java.util.List;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.plugin.PreCrawlingPlugin;
import com.crawljax.core.state.Identification;
import com.crawljax.forms.FormHandler;
import com.crawljax.forms.FormInput;

public class LoginPlugin implements PreCrawlingPlugin{

	public final String UNFIELD = new String("userName");
	public final String PWFIELD = new java.lang.String("j_password");
	
	String username;
	String password;
	
	public LoginPlugin(String un, String pw){
		this.username = un;
		this.password = pw;
	}
	
	public void preCrawling(EmbeddedBrowser browser) {
		
		List<FormInput> formInputs = new ArrayList<FormInput>();
		
		// LDS Account field is "userName"
		Identification unId = new Identification();
		unId.setValue(UNFIELD);
		unId.setHow(Identification.How.id);
		formInputs.add(new FormInput("text", unId, username)); 
		// LDS Account field is "j_password"
		Identification pwId = new Identification();
		pwId.setValue(PWFIELD);
		pwId.setHow(Identification.How.id);
		formInputs.add(new FormInput("text", pwId, password)); 
		
		// fill in login values 
		FormHandler formHandler = new FormHandler(browser, null, false); 
		formHandler.handleFormElements(formInputs); 
	}

	
	
}
