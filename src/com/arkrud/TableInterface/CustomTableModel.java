package com.arkrud.TableInterface;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.S3Folder;
import com.arkrud.aws.CustomObjects.CFStackTreeNodeUserObject;
import com.arkrud.aws.CustomObjects.CustomAWSObject;
import com.arkrud.aws.CustomObjects.CustomEC2ELB;
import com.arkrud.aws.CustomObjects.CustomEC2ELBV2;
import com.arkrud.aws.CustomObjects.CustomEC2SecurityGroup;
import com.arkrud.aws.CustomObjects.CustomEC2TargetGroup;
import com.arkrud.aws.CustomObjects.CustomRoute53Zone;
import com.arkrud.aws.StaticFactories.EC2Common;
import com.arkrud.aws.StaticFactories.S3Common;

public class CustomTableModel extends AbstractTableModel implements TableModelListener  {
	
	public CustomTableModel(boolean isEditableTable) {
		super();
		this.isEditableTable = isEditableTable;
		addTableModelListener(this);
		

	}

	private static final long serialVersionUID = 1L;
	private ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
	private ArrayList<String> columns = new ArrayList<String>();
	private boolean isFileBrowser;
	private String localPath;
	private boolean isEditableTable;
	private Dashboard dash;

	public void addRow(int row, ArrayList<Object> rowData) {
		data.add(row, rowData);
		fireTableRowsInserted(row, row);

	}

    

	public Dashboard getDash() {
		return dash;
	}



	public void setDash(Dashboard dash) {
		this.dash = dash;
	}



