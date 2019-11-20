package com.arkrud.TableInterface;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancingv2.model.Listener;
import com.amazonaws.services.elasticloadbalancingv2.model.Rule;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.LinkLikeButton;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.CustomObjects.CustomEC2ELB;
import com.arkrud.aws.CustomObjects.CustomEC2ELBV2;
import com.arkrud.aws.CustomObjects.CustomEC2Instance;
import com.arkrud.aws.CustomObjects.CustomEC2SecurityGroup;
import com.arkrud.aws.CustomObjects.CustomEC2TargetGroup;
import com.tomtessier.scrollabledesktop.BaseInternalFrame;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomTableButtonColumnAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private CustomTable table;
	private JScrollableDesktopPane jScrollableDesktopPan;
	private ArrayList<String> columnHeaders;
	private String buttonColumn;
	private String frameHeaderPrefix;
	private String usageFlag;

	public CustomTableButtonColumnAction(CustomTable table, JScrollableDesktopPane jScrollableDesktopPan, ArrayList<String> columnHeaders, String buttonColumn,
			String frameHeaderPrefix, String usageFlag) {
		super();
		this.table = table;
		this.jScrollableDesktopPan = jScrollableDesktopPan;
		this.columnHeaders = columnHeaders;
		this.buttonColumn = buttonColumn;
		this.frameHeaderPrefix = frameHeaderPrefix;
		this.usageFlag = usageFlag;
		}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (usageFlag.equals("Instances")) {
			int slecetdRowModelIndex = table.convertRowIndexToModel(table.getSelectedRow());
			Instance instance = ((CustomEC2ELB) table.getDataObject()).getInstances().get(slecetdRowModelIndex);
			String instanceID = instance.getInstanceId();
			CustomEC2Instance customEC2Instance = new CustomEC2Instance(((CustomEC2ELB) table.getDataObject()).getAwsAccount(), instanceID, false, null);
			UtilMethodsFactory.showFrame(customEC2Instance, jScrollableDesktopPan);
		} else if (usageFlag.contains("Subnets")) {
		} else if (usageFlag.equals("Instance ID")) {
			int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			int realcolumnIndex = table.convertColumnIndexToModel(table.getSelectedColumn());
			LinkLikeButton linkLikeButton = (LinkLikeButton)table.getModel().getValueAt(realRowIndex, realcolumnIndex);
			CustomEC2Instance customEC2Instance = new CustomEC2Instance(((CustomEC2TargetGroup) table.getDataObject()).getAccount(), linkLikeButton.getText() );
			UtilMethodsFactory.showFrame(customEC2Instance, jScrollableDesktopPan);
		} else if (usageFlag.equals("Target Group")) {
			int realRowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			int realcolumnIndex = table.convertColumnIndexToModel(table.getSelectedColumn());
			LinkLikeButton linkLikeButton = (LinkLikeButton)table.getModel().getValueAt(realRowIndex, realcolumnIndex);
			CustomEC2TargetGroup customEC2TargetGroup = new CustomEC2TargetGroup(((CustomEC2ELBV2) table.getDataObject()).getAccount(), linkLikeButton.getText() );
			UtilMethodsFactory.showFrame(customEC2TargetGroup, jScrollableDesktopPan);
		} else if (usageFlag.equals("Tags") || usageFlag.equals("Ingress") || usageFlag.equals("Egress")) {
			// Get table selected row index
			// Convert this index to index in the table model (will be different because of sorting and filtering)
			int slecetdRowModelIndex = table.convertRowIndexToModel(table.getSelectedRow());
			// Find column indexes by name in the table model
			int idColumnIndex = ((CustomTableModel) table.getModel()).findColumn("GroupID");
			int descriptionColumnIndex = ((CustomTableModel) table.getModel()).findColumn("Description");
			// Get SecurityGroup ID from group from selected table row for farther processing
			String groupID = (String) table.getModel().getValueAt(slecetdRowModelIndex, idColumnIndex);
			// Get EC2 SecurityGroup object by group ID
			@SuppressWarnings("unchecked")
			Iterator<CustomEC2SecurityGroup> groupsIterator = (Iterator<CustomEC2SecurityGroup>) ((CustomTreeContainer) table.getDataObject()).getEc2Objects()
					.iterator();
			while (groupsIterator.hasNext()) {
				CustomEC2SecurityGroup group = groupsIterator.next();
				if (group.getGroupId().contains(groupID)) {
					if (checkForEmptyset(group, usageFlag)) {
						CustomTable tagTable = new CustomTable(group, columnHeaders, jScrollableDesktopPan, usageFlag, false);
						// Presort Tags table alphabetically based on tag names
						tagTable.setTransferHandler(table.getTransferHandler());
						tagTable.getRowSorter().toggleSortOrder(((CustomTableModel) tagTable.getModel()).findColumn(buttonColumn));
						// Get SecurityGroup Description from group from selected table to generate unique descriptive Tags table title
						String groupDescription = (String) table.getModel().getValueAt(slecetdRowModelIndex, descriptionColumnIndex);
						String groupIDString = (String) table.getModel().getValueAt(slecetdRowModelIndex, idColumnIndex);
						String frameTitle = frameHeaderPrefix + groupDescription + " - " + groupIDString;
						// Generate new internal frame for the table
						BaseInternalFrame theFrame = new CustomTableViewInternalFrame(frameTitle, tagTable);
						// Add the internal frame to Scrollable desktop pane
						UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(frameTitle, jScrollableDesktopPan, theFrame);
					} else {
						final JPanel panel = new JPanel();
						JOptionPane.showMessageDialog(panel, "There are no " + frameHeaderPrefix, "Warning", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		} else if (usageFlag.equals("ListenersRules")) {
			int slecetdRowModelIndex = table.convertRowIndexToModel(table.getSelectedRow());
			int listenerARNColumnIndex = ((CustomTableModel) table.getModel()).findColumn("ARN");
			String listenerARN = (String) table.getModel().getValueAt(slecetdRowModelIndex, listenerARNColumnIndex);
			CustomEC2ELBV2 customEC2ELBV2 = (CustomEC2ELBV2) table.getDataObject();
			Listener listener = customEC2ELBV2.getListener(((CustomEC2ELBV2) table.getDataObject()).getLoadBalancerArn(), listenerARN);
			customEC2ELBV2.setListenerOfInterest(listener);
			CustomTable tagTable = new CustomTable(customEC2ELBV2, columnHeaders, jScrollableDesktopPan, usageFlag, false);
			BaseInternalFrame theFrame = new CustomTableViewInternalFrame(listenerARN, tagTable);
			UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(listenerARN, jScrollableDesktopPan, theFrame);
		} else if (usageFlag.equals("Rules Actions")) {
			int slecetdRowModelIndex = table.convertRowIndexToModel(table.getSelectedRow());
			int listenerARNColumnIndex = ((CustomTableModel) table.getModel()).findColumn("ARN");
			Rule rule = (Rule) table.getModel().getValueAt(slecetdRowModelIndex, listenerARNColumnIndex);
			ArrayList<String> propertyNameLabels = new ArrayList<String>();
			propertyNameLabels.add("Order");
			propertyNameLabels.add("Type");
			propertyNameLabels.add("Target Group");
			propertyNameLabels.add("Redirect Config");
			propertyNameLabels.add("Authenticate Config");
			propertyNameLabels.add("Authenticate OID Config");
			propertyNameLabels.add("Fixed Response Config");
			CustomEC2ELBV2 customEC2ELBV2 = (CustomEC2ELBV2) table.getDataObject();
			customEC2ELBV2.setListenerRule(rule);
			CustomTable tagTable = new CustomTable(customEC2ELBV2, propertyNameLabels, jScrollableDesktopPan, usageFlag, false);
			BaseInternalFrame theFrame = new CustomTableViewInternalFrame(rule.getRuleArn() + " Actions", tagTable);
			UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(rule.getRuleArn() + " Actions", jScrollableDesktopPan, theFrame);
		} else if (usageFlag.equals("Rules Conditions")) {
			int slecetdRowModelIndex = table.convertRowIndexToModel(table.getSelectedRow());
			int listenerARNColumnIndex = ((CustomTableModel) table.getModel()).findColumn("ARN");
			Rule rule = (Rule) table.getModel().getValueAt(slecetdRowModelIndex, listenerARNColumnIndex);
			ArrayList<String> propertyNameLabels = new ArrayList<String>();
			propertyNameLabels.add("Field");
			propertyNameLabels.add("Values");
			CustomEC2ELBV2 customEC2ELBV2 = (CustomEC2ELBV2) table.getDataObject();
			customEC2ELBV2.setListenerRule(rule);
			CustomTable tagTable = new CustomTable(customEC2ELBV2, propertyNameLabels, jScrollableDesktopPan, usageFlag, false);
			BaseInternalFrame theFrame = new CustomTableViewInternalFrame(rule.getRuleArn() + " Conditions", tagTable);
			UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(rule.getRuleArn() + " Actions", jScrollableDesktopPan, theFrame);
		}
	}

	private boolean checkForEmptyset(Object obj, String tableLable) {
		int count = 0;
		if (tableLable.contains("Tags")) {
			count = ((SecurityGroup) obj).getTags().size();
		} else if (tableLable.contains("Ingress")) {
			count = ((SecurityGroup) obj).getIpPermissions().size();
		} else if (tableLable.contains("Egress")) {
			count = ((SecurityGroup) obj).getIpPermissionsEgress().size();
		} else if (tableLable.contains("ListenersRules")) {
			count = ((Listener) obj).getDefaultActions().size();
		} else {
			// count = 2;
		}
		if (count == 0) {
			return false;
		} else {
			return true;
		}
	}
}
