import javax.swing.JPanel;
import javax.swing.JTextArea;

import applet.ResultBean;


@SuppressWarnings("serial")
public class AnalysisPanel extends JPanel{

	public JTextArea contentText;
	
	public AnalysisPanel(ResultBean selectedRow){
		
		contentText = new JTextArea();
		
	}
	
	public void updateAnalysis(){
		
	}
	
}
