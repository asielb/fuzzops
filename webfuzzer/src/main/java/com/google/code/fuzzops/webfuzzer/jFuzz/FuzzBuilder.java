package com.google.code.fuzzops.webfuzzer.jFuzz;

public class FuzzBuilder { 
	
	static enum dataType {word, number, uri, base64};
	static FuzzRnd rnd;
	String[] dict;
	String seed;
	
	public FuzzBuilder(String[] dict, String seed) {
		this.seed = seed;
		//Add a dictionary here.
	}
	
	
	public String generateData(int length){
		rnd = new FuzzRnd(this.seed);
		byte[] dat = new byte[length];
		rnd.generateBytes(dat);
		return new String(dat);
	}
	
	public static String generateData(int length, String seed){
		rnd = new FuzzRnd(seed);
		byte[] dat = new byte[length];
		rnd.generateBytes(dat);
		return new String(dat);
	}
	//Build other structs that will hard code in specific items. i.e http://, etc. Maybe pass this as a dictionary?
	
}
