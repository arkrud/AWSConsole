package com.arkrud.TreeInterface;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.CustomObjects.CustomRegionObject;

public class StateRenderer implements TreeCellRenderer {
	private JCheckBox checkBox;
	private Icon origIcon;

	public StateRenderer() {
		checkBox = new JCheckBox();
		checkBox.setOpaque(false);
		origIcon = checkBox.getIcon();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			TreeNodeState state = (TreeNodeState) node.getUserObject();
			checkBox.setText(state.getNodeText());
			checkBox.setSelected(state.isSelected());
			if (node.getUserObject() instanceof AwsCommon) {
				if (((AwsCommon) node.getUserObject()).getNodeText().equals("Filters")) {
					checkBox.setIcon(UtilMethodsFactory.populateInterfaceImages().get("filter"));
				} else if (((AwsCommon) node.getUserObject()).getNodeText().equals("Select To Finish Configuration")) {
					checkBox.setIcon(UtilMethodsFactory.populateInterfaceImages().get("configuration"));
				}
			} else if (node.getUserObject() instanceof CustomRegionObject) {
				checkBox.setIcon(UtilMethodsFactory.populateInterfaceImages().get("regions"));
			} else {
				checkBox.setIcon(origIcon);
			}
		} else {
			checkBox.setText("??");
			checkBox.setSelected(false);
		}
		if (selected) {
			checkBox.setBackground(UIManager.getColor("Tree.selectionBackground"));
			checkBox.setForeground(UIManager.getColor("Tree.selectionForeground"));
		} else {
			checkBox.setForeground(tree.getForeground());
		}
		checkBox.setOpaque(selected);
		return checkBox;
	}
}
