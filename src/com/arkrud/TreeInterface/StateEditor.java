package com.arkrud.TreeInterface;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.CustomObjects.CustomRegionObject;

public class StateEditor extends AbstractCellEditor implements TreeCellEditor, ItemListener {
	private static final long serialVersionUID = 1L;
	private JCheckBox checkBox;
	private TreeNodeState editorValue;
	private DefaultMutableTreeNode node;
	private JTree tree;

	public StateEditor() {
		checkBox = new JCheckBox();
		checkBox.addItemListener(this);
		checkBox.setOpaque(false);
	}

	@Override
	public Object getCellEditorValue() {
		editorValue.setSelected(checkBox.isSelected());
		INIFilesFactory.editINIFileItemInSection(UtilMethodsFactory.getConsoleConfig(), editorValue.getAWSAccountAlias(), editorValue.getNodeScreenName(), editorValue.isSelected());
		return editorValue;
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		if (value instanceof DefaultMutableTreeNode) {
			node = (DefaultMutableTreeNode) value;
			TreeNodeState state = (TreeNodeState) node.getUserObject();
			editorValue = state;
			checkBox.setText(state.getNodeText());
			checkBox.setSelected(state.isSelected());
		} else {
			checkBox.setText("??");
			checkBox.setSelected(false);
		}
		return checkBox;
	}

	@Override
	public boolean isCellEditable(EventObject event) {
		if (!super.isCellEditable(event)) {
			return false;
		}
		if (event != null && event.getSource() instanceof JTree && event instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) event;
			 tree = (JTree) event.getSource();
			TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (node.getUserObject() instanceof AwsCommon) {
				if (((AwsCommon) node.getUserObject()).getNodeText().equals("Filters") || ((AwsCommon) node.getUserObject()).getNodeText().equals("Select To Finish Configuration")) {
					return false;
				} else {
					return true;
				}
			} else if (node.getUserObject() instanceof CustomRegionObject) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (((TreeNodeState)node.getUserObject()).isSelected()) {
			Enumeration<?> children = node.preorderEnumeration();
			while (children.hasMoreElements()) {
				DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) children.nextElement();
				TreeNodeState state = (TreeNodeState)theNode.getUserObject();
				state.setSelected(false);
			}
		} else {
			Enumeration<?> children = node.preorderEnumeration();
			while (children.hasMoreElements()) {
				DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) children.nextElement();
				TreeNodeState state = (TreeNodeState)theNode.getUserObject();
				state.setSelected(true);
			}
		}
		
		tree.collapsePath(new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(node)));
		tree.expandPath(new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(node)));
		
		
	}
}