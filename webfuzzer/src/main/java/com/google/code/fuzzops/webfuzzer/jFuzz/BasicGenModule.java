package com.google.code.fuzzops.webfuzzer.jFuzz;

import com.google.code.fuzzops.webfuzzer.controller.FuzzEngine;


public class BasicGenModule extends AbstractDataGenModule{

	String lastData = "";
	String currentData = "";
	int size = FuzzEngine.DATA_INITIAL_LENGTH;
	char[] lastFuzzDiff;
	@SuppressWarnings("unchecked")
	int charsToTest;

	@SuppressWarnings("unchecked")
	public BasicGenModule(){
	}

	//This is to be used on the first generation of data
	public String generate() {
		lastData = currentData;
		currentData = FuzzBuilder.generateData(size, seed);
		//If the length of the url is too long, then ressed and regenerated data.
		//TODO: In the future, you may want to change the use of the HttpURLConnection. This would allow you to bypass the 750 character URL
		//      restriction by generating requests in a manner other than using a URL.
		if(size >= FuzzEngine.URL_MAX_LENGTH){
			seed = scrub(FuzzBuilder.generateData(5, seed));
			size = 0;
		}
		size += FuzzEngine.DATA_INCREMENTING_LENGTH;
		return scrub(currentData);
	}

	@SuppressWarnings("unchecked")
	public String generate(int returnCode) {
		if(charsToTest > 0){
			if(returnCode  != 200)
				try{
					badChars.add(lastFuzzDiff[lastFuzzDiff.length - (charsToTest+1)]);
				}catch (ArrayIndexOutOfBoundsException aE){
					badChars.add(lastFuzzDiff[lastFuzzDiff.length - (charsToTest)]);
				}
			return String.valueOf(lastFuzzDiff[lastFuzzDiff.length - charsToTest--]);
		} else {
			if(returnCode != 200){
				//If the connection gave anything but a 200 request, then determine what new character caused the issue
				lastFuzzDiff = currentData.substring(lastData.length()).toCharArray();
				if(lastFuzzDiff.length <= 0){
					lastFuzzDiff = new char[]{'a'};
				}
				charsToTest = lastFuzzDiff.length;
				return String.valueOf(lastFuzzDiff[lastFuzzDiff.length - charsToTest]);
			}
			else{
				return generate();
			}
		}
	}

	//Inherited scrub() method

}
