package com.google.code.fuzzops.webfuzzer.controller;

import com.google.code.fuzzops.webfuzzer.jFuzz.AbstractDataGenModule;
import com.google.code.fuzzops.webfuzzer.jFuzz.BasicGenModule;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.google.code.fuzzops.webfuzzer.applet.FuzzRequestBean;

public class DataGenerationController {
	
	ArrayList<Class> dataGenModules;
	ArrayList badChars;
	int iterator;
	
	public DataGenerationController(){
		iterator = 0;
		badChars = new ArrayList();
		for(int i = 0; i < FuzzEngine.BAD_CHAR_DEFAULT.length; i++){
			badChars.add(FuzzEngine.BAD_CHAR_DEFAULT[i]);
		}
		dataGenModules = new ArrayList<Class>();
		dataGenModules.add(BasicGenModule.class);
	}
	

	public AbstractDataGenModule getNextModule(){
		if(hasNext()){
			try {
			AbstractDataGenModule module = (AbstractDataGenModule) dataGenModules.get(iterator++).newInstance();
			module.badChars = badChars;
			return module;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}else
			return null;
	}
	
	public boolean hasNext(){
		if (iterator < dataGenModules.size())
			return true;
		else
			return false;
	}
	
	public int getModuleListSize(){
		return dataGenModules.size();
	}
}
