

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import controller.FuzzerMonitor;

@SuppressWarnings("serial")
public class UtilBean implements Serializable{

	ObjectOutputStream output;
	ObjectInputStream input;
	File outputFile;
	File dictionary;
	public FuzzerMonitor monitor;
	
	public UtilBean(ObjectOutputStream out, ObjectInputStream in, File folder){
		output = out;
		input = in;
		outputFile = folder;
	}

	
	public UtilBean(File folder, FuzzerMonitor monitor2) {
		this.monitor = monitor2;
		this.outputFile = folder;
	}


	public ObjectOutputStream getOutput() {
		return output;
	}

	public void setOutput(ObjectOutputStream output) {
		this.output = output;
	}

	public ObjectInputStream getInput() {
		return input;
	}

	public void setInput(ObjectInputStream input) {
		this.input = input;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public void setDictionary(File dictionary){
		//TODO: Set this to a file eventually
		this.dictionary = dictionary;
	}
	
	public File getDictionary() {
		return dictionary;
	}
	
}
