package com.arkrud.TableInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.arkrud.UI.CustomProgressBar;
import com.arkrud.UI.LinkLikeButton;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.ProcessTask;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.S3Folder;
import com.arkrud.aws.CustomObjects.CustomAWSObject;
import com.arkrud.aws.CustomObjects.CustomEC2ELB;
import com.arkrud.aws.CustomObjects.CustomEC2Instance;
import com.arkrud.aws.StaticFactories.S3Common;
import com.tomtessier.scrollabledesktop.BaseInternalFrame;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomTablePopupListener extends MouseAdapter implements ActionListener, PropertyChangeListener {
	private JPopupMenu popupMenu;
	private JScrollableDesktopPane jScrollableDesktopPan;
	private AWSAccount awsAccount;
	private int vColIndex;
	private int mRowIndex;
	private CustomTable table;
	private CustomProgressBar myProgressBar;
	private Download download;
	private CustomAWSObject customAWSObject;

	public CustomTablePopupListener(JPopupMenu popupMenu, JScrollableDesktopPane jScrollableDesktopPan, AWSAccount awsAccount) {
		this.popupMenu = popupMenu;
		this.jScrollableDesktopPan = jScrollableDesktopPan;
		this.awsAccount = awsAccount;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		showPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		table = (CustomTable) e.getSource();
		vColIndex = table.getCustomColumnModel().getColumnIndexAtX(e.getX());
		if (table.getSelectedRow() != -1) {
			mRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
		}
		if ((e.getClickCount()) == 1) {
			if (table.getModel().getValueAt(mRowIndex, vColIndex).getClass().toString().contains("S3ObjectSummary")) {
			} else if (table.getModel().getValueAt(mRowIndex, vColIndex).getClass().toString().contains("S3Folder")) {
				S3Folder s3Folder = (S3Folder) table.getModel().getValueAt(mRowIndex, vColIndex);
				String frameTitle = s3Folder.getFolderAccount().getAccountAlias() + " - " + s3Folder.getBucket().getName() + " - " + s3Folder.getFolderPath();
				ArrayList<String> columnHeaders = new ArrayList<String>();
				columnHeaders.add("Object");
				columnHeaders.add("Size");
				columnHeaders.add("Owner");
				JScrollableDesktopPane pane = table.getDash().getJScrollableDesktopPane();
				CustomTable table1 = new CustomTable(s3Folder, columnHeaders, pane, "S3Folders", false);
				table1.setTransferHandler(table.getTransferHandler());
				DefaultMutableTreeNode node = table.getParentTreeNode();
				table1.setParentTreeNode(node);
				table1.setParentTreeModel(table.getParentTreeModel());
				table1.setDash(table.getDash());
				table1.setS3Folder(s3Folder);
				table1.setTree(table.getTree());
				BaseInternalFrame theFrame = new CustomTableViewInternalFrame(frameTitle, table1);
				UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(frameTitle, pane, theFrame);
			} else if (table.getModel().getValueAt(mRowIndex, vColIndex).getClass().toString().contains("Stack")) {
			}
			showPopup(e);
		} else {
			if (vColIndex == -1) {
				return;
			}
			showPopup(e);
		}
	}

	/**
	 * show pop up menu on table row
	 *
	 * @param e
	 */
	private void showPopup(MouseEvent e) {
		table = (CustomTable) e.getSource();
		if (e.isPopupTrigger()) {
			if (table.getSelectedRow() == table.rowAtPoint(e.getPoint())) {
				popupMenu.removeAll();
				int realColumnIndex = table.convertColumnIndexToModel(table.columnAtPoint(e.getPoint()));
				int realRowIndex = table.convertRowIndexToModel(table.rowAtPoint(e.getPoint()));
				Object cellValue = table.getModel().getValueAt(realRowIndex, realColumnIndex);
				String columnName = table.getModel().getColumnName(realColumnIndex);
				int selectedRowsCount = table.getSelectedRowCount();
				if (cellValue instanceof AWSAccount) {
					String[] menus = { "Delete AWS Account" };
					addMenuItems(menus);
				} else if (cellValue instanceof S3Folder) {
					String[] menus = { "Delete S3 Folder", "Download" };
					addMenuItems(menus);
				} else if (cellValue instanceof String) {
					if (table.getColumnName(table.columnAtPoint(e.getPoint())).contains("Instance Name")) {
						String[] menus = { "SHH to Instance" };
						addMenuItems(menus);
					} else if (table.getColumnName(table.columnAtPoint(e.getPoint())).contains("Application")) {
						String[] menus = { "Delete Tree" };
						addMenuItems(menus);
					}
				} else if (cellValue instanceof S3ObjectSummary) {
					String[] menus = { "Delete S3 Object", "Download" };
					S3ObjectSummary os = (S3ObjectSummary) (table.getValueAt(table.rowAtPoint(e.getPoint()), table.columnAtPoint(e.getPoint())));
					String str = os.getKey();
					if (!str.contentEquals("")) {
						addMenuItems(menus);
					}
				} else if (cellValue instanceof Integer) {
					if (columnName.contains("Load Balancer Port")) {
						String[] menus = { "Open Endpoint" };
						addMenuItems(menus);
					}
				} else if (cellValue instanceof LinkLikeButton) {
					customAWSObject = ((LinkLikeButton) table.getModel().getValueAt(mRowIndex, vColIndex)).getCustomAWSObject();
					customAWSObject.setAccount(awsAccount);
					if (selectedRowsCount > 1) {
						addMenuItems(customAWSObject.defineTableMultipleSelectionDropDown());
					} else {
						addMenuItems(customAWSObject.defineTableSingleSelectionDropDown());
					}
				} else {
					
				}
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private void addMenuItems(String[] menuItems) {
		int x = 0;
		while (x < menuItems.length) {
			JMenuItem menuItem = new JMenuItem(menuItems[x]);
			menuItem.addActionListener(this);
			popupMenu.add(menuItem);
			x++;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String menuText = ((JMenuItem) e.getSource()).getText();
		int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
		int realcolumnIndex = table.convertColumnIndexToModel(table.getSelectedColumn());
		Object object = table.getModel().getValueAt(realRowIndex, realcolumnIndex);
		if (object instanceof S3ObjectSummary) {
			if (menuText.contains("Delete")) {
				S3ObjectSummary s3ObjectSummary = (S3ObjectSummary) table.getModel().getValueAt(realRowIndex, 0);
				int response = JOptionPane.showConfirmDialog(null, "Do you want to delete " + s3ObjectSummary.getKey() + "?", "S3 Object Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.NO_OPTION) {
				} else if (response == JOptionPane.YES_OPTION) {
					S3Common.deletes3Object(S3Common.connectToS3(AwsCommon.getAWSCredentials(table.getS3Folder().getFolderAccount().getAccountAlias())), table.getS3Folder().getBucket().getName(), s3ObjectSummary.getKey());
					UtilMethodsFactory.reloadTableModelAfterRowRemoval(table);
				} else if (response == JOptionPane.CLOSED_OPTION) {
				}
			} else if (menuText.contains("Download")) {
				S3ObjectSummary s3ObjectSummary = (S3ObjectSummary) table.getModel().getValueAt(realRowIndex, 0);
				String s3objectKey = s3ObjectSummary.getKey();
				File fileToSave = null;
				@SuppressWarnings("deprecation")
				TransferManager tx = new TransferManager(AwsCommon.getAWSCredentials(table.getS3Folder().getFolderAccount().getAccountAlias()));
				GetObjectRequest getObjectRequest = new GetObjectRequest(table.getS3Folder().getBucket().getName(), s3ObjectSummary.getKey());
				String fileName = s3objectKey.split("/")[s3objectKey.split("/").length - 1];
				myProgressBar = new CustomProgressBar(false, false, "Downloading...");
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Download From S3");
				fileChooser.setSelectedFile(new File(fileName));
				int userSelection = fileChooser.showSaveDialog(jScrollableDesktopPan);
				getObjectRequest.setGeneralProgressListener(new ProgressListener() {
					@Override
					public void progressChanged(ProgressEvent progressEvent) {
						myProgressBar.setVisible(true);
						JProgressBar pb = myProgressBar.getPb();
						pb.setValue((int) download.getProgress().getPercentTransferred());
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
				if (userSelection == JFileChooser.APPROVE_OPTION) {
					fileToSave = fileChooser.getSelectedFile();
					download = tx.download(getObjectRequest, fileToSave);
				}
			}
		} else if (object instanceof AWSAccount) {
			if (menuText.contains("Delete")) {
				AWSAccount account = (AWSAccount) table.getModel().getValueAt(realRowIndex, 0);
				removeAWSAccount(account, table.getDash(), table.getParentTreeNode(), table.getTree());
			}
		} else if (object instanceof LinkLikeButton) {
		} else if (object instanceof Integer) {
			if (menuText.contains("Open Endpoint")) {
				CustomEC2ELB elb = (CustomEC2ELB) table.getDataObject();
				UtilMethodsFactory.openURLInBrowser("http://" + elb.getDNSName() + ":" + table.getModel().getValueAt(realRowIndex, 1));
			}
		} else if (object instanceof String) {
			if (menuText.contains("SHH to Instance")) {
				String id = ((LinkLikeButton) table.getModel().getValueAt(realRowIndex, 0)).getText();
				AWSAccount account = ((LinkLikeButton) table.getModel().getValueAt(realRowIndex, 0)).getAccount();
				CustomEC2Instance instance = new CustomEC2Instance(account, id, false, null);
				if (instance.getPlatform() == null) {
					ProcessTask processTask = new ProcessTask("centos", instance.getPrivateIpAddress(), instance.getKeyName());
					try {
						processTask.call();
					} catch (Exception e1) {
						// e1.printStackTrace();
					}
				} else {
					if (instance.getPlatform().equals("windows")) {
						JOptionPane.showMessageDialog(table, "This instance has Windows OS. Please use RDP to connct", "SSH is not Supported for Windows", JOptionPane.WARNING_MESSAGE);
					} else {
						ProcessTask processTask = new ProcessTask("centos", instance.getPrivateIpAddress(), instance.getKeyName());
						try {
							processTask.call();
						} catch (Exception e1) {
							// e1.printStackTrace();
						}
					}
				}
			} else if (menuText.contains("Delete Tree")) {
				int response = JOptionPane.showConfirmDialog(null, "Do you want to remove remove this Applicatin Tree(s)", "Application Tree removal", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.NO_OPTION) {
				} else if (response == JOptionPane.YES_OPTION) {
					int[] selection = table.getSelectedRows();
					String[] treeData = new String[selection.length];
					for (int i = 0; i < selection.length; i++) {
						selection[i] = table.convertRowIndexToModel(selection[i]);
						CustomTableModel model = (CustomTableModel) table.getModel();
						treeData[i] = (String) model.getValueAt(selection[i], 0);
						Dashboard dash = (Dashboard) (table.getDataObject());
						dash.removeTreeTabPaneTab(treeData[i]);
					}
					INIFilesFactory.removeINIFileItems(UtilMethodsFactory.getConsoleConfig(), "Applications", treeData);
					UtilMethodsFactory.reloadTableModelAfterRowRemoval(table);
				} else if (response == JOptionPane.CLOSED_OPTION) {
				}
			} else {
			}
		} else if (object instanceof S3Folder) {
			if (menuText.contains("Delete")) {
				S3Folder s3Folder = (S3Folder) table.getModel().getValueAt(realRowIndex, 0);
				int response = JOptionPane.showConfirmDialog(null, "Do you want to delete " + s3Folder.getFolderName() + "?", "S3 Folder Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.NO_OPTION) {
				} else if (response == JOptionPane.YES_OPTION) {
					S3Common.deleteFolderInBacket(S3Common.connectToS3(AwsCommon.getAWSCredentials(s3Folder.getFolderAccount().getAccountAlias())), s3Folder.getBucket().getName(), s3Folder.getFolderPath());
					UtilMethodsFactory.reloadTableModelAfterRowRemoval(table);
				} else if (response == JOptionPane.CLOSED_OPTION) {
				}
			}
		} else {
			((CustomAWSObject) object).performTableActions(customAWSObject, jScrollableDesktopPan, table, menuText);
			if (table.getableUsageIdentifier().equals("AddELBListenersSimple") || table.getableUsageIdentifier().equals("AddELBListenersAdvanced") || table.getableUsageIdentifier().equals("AddTags")
					|| table.getableUsageIdentifier().equals("EditELBTags")) {
				if (menuText.contains("Delete")) {
					UtilMethodsFactory.reloadTableModelAfterRowRemoval(table);
				}
			}
		}
	}

	// Remove AWS account
	private void removeAWSAccount(AWSAccount account, Dashboard dash, DefaultMutableTreeNode node, JTree tree) {
		// Show confirmation message
		int response = JOptionPane.showConfirmDialog(null, "Do you want to remove AWS Account " + account.getAccountAlias() + "?", "AWS Account Removal", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.NO_OPTION) {
		} else if (response == JOptionPane.YES_OPTION) {
			// Remove account information from INI configuration files
			INIFilesFactory.removeAccountFromINIFile(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"), account.getAccountAlias());
			INIFilesFactory.removeAccountFromINIFile(UtilMethodsFactory.getAWSAPIINIConfigs("config"), "profile " + account.getAccountAlias());
			INIFilesFactory.removeAccountFromINIFile(UtilMethodsFactory.getConsoleConfig(), account.getAccountAlias());
			// Clear account objects from tree view
			int x = 0;
			while (x < node.getChildCount()) {
				DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) node.getChildAt(x);
				if (((CustomAWSObject) theNode.getUserObject()).getObjectName().contains(account.getAccountRegion())) {
					int y = 0;
					while (y < theNode.getChildCount()) {
						DefaultMutableTreeNode theAccountNode = (DefaultMutableTreeNode) theNode.getChildAt(y);
						if (((AWSAccount) theAccountNode.getUserObject()).getAccountAlias().contains(account.getAccountAlias())) {
							((DefaultTreeModel) tree.getModel()).removeNodeFromParent(theAccountNode);
						}
						y++;
					}
				}
				x++;
			}
			// Remove account row from the table view
			UtilMethodsFactory.reloadTableModelAfterRowRemoval(table);
		} else if (response == JOptionPane.CLOSED_OPTION) {
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}
}
