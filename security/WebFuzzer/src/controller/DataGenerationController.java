package controller;

import jFuzz.AbstractDataGenModule;
import jFuzz.BasicGenModule;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

import applet.FuzzRequestBean;

public class DataGenerationController {
	
	ArrayList<Class> dataGenModules;
	ArrayList badChars;
	int iterator;
	
	public DataGenerationController(){
		iterator = 0;
		dataGenModules = new ArrayList<Class>();
		dataGenModules.add(BasicGenModule.class);
	}
	

	public AbstractDataGenModule getNextModule(){
		if(hasNext()){
			try {
			return (AbstractDataGenModule) dataGenModules.get(iterator++).newInstance();
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
