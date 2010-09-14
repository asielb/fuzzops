import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import applet.ResultBean;

import org.apache.commons.collections.map.MultiValueMap;

@SuppressWarnings("serial")
public class ResultsPanel extends JPanel {

	String[] fileList = {"----"};
	String[] methodsList = {"----"};
	public final String[] columnNames = {"Code", "Message", "Method", "Url"};
	
	JTextArea stats;
	JTextArea info;
	JComboBox files;
	JComboBox methods;
	DefaultComboBoxModel filesModel;
	DefaultComboBoxModel methodsModel;
	InteractiveTableModel tableModel;
	
	FuzzerInfo fuzzerInfo;
	ObjectInputStream input;
	ObjectOutputStream output;
	Socket conn = null;
	CommunicationBean request;
	CommunicationBean response;
	int flag = 0;
	
	FileInputStream fInput;
	FileOutputStream fOutput;
	ObjectInputStream oInput;
	ObjectOutputStream oOutput;
	String currentFileName;
	ArrayList<ResultBean> currentFile;
	@SuppressWarnings("unchecked")
	ArrayList requestParams;
	ArrayList<ResultBean> resCol;
	JTable table;

	HashMap<Integer, ResultBean> map;
	MultiValueMap results;
	
	public ResultsPanel(FuzzerInfo fuzzerInfo){
		this.fuzzerInfo = fuzzerInfo;
		
	//JScrollPane and JTable
		tableModel = new InteractiveTableModel(columnNames);
		table = new JTable(tableModel);
		JScrollPane scroll = new JScrollPane(table);
		
	//Refresh file list
		JButton refresh = new JButton("Refresh List");
		refresh.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileList = (String[]) sendRequest("refresh", null).getParams().get(0);
				filesModel.removeAllElements();
				for(int i =0; i < fileList.length; i++){
					filesModel.addElement(fileList[i]);
				}
			}
			
		});
	//Request selected file
		JButton request = new JButton("Request File");
		request.addActionListener(new ActionListener(){
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				requestParams = new ArrayList();
				requestParams.add((String)files.getSelectedItem());
				response = sendRequest("request",requestParams);
				
				currentFile = (ArrayList<ResultBean>)response.getParams().get(0);
				//File has an arraylist<ResultBean>
				map = new HashMap<Integer, ResultBean>();
				results = MultiValueMap.decorate(map);
				
				//populate multimap
				for(int i=0; i<currentFile.size();i++){
					results.put(currentFile.get(i).getCode(), currentFile.get(i));
				}
				
				Set keySet = results.keySet();
				Object[] setArray = keySet.toArray();
				ArrayList<String> alKeys = new ArrayList<String>();
				for(int i =0; i<setArray.length;i++){
					Integer num = (Integer) setArray[i];
					alKeys.add(Integer.toString(num));
				}
				methodsList = alKeys.toArray(new String[alKeys.size()]);
				
				methodsModel.removeAllElements();
				for(int i = 0; i < methodsList.length; i++){
					methodsModel.addElement(methodsList[i]);				
				}
				
				//methodsModel.setSelectedItem(0);
				
			}
		});
		
	//Dropdown
		 //fileList = (String[])sendRequest("refresh", null).getParams().get(0);
		 files = new JComboBox(fileList); 
		 filesModel = (DefaultComboBoxModel)files.getModel();
		 
		
	//Method Dropdown
		methods = new JComboBox(methodsList);
		methodsModel = (DefaultComboBoxModel)methods.getModel();
		methods.addActionListener(new ActionListener(){

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent arg0){
				if (methods.getSelectedItem() != null){
				
					try{
						resCol = (ArrayList<ResultBean>) results.getCollection(Integer.parseInt((String)methods.getSelectedItem()));
						System.out.println(resCol.size());
						tableModel.setContent(resCol);
						}catch(Exception e){
						}
				} else {
				}
			}
			
		});
		
		//Statistics Box
		stats = new JTextArea();
		stats.setEditable(false);
		stats.setBorder(BorderFactory.createLoweredBevelBorder());
		
	//Delete file
		JButton delete = new JButton("Delete");
		delete.addActionListener(new ActionListener(){

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(JOptionPane.showConfirmDialog(null, "This action will delete this fuzz from the server! It WILL be lost!\n Are you sure?") == JOptionPane.OK_OPTION){
					requestParams = new ArrayList();
					requestParams.add((String)files.getSelectedItem());
					response = sendRequest("delete", requestParams);
					if ((Boolean)response.getParams().get(0)){
						tableModel.setContent(new ArrayList<ResultBean>());
						methodsModel.removeAllElements();
						methodsModel.addElement(new String("----"));
						filesModel.removeElement(files.getSelectedItem());
						/*fileList = (String[]) sendRequest("refresh", null).getParams().get(0);
						filesModel.removeAllElements();
						for(int i =0; i < fileList.length; i++){
							filesModel.addElement(fileList[i]);
						}*/
						JOptionPane.showMessageDialog(null, "Success");
					} else {
						JOptionPane.showMessageDialog(null, "Failed");
					}
				}
			}
			
		});
		
	//Save Changes results
		//TODO: JButton save = new JButton("Save Changes");
		
	//Save results
		JButton download = new JButton("Download");
		download.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				@SuppressWarnings("unused")
				ExcelExporter excel = new ExcelExporter(table, (String)files.getSelectedItem(), false);
			}
			
		});
		
	//Build GUI
		
		//JPanel body = new JPanel();
		//body.setLayout(new BoxLayout(body,BoxLayout.PAGE_AXIS));
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		
		//HEADER
		JPanel header = new JPanel();
		header.setLayout(new GridLayout(2,1));
		
		JPanel filePanel = new JPanel();
		filePanel.add(files);
		
		JPanel fileButtons = new JPanel();
		fileButtons.add(refresh);
		fileButtons.add(request);
		
		header.add(filePanel);
		header.add(fileButtons);
		//body.add(header);
		add(header);
		
		//RESULTS
		//body.add(scroll);
		add(methods);
		add(scroll);
		
		//STATS
		//body.add(stats);
		//add(stats);
		
		//BOTTOM
		JPanel bot = new JPanel();
		bot.add(delete);
		//bot.add(save);
		bot.add(download);
		//body.add(bot);
		add(bot);
		
		//add(body);
		
	}
	
	@SuppressWarnings("unchecked")
	private CommunicationBean sendRequest(String string, ArrayList params) {

		if(flag == 0 || conn.isClosed()){
			try {
				conn = new Socket(fuzzerInfo.getHost(), fuzzerInfo.getPortAsInt());
				output = new ObjectOutputStream(conn.getOutputStream());
				input = new ObjectInputStream(conn.getInputStream());
				flag = 1;
				sendRequest(string, params);
			} catch (UnknownHostException e) {
				JOptionPane.showMessageDialog(null, "Failed to connect to server!");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Failed communication with server!");
			}
		}
			request = new CommunicationBean(string, params);
			try {
				output.writeObject(request);
				try {
					response = (CommunicationBean) input.readObject();
					return response;
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Unrecognized Response!");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Failed to recieve response");
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Failed to send " + string + " request!");
			}				System.out.println("CLIENT: output stream start");
			System.out.println("FAILED");
			return request;
			
	}
	
}
