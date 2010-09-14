package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class FuzzerMonitor extends JFrame{

	FuzzConsolePanel consoleComponent;
	
	public FuzzerMonitor(){
		JTabbedPane tabbedPane = new JTabbedPane();
		
		consoleComponent = new FuzzConsolePanel();
		tabbedPane.addTab("Console", consoleComponent);
		
		setTitle("Web Fuzzer Server Monitor");
		add(tabbedPane);
		setSize(850,500);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void log(String text){
		consoleComponent.log(text);
	}
	
	class FuzzConsolePanel extends JPanel{
		
		JTextArea console;
		
		FuzzConsolePanel(){
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			console = new JTextArea("> ", 25, 70);
			console.setEditable(false);
			console.setAutoscrolls(true);
			console.setBorder(BorderFactory.createLoweredBevelBorder());
			JScrollPane scroll = new JScrollPane(console);
			scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e) {
					e.getAdjustable().setValue(e.getAdjustable().getMaximum());
				}
			});
			add(scroll);
			
			JPanel buttons = new JPanel();
			JButton clear = new JButton("Clear");
			clear.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					console.setText("> ");
				}
				
			});
			JButton save = new JButton("Save");
			buttons.add(clear);
			buttons.add(save);
			
			add(buttons);
			pack();
		}
		
		public void log(String string){
			if(getLineCount(console) > 100){
				console.setText(""); //Temporary Fix
			}
			console.append(string + "\n> ");

		}
		
		public int getLineCount (JTextArea _textArea)
		{
		boolean lineWrapHolder = _textArea.getLineWrap();
		_textArea.setLineWrap(false);
		double height = _textArea.getPreferredSize().getHeight();
		_textArea.setLineWrap(lineWrapHolder); 
		double rowSize = height/_textArea.getLineCount();
		return (int) (_textArea.getPreferredSize().getHeight() / rowSize);
		}

		
	}
}
