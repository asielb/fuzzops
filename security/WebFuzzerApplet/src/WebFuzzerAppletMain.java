
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


@SuppressWarnings("serial")
public class WebFuzzerAppletMain extends JApplet {

	FuzzerInfo fuzzerInfo = new FuzzerInfo("127.0.0.1","9991");
	
	
	public void init() {
		try{
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					JTabbedPane tabbedPane = new JTabbedPane();
					//Create the help tab
					String helpString = "This is the Web Application Fuzzer (Beta). Ensure configuration is accurate and then issue a request. Check back later\n to discover your results." 
					                    + "\n\n\n Contact Skyler Onken sonken@ldschurch.org with any questions.";
					
					JPanel helpPanel = new JPanel();
					JTextArea helpText = new JTextArea();
					helpText.append(helpString);
					helpText.setBackground(getBackground());
					helpText.setEditable(false);
					helpPanel.add(helpText);
					
					JComponent helpPanelComponent = helpPanel;
					tabbedPane.addTab("Help", helpPanelComponent);
					
					//Create config tab
					JComponent configPanelComponent = new ConfigPanel(fuzzerInfo);
					tabbedPane.addTab("Configurations", configPanelComponent);
					
					//Create the Request tab
					JComponent requestPanelComponent = new RequestPanel(fuzzerInfo);
					tabbedPane.addTab("Request Fuzz",requestPanelComponent);
					
					//Create the Results tab
					JComponent viewResultPanelComponent = new ResultsPanel(fuzzerInfo);
					tabbedPane.addTab("View Results",viewResultPanelComponent);
					
					//Main pane info
					add(tabbedPane);
					setSize(850,500);
					
				}
				
			});
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("FAILED");
		}
	}
}
