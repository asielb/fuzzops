

import java.io.Serializable;

@SuppressWarnings("serial")
public class FuzzResponseBean implements Serializable {

	boolean result;
	
	public FuzzResponseBean(boolean result){
		this.result = result;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
	
}
