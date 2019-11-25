package com.arkrud.UI.SecurityGroups;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.arkrud.Shareware.SpringUtilities;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.CustomObjects.CustomEC2SecurityGroup;
import com.arkrud.aws.StaticFactories.EC2Common;

public class AddSecurityGroupFrame extends JDialog implements ActionListener {
	private AWSAccount account;
	private DefaultMutableTreeNode node;
	private Dashboard dash;
	private JTree tree;
	private static final long serialVersionUID = 1L;
	private JPanel addSecurityGroupPanel;
	private JButton addButton, cancelButton;
	private JLabel securityGroupNameLabel, securityGroupDescriptionLabel, securityGroupVPCLabel;
	private JTextField securityGroupNameTextField, securityGroupDescriptionTextField;
	private JComboBox<String> vpcComboBox;

	public AddSecurityGroupFrame(AWSAccount account, DefaultMutableTreeNode node, JTree tree, Dashboard dash) {
		this.account = account;
		this.node = node;
		this.tree = tree;
		this.dash = dash;
		setTitle("Add Security Group");
		setModal(true);
		addSecurityGroupPanel = new JPanel(new SpringLayout());
		securityGroupNameLabel = new JLabel("Name");
		securityGroupNameTextField = new JTextField(50);
		securityGroupDescriptionLabel = new JLabel("Description");
		securityGroupDescriptionTextField = new JTextField(50);
		securityGroupVPCLabel = new JLabel("VPC");
		vpcComboBox = new JComboBox<String>(EC2Common.getVPCIDs(EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()))));
		addButton = new JButton("Add");
		addButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		addSecurityGroupPanel.add(securityGroupNameLabel);
		addSecurityGroupPanel.add(securityGroupNameTextField);
		addSecurityGroupPanel.add(securityGroupDescriptionLabel);
		addSecurityGroupPanel.add(securityGroupDescriptionTextField);
		addSecurityGroupPanel.add(securityGroupVPCLabel);
		addSecurityGroupPanel.add(vpcComboBox);
		addSecurityGroupPanel.add(addButton);
		addSecurityGroupPanel.add(cancelButton);
		SpringUtilities.makeCompactGrid(addSecurityGroupPanel, 4, 2, 10, 10, 10, 10);
		add(addSecurityGroupPanel, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JButton theButton = (JButton) arg0.getSource();
		if (theButton.getText().equals("Add")) {
			String groupID = CustomEC2SecurityGroup.createSecurityGroup(account.getAccountAlias(), (String) vpcComboBox.getSelectedItem(), securityGroupNameTextField.getText(), securityGroupDescriptionTextField.getText());
			// Add new node to the tree and show it
			CustomEC2SecurityGroup newGroup = new CustomEC2SecurityGroup(account, groupID, null);
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newGroup);
			((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, node, 0);
			((CustomTreeContainer) node.getUserObject()).addAWSObject(newGroup);
			UtilMethodsFactory.replaceContainerTable(node.getUserObject(), dash, node, tree);
			this.dispose();
		} else if (theButton.getText().equals("Cancel")) {
			this.dispose();
		}
	}
}
