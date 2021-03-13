package com.arkrud.TreeInterface;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.amazonaws.services.s3.model.Bucket;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.CustomProgressBar;
import com.arkrud.UI.InterfaceFilter;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AWSService;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.S3Folder;
import com.arkrud.aws.CustomObjects.CustomAWSObject;
import com.arkrud.aws.CustomObjects.CustomRoute53Zone;
import com.arkrud.aws.StaticFactories.EC2Common;
import com.tomtessier.scrollabledesktop.BaseInternalFrame;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomTreeMouseListener implements MouseListener, PropertyChangeListener { // NO_UCD (use default)
	private JPopupMenu popup;
	private Dashboard dash;
	private JTree tree;
	private Point loc;
	private HashMap<String, Boolean> dropDownMenus = new HashMap<String, Boolean>();

	public CustomTreeMouseListener(JPopupMenu popup, JTree tree, Dashboard dash) { // NO_UCD (use default)
		this.popup = popup;
		this.tree = tree;
		this.dash = dash;
	}

	private void checkForPopup(MouseEvent e) {
		// Set all tree nodes drop-down menus not visible for all nodes
		for (int i = 0; i < UtilMethodsFactory.dropDownsNames.length; i++) {
			dropDownMenus.put(UtilMethodsFactory.dropDownsNames[i], false);
		}
		// Set drop-down menu visible on specific tree nodes right click
		if (e.isPopupTrigger()) {
			loc = e.getPoint();
			TreePath path = ((JTree) e.getSource()).getPathForLocation(loc.x, loc.y);
			Object treeObject = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			if (path != null) {
				if (treeObject instanceof AwsCommon) {
					if (((AwsCommon) treeObject).getNodeText().equals("All Applications")) {
						dropDownMenus.put("Manage AWS Accounts", true);
						dropDownMenus.put("Add AWS Account", true);
						dropDownMenus.put("Refresh", true);
					} else if (((AwsCommon) treeObject).getNodeText().equals("Filters")) {
						dropDownMenus.put("Add Filter", true);
					} else {
						dropDownMenus.put("Refresh", true);
					}
				} else if (treeObject instanceof EC2Common) {
					dropDownMenus.put("Refresh", true);
				} else if (treeObject instanceof AWSAccount) {
					dropDownMenus.put("Refresh", true);
				} else if (treeObject instanceof AWSService) {
					if (((AWSService) treeObject).getAwsServiceName().contains("EC2 Service")) {
						dropDownMenus.put("Refresh", true);
					} else if (((AWSService) treeObject).getAwsServiceName().contains("S3 Service")) {
						dropDownMenus.put("Add Bucket", true);
						dropDownMenus.put("Refresh", true);
					} else if (((AWSService) treeObject).getAwsServiceName().contains("Cloud Formation")) {
						dropDownMenus.put("Create CF Stack", true);
					}
				} else if (treeObject instanceof Bucket) {
					dropDownMenus.put("Delete Bucket", true);
					dropDownMenus.put("Add Folder", true);
				} else if (treeObject instanceof S3Folder) {
					dropDownMenus.put("Delete Folder", true);
					dropDownMenus.put("Add Folder", true);
					dropDownMenus.put("Upload Data", true);
				} else if (treeObject instanceof InterfaceFilter) {
					dropDownMenus.put("Delete Filter", true);
				} else if (treeObject instanceof CustomTreeContainer) {
					String treeObjectIdentifier = ((CustomTreeContainer) treeObject).getChildObject().getSimpleName();
					switch (treeObjectIdentifier) {
					case "CustomEC2SecurityGroup":
						dropDownMenus.put("Add Security Group", true);
						dropDownMenus.put("Refresh", true);
						break;
					default:
						dropDownMenus.put("Refresh", true);
						break;
					}
				} else {
					if (((CustomAWSObject) treeObject).defineNodeTreeDropDown() != null) {
						for (String menu : ((CustomAWSObject) treeObject).defineNodeTreeDropDown()) {
							dropDownMenus.put(menu, true);
						}
					}
				}
				// Set menu attributes
				int i = 0;
				while (i < popup.getComponentCount()) {
					JMenuItem item = (JMenuItem) popup.getComponents()[i];
					for (Map.Entry<String, Boolean> entry : dropDownMenus.entrySet()) {
						if (item.getText().contains(entry.getKey())) {
							item.setEnabled(entry.getValue());
							item.setVisible(entry.getValue());
						}
					}
					i++;
				}
				// Show pop-up
				popup.show(tree, loc.x, loc.y);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JScrollableDesktopPane pane = dash.getJScrollableDesktopPane();
		TreePath path = tree.getPathForLocation(e.getX(), e.getY());
		if (e.getClickCount() == 2 && !e.isConsumed()) {
			// handle double click event.
			Object obj = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			if (path != null) {
				if (obj instanceof S3Folder) {
					S3Folder s3Folder = (S3Folder) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					String frameTitle = s3Folder.getFolderAccount().getAccountAlias() + " - " + s3Folder.getBucket().getName() + " - " + s3Folder.getFolderPath();
					ArrayList<String> columnHeaders = new ArrayList<String>();
					columnHeaders.add("Object");
					columnHeaders.add("Size");
					columnHeaders.add("Owner");
					CustomTable table = new CustomTable(s3Folder, columnHeaders, pane, "S3Folders", false);
					table.setParentTreeNode((DefaultMutableTreeNode) path.getLastPathComponent());
					table.setParentTreeModel((DefaultTreeModel) tree.getModel());
					table.setDash(dash);
					table.setS3Folder(s3Folder);
					table.setTree(tree);
					BaseInternalFrame theFrame = new CustomTableViewInternalFrame(frameTitle, table);
					UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(frameTitle, pane, theFrame);
				} else if (obj instanceof CustomTreeContainer) {
					String objectIdentifier = ((CustomTreeContainer) obj).getChildObject().getSimpleName();
					String[] headers = ((CustomTreeContainer) obj).getObjectTableColumnHeaders();
					showTable(obj, pane, path, objectIdentifier, headers);
				} else if (obj instanceof CustomRoute53Zone) {
					String objectIdentifier = obj.getClass().getSimpleName() + "Records";
					String[] recordSetColumnHeaders = { "Record Set Name", "Type", "Value", "TTL" };
					((CustomRoute53Zone) obj).setCustomRoute53ZoneTreeNode((DefaultMutableTreeNode) path.getLastPathComponent());
					showTable(obj, pane, path, objectIdentifier, recordSetColumnHeaders);
				} else {
				}
			}
		}
		checkForPopup(e);
	}

	private void showTable(Object obj, JScrollableDesktopPane pane, TreePath path, String tableIdentifier, String[] columnHeaders) {
		String frameTitle = "";
		if (obj instanceof CustomTreeContainer) {
			frameTitle = ((CustomTreeContainer) obj).getContainerName() + " For " + ((CustomTreeContainer) obj).getAccount().getAccountAlias();
		} else if (obj instanceof CustomRoute53Zone) {
			frameTitle = ((CustomRoute53Zone) obj).getName() + " For " + ((CustomRoute53Zone) obj).getAccount().getAccountAlias();
		}
		ArrayList<String> tableColumnHeaders = new ArrayList<String>(Arrays.asList(columnHeaders));
		CustomTable table = new CustomTable(obj, tableColumnHeaders, pane, tableIdentifier, false);
		table.setParentTreeNode((DefaultMutableTreeNode) path.getLastPathComponent());
		table.setTree(tree);
		table.setDash(dash);
		if (obj instanceof CustomTreeContainer) {
			table.setAwsAccount(((CustomTreeContainer) obj).getAccount());
		} else if (obj instanceof CustomRoute53Zone) {
			table.setAwsAccount(((CustomRoute53Zone) obj).getAccount());
		}
		BaseInternalFrame theFrame = new CustomTableViewInternalFrame(frameTitle, table);
		UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(frameTitle, pane, theFrame);
	}

	public Point getLoc() {
		return loc;
	}

	public void setLoc(Point loc) {
		this.loc = loc;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		checkForPopup(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		checkForPopup(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		checkForPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		checkForPopup(e);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}
}
