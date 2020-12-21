package com.arkrud.TableInterface;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FilenameUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.arkrud.Shareware.XTableColumnModel;
import com.arkrud.UI.CustomProgressBar;
import com.arkrud.UI.LinkLikeButton;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.SecurityGroups.EditSecurityRulesDialog;
import com.arkrud.Util.ProcessTask;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.S3Folder;
import com.arkrud.aws.CustomObjects.CustomAPIGateway;
import com.arkrud.aws.CustomObjects.CustomAWSObject;
import com.arkrud.aws.CustomObjects.CustomEC2Instance;
import com.tomtessier.scrollabledesktop.BaseInternalFrame;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

class CustomTableHeaderPopupListener extends MouseAdapter implements ActionListener, PropertyChangeListener {
	private RowFilter<CustomTableModel, Object> lastFilter; // NO_UCD (unused code)
	private int vColIndex;
	private JPopupMenu popup;
	private JScrollableDesktopPane jScrollableDesktopPan;
	private int mColIndex;
	private CustomTable table;
	private ArrayList<Object> uniqueValues;
	private JMenuItem workMenuItem, hideMenuItem, unHideAllMenuItem, columnMenuItem, menuItem;
	private List<RowFilter<CustomTableModel, Object>> filters = new ArrayList<RowFilter<CustomTableModel, Object>>();
	private RowFilter<CustomTableModel, Object> compoundRowFilter = null;
	private TableRowSorter<CustomTableModel> myModelSorter;
	private File file;
	private S3Folder s3Folder;
	private CustomTable theTable;
	private static TransferManager tx;
	private Upload upload;

