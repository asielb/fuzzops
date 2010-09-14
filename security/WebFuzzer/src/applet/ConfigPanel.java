package applet;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class ConfigPanel extends JPanel {

	FuzzerInfo fuzzerInfo;
	String instructionString = "\nThis tab contains configuration information about the fuzzing engine\n";
	
	
	public ConfigPanel(final FuzzerInfo fuzzerInfo) {
		this.fuzzerInfo = fuzzerInfo;
		
		JTextArea instructions = new JTextArea();
		instructions.setEditable(false);
		instructions.append(instructionString);
		instructions.setBackground(getBackground());
		instructions.setFont(new Font(getFont().getName(), Font.ITALIC, 13));
		add(instructions);
		
		JPanel smallPanel = new JPanel();
		smallPanel.setLayout(new BoxLayout(smallPanel, BoxLayout.PAGE_AXIS));
		
		JLabel hostLabel = new JLabel("Host:");
		JLabel portLabel = new JLabel("Port:");
		
		final JTextField hostInput = new JTextField(fuzzerInfo.getHost());
		final JTextField portInput = new JTextField(fuzzerInfo.getPort());
		
		JPanel buttonPanel = new JPanel();
		
		JButton save = new JButton("Save");
		JButton cancel = new JButton("Cancel");
		
		save.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!hostInput.getText().equals(fuzzerInfo.getHost()) && !portInput.getText().equals(fuzzerInfo.getPort())){
					int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to save?");
					if(choice == JOptionPane.OK_OPTION){
						fuzzerInfo.setHost(hostInput.getText());
						fuzzerInfo.setPort(portInput.getText());
					} else {
						hostInput.setText(fuzzerInfo.getHost());
						portInput.setText(fuzzerInfo.getPort());
					}
				}
			}
		});
		
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 hostInput.setText(fuzzerInfo.getHost());
				 portInput.setText(fuzzerInfo.getPort());
			}
		});
		
		buttonPanel.add(save);
		buttonPanel.add(cancel);
		smallPanel.add(instructions);
		smallPanel.add(hostLabel);
		smallPanel.add(hostInput);
		smallPanel.add(portLabel);
		smallPanel.add(portInput);
		smallPanel.add(buttonPanel);
		add(smallPanel);
	}

}
