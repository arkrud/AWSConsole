package com.arkrud.UI.SecurityGroups;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import com.amazonaws.services.ec2.model.SecurityGroup;
import com.arkrud.Shareware.SpringUtilities;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TableInterface.CustomTableModel;
import com.arkrud.Util.UtilMethodsFactory;

public class EditSecurityRulesDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel editRulesPanel, buttonPanel, rulesTablePanel;
	private JButton addButton, closeButton, saveButton, deleteButton;
	private CustomTable editRuleTable;

	public EditSecurityRulesDialog(SecurityGroup securityGroup, String rulesType) {
		setTitle("Edit " + securityGroup.getGroupName() + " Security Rules");
		setModal(true);
		editRulesPanel = new JPanel(new SpringLayout());
		editRulesPanel.add(addRulesTablePanel(securityGroup, rulesType));
		editRulesPanel.add(addButtonPanel());
		SpringUtilities.makeCompactGrid(editRulesPanel, 2, 1, 1, 1, 1, 1);
		add(editRulesPanel, BorderLayout.CENTER);
	}

	private JPanel addButtonPanel() {
		buttonPanel = new JPanel();
		addButton = new JButton("Add");
		closeButton = new JButton("Close");
		saveButton = new JButton("Save");
		deleteButton = new JButton("Delete");
		addButton.addActionListener(this);
		closeButton.addActionListener(this);
		saveButton.addActionListener(this);
		deleteButton.addActionListener(this);
		buttonPanel = new JPanel();
		buttonPanel.add(addButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(closeButton);
		return buttonPanel;
	}

	private JScrollPane addRulesTablePanel(SecurityGroup securityGroup, String rulesType) {
		rulesTablePanel = new JPanel();
		ArrayList<String> headers = new ArrayList<String>(Arrays.asList(UtilMethodsFactory.securityGroupsRulesTableColumnHeaders));
		headers.add("Manage Rules");
		editRuleTable = new CustomTable(securityGroup, headers, null, "Edit" + rulesType, true);
		editRuleTable.setName(rulesType);
		rulesTablePanel.add(editRuleTable);
		JScrollPane rulesScrollPane = new JScrollPane(editRuleTable);
		return rulesScrollPane;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JButton theButton = (JButton) arg0.getSource();
		if (theButton.getText().equals("Add")) {
			int rowCount = ((CustomTableModel) editRuleTable.getModel()).getRowCount();
			ArrayList<Object> rowData = generateSecurityRuleRowData();
			((CustomTableModel) editRuleTable.getModel()).addRow(rowCount, rowData);
		} else if (theButton.getText().equals("Delete")) {
		} else if (theButton.getText().equals("Save")) {
		} else if (theButton.getText().equals("Close")) {
			this.dispose();
		}
	}

	private ArrayList<Object> generateSecurityRuleRowData() {
		ArrayList<Object> permissionsData = new ArrayList<Object>();
		permissionsData.add("Custom TCP rule");
		permissionsData.add("TCP");
		permissionsData.add("0");
		permissionsData.add("Custom");
		permissionsData.add("");
		permissionsData.add(false);
		return permissionsData;
	}
}
