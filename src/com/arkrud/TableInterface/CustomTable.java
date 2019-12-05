package com.arkrud.TableInterface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.IntStream;

import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.elasticloadbalancingv2.model.Listener;
import com.amazonaws.services.elasticloadbalancingv2.model.Rule;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.arkrud.Shareware.ButtonColumn;
import com.arkrud.Shareware.XTableColumnModel;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.HintTextField;
import com.arkrud.UI.LinkLikeButton;
import com.arkrud.UI.SortedComboBoxModel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.S3Folder;
import com.arkrud.aws.CustomObjects.CFStackTreeNodeUserObject;
import com.arkrud.aws.CustomObjects.CustomAWSObject;
import com.arkrud.aws.CustomObjects.CustomEC2AMI;
import com.arkrud.aws.CustomObjects.CustomEC2Asg;
import com.arkrud.aws.CustomObjects.CustomEC2ELB;
import com.arkrud.aws.CustomObjects.CustomEC2ELBV2;
import com.arkrud.aws.CustomObjects.CustomEC2Instance;
import com.arkrud.aws.CustomObjects.CustomEC2KeyPair;
import com.arkrud.aws.CustomObjects.CustomEC2LC;
import com.arkrud.aws.CustomObjects.CustomEC2NetworkInterface;
import com.arkrud.aws.CustomObjects.CustomEC2SecurityGroup;
import com.arkrud.aws.CustomObjects.CustomEC2SnapShot;
import com.arkrud.aws.CustomObjects.CustomEC2TargetGroup;
import com.arkrud.aws.CustomObjects.CustomEC2Volume;
import com.arkrud.aws.CustomObjects.CustomIAMInstanceProfile;
import com.arkrud.aws.CustomObjects.CustomRegionObject;
import com.arkrud.aws.CustomObjects.CustomRoute53Zone;
import com.arkrud.aws.StaticFactories.EC2Common;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomTable extends JTable {
	private static final long serialVersionUID = 1L;
	// Private variables for getters and setters
	private XTableColumnModel columnModel = new XTableColumnModel();
	private DefaultMutableTreeNode parentTreeNode;
	private DefaultTreeModel parentTreeModel;
	private JTree tree;
	private String tableUsageIdentifier;
	private Dashboard dash;
	// private Bucket bucket;
	private S3Folder s3Folder;
	private AWSAccount awsAccount;
	private CustomTableViewInternalFrame theTableViewInternalFrame;
	private Object dataObject;
	private boolean editable;
	private TableRowSorter<CustomTableModel> myModelSorter;
	private static Class<?>[] renderedClasses = { S3ObjectSummary.class, CustomEC2ELB.class, CustomEC2ELBV2.class, CustomEC2TargetGroup.class, String.class,
			Integer.class, SecurityGroup.class, Stack.class, AWSAccount.class, S3Folder.class, Owner.class, CustomEC2AMI.class, CustomEC2SnapShot.class,
			LinkLikeButton.class, CustomEC2Instance.class, CustomEC2KeyPair.class, CustomEC2Asg.class, CustomEC2LC.class, CustomEC2Volume.class,
			CustomIAMInstanceProfile.class, CFStackTreeNodeUserObject.class, CustomEC2NetworkInterface.class, Listener.class, Rule.class };

	public CustomTable() {
	}

	public CustomTable(CustomTableModel model) {
		super();
		setModel(model);
		// Enable and set up D&D and CCP
		setFillsViewportHeight(true); // To allow Drag or CCP to empty table
		setDragEnabled(true);
		setDropMode(DropMode.INSERT_ROWS);
		// UtilMethods.setMappings(this);
		// Create table columns from column model
		setColumnModel(columnModel);
		createDefaultColumnsFromModel();
		// Add pop-up menu to table cells
		JPopupMenu tablePopup = new JPopupMenu();
		addMouseListener(new CustomTablePopupListener(tablePopup, null, null));
	}

	public CustomTable(Object dataObject, ArrayList<String> columnHeaders, JScrollableDesktopPane jScrollableDesktopPan, String tableUsageIdentifier,
			boolean editable) {
		super();
		
		// Variable reassignment to use constructor parameter in class methods
		this.tableUsageIdentifier = tableUsageIdentifier;
		this.dataObject = dataObject;
		this.editable = editable;
		// Create and populate custom table models
		CustomTableModel model = new CustomTableModel(true);
		populateModelData(model);
		// Generate table column header from model
		model.generateTableHeaders(columnHeaders);
		if (dataObject instanceof Dashboard) {
			model.setDash((Dashboard)dataObject);
		}
		
		// Set table to be sortable by clicking on the header
		myModelSorter = new TableRowSorter<CustomTableModel>(model);
		setRowSorter(myModelSorter);
		// Apply model to the table
		setModel(model);
		System.out.println("Boomww");
		
		// Configure table interface options
		setPreferredScrollableViewportSize(getPreferredSize());
		setFillsViewportHeight(true);
		setRowSelectionAllowed(true);
		setSelectionBackground(Color.DARK_GRAY);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// Configure table wide formatting
		Font font = this.getFont();
		setFont(font.deriveFont(Font.BOLD));
		// Create table columns from column model
		setColumnModel(columnModel);
		createDefaultColumnsFromModel();
		// Add table header pop-up menu
		JPopupMenu popup = new JPopupMenu();
		MouseListener popupListener = new CustomTableHeaderPopupListener(popup, myModelSorter, jScrollableDesktopPan);
		getTableHeader().addMouseListener(popupListener);
		// Add pop-up menu to table cells
		JPopupMenu tablePopup = new JPopupMenu();
		addMouseListener(new CustomTablePopupListener(tablePopup, jScrollableDesktopPan, awsAccount));
		// Setup Region column to use combo-box selector
		createComboBoxSelectorColumns();
		// Add listener to allow table updates
		new CustomTableCellListener(this, new CustomTableUpdateAction(dataObject, tableUsageIdentifier));
		setDefaultEditor(Integer.class, new CustomIntegerCellEditor(tableUsageIdentifier));
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		// Set custom renderer for table Objects columns
		CustomTableCellRenderer customTableCellRenderer = new CustomTableCellRenderer(UtilMethodsFactory.populateInterfaceImages());
		for (@SuppressWarnings("rawtypes")
		Class cl : renderedClasses) {
			setDefaultRenderer(cl, customTableCellRenderer);
		}
		// Adjust columns width to show all text and headers text
		model.adjustColumnPreferredWidths(this);
		// Set up Button like columns for EC2 Security Group Tags, Ingress, and Egress rules.
		createButtonLikeColums(jScrollableDesktopPan);
	}

	private void populateModelData(CustomTableModel model) {
		System.out.println("ddd " + dataObject.getClass().getSimpleName());
		if (dataObject instanceof CustomRoute53Zone ) {
			System.out.println("Another boom");
			model.generateTableData(((CustomRoute53Zone) dataObject).getAccount(), dataObject, tableUsageIdentifier);
			awsAccount = ((CustomRoute53Zone) dataObject).getAccount();
		} else {
			if (dataObject instanceof CustomTreeContainer) {
				model.generateTableData(((CustomTreeContainer) dataObject).getAccount(), dataObject, tableUsageIdentifier);
				awsAccount = ((CustomTreeContainer) dataObject).getAccount();
			} else if (dataObject instanceof CustomAWSObject) {
				model.generateTableData(((CustomAWSObject) dataObject).getAccount(), dataObject, tableUsageIdentifier);
				awsAccount = ((CustomAWSObject) dataObject).getAccount();
			} else {
				model.generateTableData(null, dataObject, tableUsageIdentifier);
			}
		}
		
		
	}

	private void createComboBoxSelectorColumns() {
		if (dataObject != null) {
			if (dataObject.getClass().getName().contains("String")) {
				setupRegionComboBoxColumn(AwsCommon.getAWSRegions(), getColumn("Region"));
			}
		}
		// Setup Rule Type column to use combo-box selector
		if (tableUsageIdentifier.contains("Ingress") || tableUsageIdentifier.contains("Egress")) {
			setupComboBoxColumn(CustomEC2SecurityGroup.serviceType(), getColumn("Rule Type"));
			ArrayList<String> columnData = new ArrayList<String>();
			columnData.add("Anywhere");
			columnData.add("Custom IP");
			setupComboBoxColumn(columnData, getColumn("Source Type"));
			if (editable) {
				setupHintTextField(getColumn("Range"));
			}
		} else if (tableUsageIdentifier.contains("AddELBListenersSimple") || tableUsageIdentifier.contains("AddELBListenersAdvanced")
				|| tableUsageIdentifier.contains("EditELBListenersSimple") || tableUsageIdentifier.contains("EditELBListenersAdvanced")) {
			ArrayList<String> columnData = new ArrayList<String>();
			columnData.add("HTTP");
			columnData.add("HTTPS");
			columnData.add("TCP");
			columnData.add("SSL");
			setupComboBoxColumn(columnData, getColumn("Load Balancer Protocol"));
			setupComboBoxColumn(columnData, getColumn("Instance Protocol"));
		} else if (tableUsageIdentifier.equals("AddTags")) {
			ArrayList<String> columnData = new ArrayList<String>();
			Iterator<com.amazonaws.services.ec2.model.TagDescription> tagsData = EC2Common.getAccountTags((AWSAccount) dataObject).iterator();
			while (tagsData.hasNext()) {
				com.amazonaws.services.ec2.model.TagDescription tagData = tagsData.next();
				columnData.add(tagData.getKey());
			}
			Set<String> uniqueValues = new HashSet<String>(columnData);
			ArrayList<String> sortedList = new ArrayList<String>();
			sortedList.addAll(uniqueValues);
			setupComboBoxColumn(sortedList, getColumn("Key"));
		} else if (tableUsageIdentifier.equals("AWSAccounts")) {
			ArrayList<String> columnData = new ArrayList<String>();
			Iterator<CustomRegionObject> regionsIterator = AwsCommon.getAWSRegions().iterator();
			while (regionsIterator.hasNext()) {
				CustomRegionObject region = regionsIterator.next();
				columnData.add(region.getObjectName());
			}
			setupComboBoxColumn(columnData, getColumn("Region"));
		}
	}

	private void createButtonLikeColums(JScrollableDesktopPane jScrollableDesktopPan) {
		System.out.println("tableUsageIdentifier: " +  tableUsageIdentifier); 
		if (tableUsageIdentifier.equals("CustomEC2SecurityGroup") || tableUsageIdentifier.equals("ELBSecurityGroupsList")) {
			ArrayList<String> tagsTableColumnHeadersArrayList = new ArrayList<String>(Arrays.asList(UtilMethodsFactory.tagsTableColumnHeaders));
			ButtonColumn buttonTagColumn = new ButtonColumn(this,
					new CustomTableButtonColumnAction(this, jScrollableDesktopPan, tagsTableColumnHeadersArrayList, "Key", "Tags ", "Tags"), 5);
			ArrayList<String> tableColumnHeadersArrayList = new ArrayList<String>(Arrays.asList(UtilMethodsFactory.securityGroupsRulesTableColumnHeaders));
			ButtonColumn buttonIngressColumn = new ButtonColumn(this,
					new CustomTableButtonColumnAction(this, jScrollableDesktopPan, tableColumnHeadersArrayList, "Rule Type", "Igress Rules ", "Ingress"), 6);
			ButtonColumn buttonEgressColumn = new ButtonColumn(this,
					new CustomTableButtonColumnAction(this, jScrollableDesktopPan, tableColumnHeadersArrayList, "Rule Type", "Egress Rules ", "Egress"), 7);
			buttonTagColumn.setMnemonic(KeyEvent.VK_D);
			buttonIngressColumn.setMnemonic(KeyEvent.VK_D);
			buttonEgressColumn.setMnemonic(KeyEvent.VK_D);
		} else if (tableUsageIdentifier.equals("ListenersRules")) {
			ButtonColumn buttonListenerRulesColumn = new ButtonColumn(this,
					new CustomTableButtonColumnAction(this, jScrollableDesktopPan, null, "Whatever", "Redirect Configuration ", "Rules Actions"), 3);
			buttonListenerRulesColumn.setMnemonic(KeyEvent.VK_D);
			ButtonColumn buttonListenerConditionsColumn = new ButtonColumn(this,
					new CustomTableButtonColumnAction(this, jScrollableDesktopPan, null, "Whatever", "Redirect Configuration ", "Rules Conditions"), 4);
			buttonListenerConditionsColumn.setMnemonic(KeyEvent.VK_F);
		} else if (tableUsageIdentifier.equals("ELBV2Listeners")) {
			ArrayList<String> tableColumnHeadersArrayList = new ArrayList<String>(Arrays.asList(UtilMethodsFactory.elbv2ListenerRulesTableColumnHeaders));
			ButtonColumn buttonListenerRulesColumn = new ButtonColumn(this, new CustomTableButtonColumnAction(this, jScrollableDesktopPan,
					tableColumnHeadersArrayList, "Target Group", "Listeners Rules ", "ListenersRules"), 5);
			buttonListenerRulesColumn.setMnemonic(KeyEvent.VK_D);
		} else if (tableUsageIdentifier.equals("ELBInstances")) {
			ButtonColumn instanceColumn = new ButtonColumn(this, new CustomTableButtonColumnAction(this, jScrollableDesktopPan, null, null, null, "Instances"),
					0);
			instanceColumn.setMnemonic(KeyEvent.VK_D);
		} else if (tableUsageIdentifier.equals("CustomEC2Instance")) {
			ButtonColumn instanceColumn = new ButtonColumn(this, new CustomTableButtonColumnAction(this, jScrollableDesktopPan, null, null, null, "Instance ID"),
					1);
			instanceColumn.setMnemonic(KeyEvent.VK_D);
		} else if (tableUsageIdentifier.equals("TargetGroupTargets")) {
			ButtonColumn instanceColumn = new ButtonColumn(this,
					new CustomTableButtonColumnAction(this, jScrollableDesktopPan, null, null, null, "Instance ID"), 1);
			instanceColumn.setMnemonic(KeyEvent.VK_D);
		} else if (tableUsageIdentifier.equals("Rules Actions")) {
			ButtonColumn tgColumn = new ButtonColumn(this, new CustomTableButtonColumnAction(this, jScrollableDesktopPan, null, null, null, "Target Group"), 2);
			tgColumn.setMnemonic(KeyEvent.VK_D);
		}
	}

	// Change column to use combo box selector
	private void setupRegionComboBoxColumn(ArrayList<CustomRegionObject> columnData, TableColumn regionColumn) {
		JComboBox<String> comboBox = new JComboBox<String>();
		Iterator<CustomRegionObject> it = columnData.iterator();
		while (it.hasNext()) {
			comboBox.addItem(it.next().getObjectName());
		}
		regionColumn.setCellEditor(new DefaultCellEditor(comboBox));
	}

	// Change column to use combo box selector
	private void setupComboBoxColumn(ArrayList<String> columnData, TableColumn column) {
		SortedComboBoxModel<String> model = new SortedComboBoxModel<String>(columnData.toArray(new String[] {}));
		JComboBox<String> comboBox = new JComboBox<String>(model);
		if (editable) {
			comboBox.setEnabled(true);
		} else {
			comboBox.setEnabled(false);
		}
		column.setCellEditor(new DefaultCellEditor(comboBox));
	}

	private void setupHintTextField(TableColumn sourceColumn) {
		if (editable) {
			sourceColumn.setCellEditor(new DefaultCellEditor(new HintTextField("CIDR, TP, or Security Group")));
		}
	}

	// Getters and setters
	public String getableUsageIdentifier() {
		return tableUsageIdentifier;
	}

	public void setTableUsageIdentifier(String tableLable) {
		this.tableUsageIdentifier = tableLable;
	}

	public Dashboard getDash() {
		return dash;
	}

	public void setDash(Dashboard dash) {
		this.dash = dash;
	}

	public DefaultMutableTreeNode getParentTreeNode() {
		return parentTreeNode;
	}

	public void setParentTreeNode(DefaultMutableTreeNode parentTreeNode) {
		this.parentTreeNode = parentTreeNode;
	}

	public DefaultTreeModel getParentTreeModel() {
		return parentTreeModel;
	}

	public void setParentTreeModel(DefaultTreeModel parentTreeModel) {
		this.parentTreeModel = parentTreeModel;
	}

	public void setContainigFrame(CustomTableViewInternalFrame tableViewInternalFrame) {
		theTableViewInternalFrame = tableViewInternalFrame;
	}

	public CustomTableViewInternalFrame getContainigFrame() {
		return theTableViewInternalFrame;
	}

	public XTableColumnModel getCustomColumnModel() {
		XTableColumnModel customModel = (XTableColumnModel) this.getColumnModel();
		return customModel;
	}

	public AWSAccount getAwsAccount() {
		return awsAccount;
	}

	public void setAwsAccount(AWSAccount awsAccount) {
		this.awsAccount = awsAccount;
	}

	public S3Folder getS3Folder() {
		return s3Folder;
	}

	public void setS3Folder(S3Folder s3Folder) {
		this.s3Folder = s3Folder;
	}

	public JTree getTree() {
		return tree;
	}

	public void setTree(JTree tree) {
		this.tree = tree;
	}

	public Object getDataObject() {
		return dataObject;
	}

	public void setDataObject(Object dataObject) {
		this.dataObject = dataObject;
	}

	public TableRowSorter<CustomTableModel> getMyModelSorter() {
		return myModelSorter;
	}

	public void setMyModelSorter(TableRowSorter<CustomTableModel> myModelSorter) {
		this.myModelSorter = myModelSorter;
	}

	@Override
	// Change table cell appearance
	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
		Component comp = super.prepareRenderer(renderer, row, col);
		if (getSelectedRow() == row && getSelectedColumn() == col) {
			comp.setBackground(Color.red);
		} else if (IntStream.of(getSelectedRows()).anyMatch(x -> x == row)) {
			comp.setBackground(Color.GREEN);
		} else {
			comp.setBackground(Color.LIGHT_GRAY);
		}
		return comp;
	}

	@Override
	// Set tool tips for table cells
	public String getToolTipText(MouseEvent e) {
		String tip = null;
		java.awt.Point p = e.getPoint();
		if (rowAtPoint(p) >= 0) {
			int rowIndex = rowAtPoint(p);
			int colIndex = columnAtPoint(p);
			int realColumnIndex = convertColumnIndexToModel(colIndex);
			int realRowIndex = convertRowIndexToModel(rowIndex);
			if (getModel().getColumnName(realColumnIndex).contains("Object")) {
				if (getModel().getValueAt(realRowIndex, realColumnIndex).getClass().toString().contains("S3ObjectSummary")) {
					S3ObjectSummary s3ObjectSummary = (S3ObjectSummary) getModel().getValueAt(realRowIndex, realColumnIndex);
					tip = s3ObjectSummary.getKey();
				} else if (getModel().getValueAt(realRowIndex, realColumnIndex).getClass().toString().contains("S3Folder")) {
					S3Folder s3Folder = (S3Folder) getModel().getValueAt(realRowIndex, realColumnIndex);
					tip = s3Folder.getFolderPath();
				}
			} else if (getModel().getColumnName(realColumnIndex).contains("Region")) {
				tip = "Select Region";
			} else if (getModel().getColumnName(realColumnIndex).contains("Group Name")) {
				tip = "Select Group Name";
			} else {
				// tip = (String) getModel().getValueAt(realRowIndex,
				// realColumnIndex);
			}
		}
		return tip;
	}

	public String[] getColumnNames() {
		String[] columnnames;
		columnnames = new String[getModel().getColumnCount()];
		int x = 0;
		while (x < getModel().getColumnCount()) {
			columnnames[x] = getModel().getColumnName(x);
			x++;
		}
		return columnnames;
	}
}
