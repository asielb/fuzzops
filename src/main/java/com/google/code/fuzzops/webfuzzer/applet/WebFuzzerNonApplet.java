package com.google.code.fuzzops.webfuzzer.applet;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class WebFuzzerNonApplet extends JFrame {

	private static final long serialVersionUID = 1L;
	FuzzerInfo fuzzerInfo = new FuzzerInfo("127.0.0.1","9991");
	
	public WebFuzzerNonApplet(){
		init();
	}
	
	public void init() {
		try{
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					JTabbedPane tabbedPane = new JTabbedPane();
					//Create the help tab
					String helpString = "This is the Web Application Fuzzer (Beta). Ensure configuration is accurate and then issue a request. Check back later\n to discover your results." 
					                    + "\n\n\n ";
					
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
					setSize(850,550);
					setVisible(true);
					
				}
				
			});
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("FAILED");
		}
	}
	
	public static void main(String[] args){
		new WebFuzzerNonApplet();
	}
}
