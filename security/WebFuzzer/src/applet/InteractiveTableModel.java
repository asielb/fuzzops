package applet;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;
import org.apache.commons.collections.map.MultiValueMap;



@SuppressWarnings("serial")
public class InteractiveTableModel extends AbstractTableModel{
	public static final int CODE_INDEX = 0;
	public static final int MESSAGE_INDEX = 1;
	public static final int METHOD_INDEX = 2;
	public static final int URL_INDEX = 3;
	public static final int HIDDEN_INDEX = 4;
	
	protected String[] columnNames;
	protected ArrayList<ResultBean> dataArray;
	protected MultiValueMap results;

	public InteractiveTableModel(String[] columnNames){
		this.columnNames = columnNames;
		dataArray = new ArrayList<ResultBean>();
	}
	
	/*public InteractiveTableModel(String[] columnNames, ArrayList<ResultBean> data){
		this.columnNames = columnNames;
		dataArray = data;
	}*/
	
	public boolean isCellEditable(int row, int column) {
		if (column == HIDDEN_INDEX) return false;
		else return true;
	}
	
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int column){
		switch (column) {
			case CODE_INDEX:
			case MESSAGE_INDEX:
			case METHOD_INDEX:
			case URL_INDEX:
				return String.class;
			default:
				return Object.class;
		}
	}
	
	public String getColumnName(int column){
		return columnNames[column];
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return dataArray.size();
	}

	public Object getValueAt(int row, int column) {
		ResultBean result = (ResultBean)dataArray.get(row);
		switch (column) {
		case CODE_INDEX:
			return result.getCode();
		case MESSAGE_INDEX:
			return result.getMessage();
		case METHOD_INDEX:
			return result.getMethod();
		case URL_INDEX:
			return result.getUrl();
		default:
			return new Object();
		}
	}
	
	public void setValueAt(Object value, int row, int column){
		ResultBean result = (ResultBean)dataArray.get(row);
		switch(column) {
		case CODE_INDEX:
			result.setCode((Integer) value);
			break;
		case MESSAGE_INDEX:
			result.setMessage((String) value);
			break;
		case METHOD_INDEX:
			result.setMethod((String) value);
			break;
		case URL_INDEX:
			result.setUrl((String) value);
		default:
			System.out.println("invalid index");
		}
		fireTableCellUpdated(row, column);
	}
	
	public boolean hasEmptyRow(){
		if(dataArray.size() == 0) return false;
		ResultBean result = (ResultBean)dataArray.get(dataArray.size() - 1);
		if( result.getMessage().trim().equals("") &&
			result.getMethod().trim().equals("") &&
			result.getUrl().trim().equals(""))
		{
			return true;
		}
		else return false;
	}
	

	public void setContent(ArrayList<ResultBean> resCol) {
		dataArray = resCol;
		fireTableStructureChanged();
	}
	
	/*public void addEmptyRow() {
		dataVector.add(new ResultBean());
		fireTableRowsInserted(
			dataVector.size() - 1,
			dataVector.size() - 1);
	}*/
}
	