	CustomTableHeaderPopupListener(JPopupMenu popupMenu, TableRowSorter<CustomTableModel> mySimpleModelSorter, JScrollableDesktopPane jScrollableDesktopPan) {
		popup = popupMenu;
		myModelSorter = mySimpleModelSorter;
		this.jScrollableDesktopPan = jScrollableDesktopPan;
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		JTableHeader columnHeader = (JTableHeader) mouseEvent.getSource();
		int columnPoint = columnHeader.columnAtPoint(mouseEvent.getPoint());
		table = (CustomTable) columnHeader.getTable();
		int columnCursorType = columnHeader.getCursor().getType();
		if (columnCursorType == Cursor.E_RESIZE_CURSOR) {
			mouseEvent.consume();
		} else {
			if (columnPoint == 0) { // the very first column header
				table.selectAll(); // will select all table cells
			} else {
				table.setColumnSelectionAllowed(true);
				table.setRowSelectionAllowed(false);
				table.clearSelection();
				table.setColumnSelectionInterval(columnPoint, columnPoint);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent evt) {
		maybeShowPopup(evt);
	}

	@Override
	public void mouseReleased(MouseEvent evt) {
		table = (CustomTable) ((JTableHeader) evt.getSource()).getTable();
		vColIndex = table.getCustomColumnModel().getColumnIndexAtX(evt.getX());
		mColIndex = table.convertColumnIndexToModel(vColIndex);
		uniqueValues = new ArrayList<Object>();
		int count = 0;
		while (count < table.getRowCount()) {
			uniqueValues.add(table.getValueAt(count, vColIndex));
			count++;
		}
		if (vColIndex == -1) {
			return;
		}
		// Determine if mouse was clicked between column heads
		Rectangle headerRect = table.getTableHeader().getHeaderRect(vColIndex);
		if (vColIndex == 0) {
			headerRect.width -= 3; // Hard-coded constant
		} else {
			headerRect.grow(-3, 0); // Hard-coded constant
		}
		if (!headerRect.contains(evt.getX(), evt.getY())) {
			// Mouse was clicked between column heads vColIndex is the column head closest to the click vLeftColIndex is the column head to the left of the click 
			int vLeftColIndex = vColIndex;
			if (evt.getX() < headerRect.x) {
				vLeftColIndex--;
			}
		}
		maybeShowPopup(evt);
	}

	private ArrayList<Object> GetUniqueValues(Collection<Object> values) {
		return new ArrayList<Object>(new HashSet<Object>(values));
	}

	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.removeAll();
			menuItem = new JMenuItem();
			if (uniqueValues.size() > 0) {
				switch (table.getableUsageIdentifier()) {
				case "SG Overview":
					menuItem.setText("Add Security Group");
					break;
				case "AWSAccounts":
					menuItem.setText("Add AWS Account");
					break;
				case "Ingress":
					menuItem.setText("Manage Ingress Rules");
					break;
				case "Egress":
					menuItem.setText("Manage Egress Rules");
					break;
				case "SecurityGroupsTags":
					menuItem.setText("Manage Tags");
					break;
				case "S3Folders":
					menuItem.setText("Upload");
					break;
				case "Applications":
					menuItem.setText("Add Application Tree");
					break;
				case "TargetGroupTargets":
					menuItem.setText("SHH To All Targets");
					break;
				case "ELBInstances":
					menuItem.setText("SHH To All Targets");
					break;

				default:
					System.out.println("No Table Usage Identifier specified");
				}
				menuItem.addActionListener(this);
				popup.add(menuItem);
				popup.addSeparator();
			}
			hideMenuItem = new JMenuItem("Hide Column");
			hideMenuItem.addActionListener(this);
			popup.add(hideMenuItem);
			unHideAllMenuItem = new JMenuItem("Show All Columns");
			unHideAllMenuItem.addActionListener(this);
			popup.add(unHideAllMenuItem);
			JTableHeader th = table.getTableHeader();
			XTableColumnModel tcm = (XTableColumnModel) th.getColumnModel();
			for (int x = 0, y = tcm.getColumnCount(false); x < y; x++) {
				TableColumn tc = tcm.getColumnByModelIndex(x);
				columnMenuItem = new JMenuItem((String) tc.getHeaderValue());
				if (tcm.isColumnVisible(tc)) {
					columnMenuItem.setEnabled(false);
				} else {
					columnMenuItem.setEnabled(true);
				}
				columnMenuItem.addActionListener(this);
				popup.add(columnMenuItem);
			}
			popup.addSeparator();
			int count = 0;
			JMenuItem menuItem;
			while (count < GetUniqueValues(uniqueValues).size()) {
				if (GetUniqueValues(uniqueValues).get(count) != null) {
					menuItem = new JMenuItem(GetUniqueValues(uniqueValues).get(count).toString());
					// Set name property of menu item as class.toString() of table column object to support row filtering
					if (GetUniqueValues(uniqueValues).get(count) instanceof S3ObjectSummary) {
						// Set MenuItem text to the name of the S3 object to match the table cell view of the object
						String menuText = ((S3ObjectSummary) GetUniqueValues(uniqueValues).get(count)).getKey();
						menuItem.setText(menuText.split("/")[menuText.split("/").length - 1]);
						menuItem.setName(((S3ObjectSummary) GetUniqueValues(uniqueValues).get(count)).toString());
					} else if (GetUniqueValues(uniqueValues).get(count) instanceof SecurityGroup) {
						String menuText = ((SecurityGroup) GetUniqueValues(uniqueValues).get(count)).getGroupName();
						menuItem.setText(menuText);
						menuItem.setName(menuText);
					} else if (GetUniqueValues(uniqueValues).get(count) instanceof LinkLikeButton) {
						String menuText = ((LinkLikeButton) GetUniqueValues(uniqueValues).get(count)).getText();
						menuItem.setText(menuText);
						menuItem.setName(menuText);
					} else if (GetUniqueValues(uniqueValues).get(count) instanceof AWSAccount) {
						String menuText = ((AWSAccount) GetUniqueValues(uniqueValues).get(count)).getAccountAlias();
						menuItem.setText(menuText);
						menuItem.setName(menuText);
					} else if (GetUniqueValues(uniqueValues).get(count) instanceof CustomAWSObject) {
						String menuText = ((CustomAWSObject) GetUniqueValues(uniqueValues).get(count)).getTreeNodeLeafText();
						menuItem.setText(menuText);
						menuItem.setName(menuText);
					} else {
						menuItem.setName(GetUniqueValues(uniqueValues).get(count).toString());
					}
					menuItem.addActionListener(this);
					popup.add(menuItem);
				}
				count++;
			}
			menuItem = new JMenuItem("Show All Rows");
			// Set name property of menu item support row filtering
			menuItem.setName("Show All Rows");
			menuItem.addActionListener(this);
			popup.add(menuItem);
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String popupText = ((JMenuItem) e.getSource()).getText();
		XTableColumnModel columnModel = (XTableColumnModel) table.getColumnModel();
		if (popupText.contains("Hide Column")) {
			CustomTableModel model = (CustomTableModel) table.getModel();
			int selectedColumnIndex = table.getSelectedColumn();
			if (selectedColumnIndex >= 0) {
				String columnName = table.getColumnName(selectedColumnIndex);
				TableColumn column = columnModel.getColumnByModelIndex(model.findColumn(columnName));
				boolean visible = columnModel.isColumnVisible(column);
				columnModel.setColumnVisible(column, !visible);
			}
		} else if (popupText.contains("Show All Columns")) {
			columnModel.setAllColumnsVisible();
		} else if (popupText.contains("Add Security Group")) {
			UtilMethodsFactory.showDialogToDesctop("AddSecurityGroupFrame", 300, 160, table.getParentTreeNode(), table.getTree(), table.getDash(),
					table.getAwsAccount(), null, null, null);
		} else if (popupText.contains("Create ELB")) {
			// UtilMethods.addFrameToDesctop("ELBAddPanel",table.getAwsAccount(), table.getParentTreeNode(), table.getTree(), table.getDash());
		} else if (popupText.contains("Add AWS Account")) {
			UtilMethodsFactory.showDialogToDesctop("AWSAccountFrame", 400, 200, table.getParentTreeNode(), table.getTree(), table.getDash(), null, null, null,
					null);
		} else if (popupText.contains("Add Application Tree")) {
			UtilMethodsFactory.showDialogToDesctop("AddAppicationTreesFrame", 250, 140, table.getParentTreeNode(), table.getTree(), table.getDash(), null, null,
					null, table);
		} else if (popupText.contains("SHH To All Targets")) {
			int realcolumnIndex = table.convertColumnIndexToModel(table.getSelectedColumn());
			int x = 0;
			String id = "";
			while (x < table.getRowCount()) {
				try {
					id = ((LinkLikeButton) table.getModel().getValueAt(x, realcolumnIndex)).getText();
				} catch (Exception e2) {
					JOptionPane.showConfirmDialog(null, "Please select the Targets column", "Column Selection Warning", JOptionPane.CLOSED_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				CustomEC2Instance instance = new CustomEC2Instance(table.getAwsAccount(), id, false, null);
				if (instance.getPlatform() == null) {
					ProcessTask processTask = new ProcessTask("centos", instance.getPrivateIpAddress(), instance.getKeyName());
					try {
						processTask.call();
					} catch (Exception e1) {
					}
				} else {
					if (instance.getPlatform().equals("windows")) {
						JOptionPane.showMessageDialog(table, "This instance has Windows OS. Please use RDP to connct", "SSH is not Supported for Windows",
								JOptionPane.WARNING_MESSAGE);
					} else {
						ProcessTask processTask = new ProcessTask("centos", instance.getPrivateIpAddress(), instance.getKeyName());
						try {
							processTask.call();
						} catch (Exception e1) {
						}
					}
				}
				x++;
			}
		} else if (popupText.contains("Manage Ingress Rules") || popupText.contains("Manage Egress Rules")) {
			EditSecurityRulesDialog editSecurityRulesDialod = new EditSecurityRulesDialog((SecurityGroup) table.getDataObject(),
					table.getableUsageIdentifier());
			editSecurityRulesDialod.setSize(700, 500);
			// Get the size of the screen
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			// Determine the new location of the window
			int w = editSecurityRulesDialod.getSize().width;
			int h = editSecurityRulesDialod.getSize().height;
			int x = (dim.width - w) / 2;
			int y = (dim.height - h) / 2;
			// Move the window
			editSecurityRulesDialod.setLocation(x, y);
			editSecurityRulesDialod.setVisible(true);
		} else if (popupText.contains("Manage Tags")) {
		} else if (popupText.contains("Upload")) {
			uploadToS3(table);
			table.getDash().getCustomTree("All Applications").refreshTreeNode(null, null);
		} else if (isMenuTextATableColumn(popupText)) {
			CustomTableModel model = (CustomTableModel) table.getModel();
			TableColumn column = columnModel.getColumnByModelIndex(model.findColumn(popupText));
			boolean visible = columnModel.isColumnVisible(column);
			columnModel.setColumnVisible(column, !visible);
		} else {
			workMenuItem = (JMenuItem) e.getSource();
			newFilter();
		}
	}

	private boolean isMenuTextATableColumn(String menuText) {
		boolean isColumn = false;
		JTableHeader th = table.getTableHeader();
		XTableColumnModel tcm = (XTableColumnModel) th.getColumnModel();
		for (int x = 0, y = tcm.getColumnCount(false); x < y; x++) {
			TableColumn tc = tcm.getColumnByModelIndex(x);
			if (((String) tc.getHeaderValue()).contains(menuText)) {
				isColumn = true;
				break;
			}
		}
		return isColumn;
	}

	private void newFilter() {
		RowFilter<CustomTableModel, Object> rf = null;
		try {
			String theText = "";
			if (workMenuItem.getName().equals("Show All Rows")) {
				theText = "^*";
			} else {
				theText = workMenuItem.getName();
			}
			rf = RowFilter.regexFilter(theText, mColIndex);
			filters.add(rf);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		if (workMenuItem.getName().equals("Show All Rows")) {
			myModelSorter.setRowFilter(rf);
			filters.clear();
			setLastFilter(null);
		} else {
			if (filters.size() > 1) {
				rf = RowFilter.regexFilter(workMenuItem.getName(), mColIndex);
				filters.add(rf);
				compoundRowFilter = RowFilter.andFilter(filters);
				myModelSorter.setRowFilter(compoundRowFilter);
				setLastFilter(compoundRowFilter);
			} else {
				myModelSorter.setRowFilter(rf);
				setLastFilter(rf);
			}
		}
		table.getContainigFrame().updateStatusBarCounter(table.getRowCount());
	}

	private void uploadToS3(CustomTable table) {
		theTable = table;
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showOpenDialog(theTable.getDash());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			tx = new TransferManager(AwsCommon.getAWSCredentials(theTable.getS3Folder().getFolderAccount().getAccountAlias()));
			if (file.isDirectory()) {
				s3Folder = new S3Folder(file.getName(), theTable.getS3Folder().getBucket(), theTable.getS3Folder().getFolderAccount(),
						((S3Folder) theTable.getParentTreeNode().getUserObject()).getFolderPath() + file.getName() + "/");
				DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(s3Folder);
				theTable.getParentTreeModel().insertNodeInto(topNode, theTable.getParentTreeNode(), 0);
				final CustomProgressBar progFrame = new CustomProgressBar(true, false, "Loading EC2 Objects");
				progFrame.getPb().setIndeterminate(true);
				// Save uploaded folder path into variable to use later to build corresponding S3 Folders structure
				buildFolderTreeContents(file, topNode, theTable);
				SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						MultipleFileUpload multiUpload = tx.uploadDirectory(theTable.getS3Folder().getBucket().getName(),
								((S3Folder) theTable.getParentTreeNode().getUserObject()).getFolderPath() + file.getName(), file, true);
						try {
							multiUpload.waitForCompletion();
						} catch (AmazonServiceException e) {
							e.printStackTrace();
						} catch (AmazonClientException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return null;
					};

					// This is called when the SwingWorker's doInBackground finishes
					@Override
					protected void done() {
						progFrame.getPb().setIndeterminate(false);
						// Hide progress bar JFrame
						progFrame.setVisible(false);
					};
				};
				w.addPropertyChangeListener(this);
				w.execute();
				progFrame.setVisible(true);
			} else {
				final CustomProgressBar myProgressBar = new CustomProgressBar(false, false, "Uploading...");
				PutObjectRequest request;
				if (FilenameUtils.getExtension(file.getName()).isEmpty()) {
					request = new PutObjectRequest(theTable.getS3Folder().getBucket().getName(),
							((S3Folder) theTable.getParentTreeNode().getUserObject()).getFolderPath() + FilenameUtils.getBaseName(file.getName()), file);
				} else {
					request = new PutObjectRequest(theTable.getS3Folder().getBucket().getName(),
							((S3Folder) theTable.getParentTreeNode().getUserObject()).getFolderPath() + FilenameUtils.getBaseName(file.getName()) + "."
									+ FilenameUtils.getExtension(file.getName()),
							file);
				}
				request.setGeneralProgressListener(new ProgressListener() {
					@Override
					public void progressChanged(ProgressEvent progressEvent) {
						myProgressBar.setVisible(true);
						JProgressBar pb = myProgressBar.getPb();
						pb.setValue((int) upload.getProgress().getPercentTransferred());
						switch (progressEvent.getEventType()) {
						case CLIENT_REQUEST_SUCCESS_EVENT:
							break;
						case CLIENT_REQUEST_FAILED_EVENT:
							break;
						case CLIENT_REQUEST_RETRY_EVENT:
							break;
						case CLIENT_REQUEST_STARTED_EVENT:
							break;
						case HTTP_REQUEST_COMPLETED_EVENT:
							break;
						case HTTP_REQUEST_CONTENT_RESET_EVENT:
							break;
						case HTTP_REQUEST_STARTED_EVENT:
							break;
						case HTTP_RESPONSE_COMPLETED_EVENT:
							break;
						case HTTP_RESPONSE_CONTENT_RESET_EVENT:
							break;
						case HTTP_RESPONSE_STARTED_EVENT:
							break;
						case REQUEST_BYTE_TRANSFER_EVENT:
							break;
						case REQUEST_CONTENT_LENGTH_EVENT:
							break;
						case RESPONSE_BYTE_DISCARD_EVENT:
							break;
						case RESPONSE_BYTE_TRANSFER_EVENT:
							break;
						case RESPONSE_CONTENT_LENGTH_EVENT:
							break;
						case TRANSFER_CANCELED_EVENT:
							break;
						case TRANSFER_COMPLETED_EVENT:
							pb.setValue(100);
							myProgressBar.dispose();
							String frameTitle = ((S3Folder) theTable.getParentTreeNode().getUserObject()).getFolderAccount().getAccountAlias() + " - "
									+ ((S3Folder) theTable.getParentTreeNode().getUserObject()).getBucket().getName() + " - "
									+ ((S3Folder) theTable.getParentTreeNode().getUserObject()).getFolderPath();
							BaseInternalFrame theFrame = new CustomTableViewInternalFrame(frameTitle, replacementTable(theTable, jScrollableDesktopPan));
							UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(frameTitle, jScrollableDesktopPan, theFrame);
							break;
						case TRANSFER_FAILED_EVENT:
							break;
						case TRANSFER_PART_COMPLETED_EVENT:
							break;
						case TRANSFER_PART_FAILED_EVENT:
							break;
						case TRANSFER_PART_STARTED_EVENT:
							break;
						case TRANSFER_PREPARING_EVENT:
							break;
						case TRANSFER_STARTED_EVENT:
							break;
						default:
							break;
						}
					}
				});
				upload = tx.upload(request);
			}
		} else {
		}
	}

	private CustomTable replacementTable(CustomTable theTable, JScrollableDesktopPane jScrollableDesktopPan) {
		ArrayList<String> columnHeaders = new ArrayList<String>();
		columnHeaders.add("Object");
		columnHeaders.add("Size");
		columnHeaders.add("Owner");
		CustomTable table = new CustomTable((theTable.getParentTreeNode().getUserObject()), columnHeaders, jScrollableDesktopPan, "S3Folders", false);
		table.setTransferHandler(theTable.getTransferHandler());
		// table1.setAwsAccount(((S3Folder) theTable.getParentTreeNode().getUserObject()).getFolderAccount());
		// table1.setBucket(((S3Folder) theTable.getParentTreeNode().getUserObject()).getBucket());
		table.setS3Folder(((S3Folder) theTable.getParentTreeNode().getUserObject()));
		table.setParentTreeNode(theTable.getParentTreeNode());
		table.setParentTreeModel(theTable.getParentTreeModel());
		table.setDash(theTable.getDash());
		table.setTree(theTable.getTree());
		return table;
	}

	// Build tree nodes for uploaded folder structure
	private void buildFolderTreeContents(File dir, DefaultMutableTreeNode topNode, CustomTable table) {
		S3Folder s3Folder;
		TreePath pathToExpand = null;
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					s3Folder = new S3Folder(file.getName(), table.getS3Folder().getBucket(), table.getS3Folder().getFolderAccount(),
							((S3Folder) table.getParentTreeNode().getUserObject()).getFolderPath()
									+ file.getCanonicalPath().replace("\\", "/").substring(file.getParent().length() + 1) + "/");
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(s3Folder);
					table.getParentTreeModel().insertNodeInto(node, topNode, 0);
					buildFolderTreeContents(file, node, table);
					// Get the path for last tree node with the leaf in the uploaded folders hierarchy to expand all uploaded nodes
					pathToExpand = new TreePath(topNode.getPath());
				} else {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		table.getTree().expandPath(pathToExpand);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

	public RowFilter<CustomTableModel, Object> getLastFilter() {
		return lastFilter;
	}

	public void setLastFilter(RowFilter<CustomTableModel, Object> lastFilter) {
		this.lastFilter = lastFilter;
	}
}