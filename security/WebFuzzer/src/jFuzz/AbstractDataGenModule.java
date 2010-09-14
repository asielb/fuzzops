package jFuzz;

import java.util.ArrayList;

import controller.FuzzEngine;



public abstract class AbstractDataGenModule {

	@SuppressWarnings("unchecked")
	public ArrayList badChars;
	public String seed;
	
	abstract public String generate();

	abstract public String generate(int response);
	
	public String scrub(String str){
		for(int j = 0; j < FuzzEngine.BAD_CHAR_DEFAULT.length;j++)
			str = str.replaceAll(FuzzEngine.BAD_CHAR_DEFAULT[j], "");
		for(int i = 0; i < badChars.size(); i++)
			str = str.replaceAll(String.valueOf(badChars.get(i)),"");
		return str;
	}
	
}