	// Method to adjust column width to longest data in the cell and column
	// header name
	public void adjustColumnPreferredWidths(JTable table) { // NO_UCD (use default)
		TableColumnModel columnModel = table.getColumnModel();
		for (int col = 0; col < table.getColumnCount(); col++) {
			int maxwidth = 0;
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer rend = table.getCellRenderer(row, col);
				Object value = table.getValueAt(row, col);
				Component comp = rend.getTableCellRendererComponent(table, value, false, false, row, col);
				maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
			}
			TableColumn column = columnModel.getColumn(col);
			TableCellRenderer headerRenderer = column.getHeaderRenderer();
			if (headerRenderer == null)
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			Object headerValue = column.getHeaderValue();
			Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0, col);
			maxwidth = Math.max(maxwidth, headerComp.getPreferredSize().width);
			column.setPreferredWidth(maxwidth);
		}
	}

	public void generateAWSAccountsTableData() { // NO_UCD (use default)
		data = INIFilesFactory.getAWSAccountsFromINIConfig(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"), UtilMethodsFactory.getAWSAPIINIConfigs("config"));
	}

	public void generateTableData(AWSAccount account, Object tableObject, String tableIdentifier) { // NO_UCD (use default)
		// ELB tables
		if (tableIdentifier.equals("S3Folders")) {
			data = S3Common.prepareTheTableData((S3Folder) tableObject);
			if (data.size() < 1) {
				ArrayList<Object> rowData = new ArrayList<Object>();
				S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
				s3ObjectSummary.setKey("");
				rowData.add(s3ObjectSummary);
				rowData.add("");
				rowData.add("");
				ArrayList<ArrayList<Object>> epmtyData = new ArrayList<ArrayList<Object>>();
				epmtyData.add(rowData);
				data = epmtyData;
			}
		} else if (tableIdentifier.equals("AWSAccounts")) {
			data = INIFilesFactory.getAWSAccountsFromINIConfig(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"), UtilMethodsFactory.getAWSAPIINIConfigs("config"));
		} else if (tableIdentifier.equals("Applications")) {
			data = INIFilesFactory.getAppTreesConfigInfo(UtilMethodsFactory.getConsoleConfig());
		} else if (tableIdentifier.equals("Ingress")) {
			data = ((CustomEC2SecurityGroup) tableObject).groupPolicyRulesData(tableIdentifier, false);
		} else if (tableIdentifier.equals("EditIngress")) {
			data = ((CustomEC2SecurityGroup) tableObject).groupPolicyRulesData(tableIdentifier, true);
		} else if (tableIdentifier.equals("Egress")) {
			data = ((CustomEC2SecurityGroup) tableObject).groupPolicyRulesData(tableIdentifier, false);
		} else if (tableIdentifier.equals("EditEgress")) {
			data = ((CustomEC2SecurityGroup) tableObject).groupPolicyRulesData(tableIdentifier, true);
		} else if (tableIdentifier.equals("ELBInstances")) {
			data = ((CustomEC2ELB) tableObject).getELBInstancesData();
		} else if (tableIdentifier.equals("ELBNetworking")) {
			data = ((CustomEC2ELB) tableObject).getELBSubnetsData();
		} else if (tableIdentifier.equals("ELBListeners")) {
			data = ((CustomEC2ELB) tableObject).getELBListenersData();
		} else if (tableIdentifier.equals("ELBV2Listeners")) {
			data = ((CustomEC2ELBV2) tableObject).getELBListenersData();
		} else if (tableIdentifier.equals("ELBV2Attributes")) {
			data = ((CustomEC2ELBV2) tableObject).getELBAttributesData();
		} else if (tableIdentifier.equals("ELBSecurityGroups")) {
			data = ((CustomEC2ELB) tableObject).getELBSecurityGroupsData();
		} else if (tableIdentifier.equals("ELBV2SecurityGroups")) {
			data = ((CustomEC2ELBV2) tableObject).getELBSecurityGroupsData();
		} else if (tableIdentifier.equals("ListenersRules")) {
			data = ((CustomEC2ELBV2) tableObject).getELBListenersRulesData();
		} else if (tableIdentifier.equals("Rules Actions")) {
			data = ((CustomEC2ELBV2) tableObject).getELBListenersRulesActionsData();
		} else if (tableIdentifier.equals("Rules Conditions")) {
			data = ((CustomEC2ELBV2) tableObject).getELBListenersRulesConditionsData();
		} else if (tableIdentifier.equals("StackResources")) {
			data = ((CFStackTreeNodeUserObject) tableObject).getStacksResources();
		} else if (tableIdentifier.equals("StackOutputs")) {
			data = ((CFStackTreeNodeUserObject) tableObject).getStacksOutputs();
		} else if (tableIdentifier.equals("StackEvents")) {
			data = ((CFStackTreeNodeUserObject) tableObject).getStacksEvents();
		} else if (tableIdentifier.equals("StackParameters")) {
			data = ((CFStackTreeNodeUserObject) tableObject).getStacksParameters();
		} else if (tableIdentifier.equals("TargetGroupTargets")) {
			data = ((CustomEC2TargetGroup) tableObject).getTGTargetsData();
		} else if (tableIdentifier.equals("TargetGroupAvailabilityZones")) {
			data = ((CustomEC2TargetGroup) tableObject).getTGTargetsAZsData();
		} else if (tableIdentifier.equals("TargetGroupAttributes")) {
			data = ((CustomEC2TargetGroup) tableObject).getTGAttributesData();
		} else if (tableIdentifier.equals("CustomRoute53Zone")) {
			System.out.println("Count: " + ((CustomRoute53Zone) tableObject).getCustomRoute53ZoneTreeNode().getChildCount());
			data = ((CustomRoute53Zone) tableObject).getRoute53ZonesRecordsData(((CustomRoute53Zone) tableObject).getCustomRoute53ZoneTreeNode());
		} else if (tableIdentifier.equals("Tags")) {
			data = ((CustomAWSObject) tableObject).getAWSObjectTagsData();
		} else {
			data = EC2Common.getAWSObjectData((CustomTreeContainer) tableObject);
		}
	}

	public void generateTableHeaders(ArrayList<String> columnHeaders) {
		Iterator<String> it = columnHeaders.iterator();
		while (it.hasNext()) {
			columns.add(it.next());
		}
	}

	@Override
	public Class<?> getColumnClass(int c) {

		if (getColumnName(c).contains("Manage Rules") || getColumnName(c).contains("Add Instances") || getColumnName(c).contains("Add Security Groups") || getColumnName(c).contains("Add Zone") || getColumnName(c).contains("Encrypted")
				|| getColumnName(c).contains("IP Address Type") || getColumnName(c).contains("Instance Protection") || getColumnName(c).contains("Custom Tree Visible")) {
			return Boolean.class;
		} else if (getColumnName(c).equals("Load Balancer Port") || getColumnName(c).equals("Instance Port") || getColumnName(c).contains("Volume") || getColumnName(c).contains("Size") || getColumnName(c).contains("IOPS")) {
			return Integer.class;
		} else if (getColumnName(c).contains("Creation Time") || getColumnName(c).contains("Started")) {
			return Date.class;
		} else {
			return getValueAt(0, c).getClass();
		}
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getColumnName(int col) {
		return columns.get(col);
	}

	public String getLocalPath() {
		return localPath;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			return data.get(rowIndex).get(columnIndex);
		} catch (Exception e) {
			return "Undefined";
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		boolean editableCellstate = false;
		if (getColumnName(column).contains("Region")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("aws_access_key_id")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("aws_secret_access_key")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Account")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Attachments")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Tags")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Ingress")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Egress")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Rules")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Redirect")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Rule Type")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Load Balancer")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Source Type")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Manage Rules")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Load Balancer Protocol")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Instance Protocol")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Instance Port")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Load Balancer Port")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Add Instances")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Add Subnets")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Add Zone")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Add Security Groups")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Rules Actions")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Target Group")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Rules Conditions")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Instance ID")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Key")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Value")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Instance Name")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("ELB Name")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("AMI Name")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("SSL Certificate")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Custom Tree Visible")) {
			editableCellstate = true;
		} else if (getColumnName(column).contains("Port Range")) {
			if (isEditableTable) {
				editableCellstate = true;
			}
		} else if (getColumnName(column).contains("Range")) {
			if (isEditableTable) {
				editableCellstate = true;
			}
		}
		return editableCellstate;
	}

	public boolean isFileBrowser() {
		return isFileBrowser;
	}

	public void removeRow(int row) {
		data.remove(row);
		fireTableRowsDeleted(0, row);
	}

	/*public void removeRows() {
		this.data.clear();
	}*/
	public void removeRows(int[] indices) {
		Arrays.sort(indices);
		for (int i = indices.length - 1; i >= 0; i--) {
			this.data.remove(indices[i]);
			fireTableRowsDeleted(indices[i], indices[i]);
		}
	}

	public void setEditableFields(boolean editable) {
		if (editable) {
			isEditableTable = true;
		}
	}

	public void setFileBrowser(boolean isFileBrowser) {
		this.isFileBrowser = isFileBrowser;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		int x = 0;
		while (x < data.size()) {
			if (value instanceof Boolean && getColumnName(col).contains("Manage Rules")) {
				data.get(row).set(col, (boolean) value);
			} else if ((value instanceof Boolean && getColumnName(col).contains("Add Instances"))) {
				data.get(row).set(col, (boolean) value);
			} else if ((value instanceof Boolean && getColumnName(col).contains("Add Subnets"))) {
				data.get(row).set(col, (boolean) value);
			} else if ((value instanceof Boolean && getColumnName(col).contains("Add Security Groups"))) {
				data.get(row).set(col, (boolean) value);
			} else if ((value instanceof Boolean && getColumnName(col).contains("Add Zone"))) {
				data.get(row).set(col, (boolean) value);
			} else {
				data.get(row).set(col, value);
			}
			x++;
		}
		fireTableCellUpdated(row, col);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
	    int column = e.getColumn();
	    if (column > 0) {
	    if (getColumnClass(column).getSimpleName().contains("Boolean")) {
	    	String treeName = "";
			try {
				treeName = (String) ((CustomTableModel) e.getSource()).getValueAt(row, 0);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
	    	TableModel model = (TableModel) e.getSource();
	        Boolean checked = true;
			try {
				checked = (Boolean) model.getValueAt(row, column);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	        if (checked) {
	        	INIFilesFactory.updateINIFileItems(UtilMethodsFactory.getConsoleConfig(),"Applications", "true", treeName);
	        	dash.addTreeTabPaneTab(treeName);
	        } else {
	            INIFilesFactory.updateINIFileItems(UtilMethodsFactory.getConsoleConfig(),"Applications", "false", treeName);
	            int response = JOptionPane.showConfirmDialog(null, "Do you want to hide this Tree", "Hide Application Tree", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.NO_OPTION) {
				} else if (response == JOptionPane.YES_OPTION) {
					dash.removeTreeTabPaneTab(treeName);
				} else if (response == JOptionPane.CLOSED_OPTION) {
				}
	        }
	    }
	    }

	}
}
