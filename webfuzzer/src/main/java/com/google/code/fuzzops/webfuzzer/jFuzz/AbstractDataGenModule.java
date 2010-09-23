package com.google.code.fuzzops.webfuzzer.jFuzz;

import java.util.ArrayList;

import com.google.code.fuzzops.webfuzzer.controller.FuzzEngine;



public abstract class AbstractDataGenModule {

	@SuppressWarnings("unchecked")
	public ArrayList badChars;
	public String seed;
	
	abstract public String generate();

	abstract public String generate(int response);
	
	public String scrub(String str){
		for(int j = 0; j < FuzzEngine.BAD_CHAR_DEFAULT.length;j++)
			str = str.replace(FuzzEngine.BAD_CHAR_DEFAULT[j], "");
		for(int i = 0; i < badChars.size(); i++)
			str = str.replace(String.valueOf(badChars.get(i)),"");
		return str;
	}
	
}
