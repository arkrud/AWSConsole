package com.arkrud.UI.AWSAccount;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.amazonaws.regions.Region;
import com.arkrud.Shareware.SpringUtilities;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.CustomObjects.CustomRegionObject;

public class AWSAccountFrame extends JDialog implements ActionListener {
	static final long serialVersionUID = 1L;
	private JButton addButton, cancelButton;
	private JPanel addAccountPanel;
	private JLabel accountLabel, keyLabel, secretLabel, regionLabel;
	private final JTextField accountTextField, keyTextField, secretTextField;
	private String[] awsRegions = { "us-west-2", "us-east-1", "us-west-1", "eu-west-1", "eu-central-1" };
	private final JComboBox<String> regionComboBox = new JComboBox<String>(awsRegions);
	private Dashboard dash;
	private DefaultMutableTreeNode node;
	private JTree tree;

	public AWSAccountFrame(Dashboard dash, DefaultMutableTreeNode node, JTree tree) {
		this.dash = dash;
		this.node = node;
		this.tree = tree;
		setModal(true);
		accountLabel = new JLabel();
		accountLabel.setText("Account:");
		accountTextField = new JTextField(15);
		accountTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				keyTextField.requestFocusInWindow();
			}
		});
		;
		keyLabel = new JLabel();
		keyLabel.setText("Key:");
		keyTextField = new JTextField(15);
		keyTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				secretTextField.requestFocusInWindow();
			}
		});
		secretLabel = new JLabel();
		secretLabel.setText("Secret:");
		secretTextField = new JPasswordField(15);
		regionLabel = new JLabel();
		regionLabel.setText("Region:");
		regionComboBox.setSelectedIndex(0);
		addButton = new JButton("Add");
		cancelButton = new JButton("Cancel");
		addAccountPanel = new JPanel(new SpringLayout());
		addAccountPanel.add(accountLabel);
		addAccountPanel.add(accountTextField);
		addAccountPanel.add(keyLabel);
		addAccountPanel.add(keyTextField);
		addAccountPanel.add(secretLabel);
		addAccountPanel.add(secretTextField);
		addAccountPanel.add(regionLabel);
		addAccountPanel.add(regionComboBox);
		addAccountPanel.add(addButton);
		add(addAccountPanel, BorderLayout.CENTER);
		addButton.addActionListener(this);
		addAccountPanel.add(cancelButton);
		cancelButton.addActionListener(this);
		setTitle("Add AWS Account");
		SpringUtilities.makeCompactGrid(addAccountPanel, 5, 2, 10, 10, 10, 10);
		secretTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addButton.requestFocusInWindow();
				addButton.doClick();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		JButton theButton = (JButton) ae.getSource();
		if (theButton.getText().equals("Add")) {
			String account = accountTextField.getText();
			String key = keyTextField.getText();
			String secret = secretTextField.getText();
			String region = (String) regionComboBox.getSelectedItem();
			HashMap<String, String> credentials = new HashMap<String, String>();
			credentials.put("aws_access_key_id", key);
			credentials.put("aws_secret_access_key", secret);
			INIFilesFactory.addINIFileSection(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"), account, credentials);
			INIFilesFactory.addINIFileEmptySection(UtilMethodsFactory.getConsoleConfig(), account);
			INIFilesFactory.addINIFileItemToSection(UtilMethodsFactory.getConsoleConfig(), account, account, true);
			HashMap<String, String> awsSecurity = new HashMap<String, String>();
			awsSecurity.put("region", region);
			INIFilesFactory.addINIFileSection(UtilMethodsFactory.getAWSAPIINIConfigs("config"), "profile " + account, awsSecurity);
			Iterator<AWSAccount> it = INIFilesFactory.readAWSINIAccounts(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"), UtilMethodsFactory.getConsoleConfig(), UtilMethodsFactory.getAWSAPIINIConfigs("config"), false).iterator();
			while (it.hasNext()) {
				AWSAccount newAccount = it.next();
				if (newAccount.getAccountAlias().contains(account)) {
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newAccount);
					int i = 0;
					while (i < node.getChildCount()) {
						DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) node.getChildAt(i);
						Region reg = ((CustomRegionObject) defaultMutableTreeNode.getUserObject()).getRegion();
						if (reg.getName().contains(region)) {
							((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, defaultMutableTreeNode, 0);
						}
						i++;
					}
				}
				Iterator<DefaultMutableTreeNode> regionsIterator = dash.getCustomTree("config").getRegionNodes().iterator();
				while (regionsIterator.hasNext()) {
					DefaultMutableTreeNode defaultMutableTreeNode = regionsIterator.next();
					Region reg = ((CustomRegionObject) defaultMutableTreeNode.getUserObject()).getRegion();
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newAccount);
					if (reg.getName().contains(region)) {
						((DefaultTreeModel) dash.getCustomTree("config").getServicesTree().getModel()).insertNodeInto(newNode, defaultMutableTreeNode, 0);
					}
				}
				UtilMethodsFactory.replaceContainerTable(node.getUserObject(), dash, node, tree);
			}
			this.dispose();
		} else {
			this.dispose();
		}
	}
}
