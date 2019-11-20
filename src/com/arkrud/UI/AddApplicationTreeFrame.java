package com.arkrud.UI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.arkrud.Shareware.SpringUtilities;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TableInterface.CustomTableModel;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.UtilMethodsFactory;

public class AddApplicationTreeFrame extends JDialog implements ActionListener {
	static final long serialVersionUID = 1L;
	private JButton addButton, cancelButton;
	private JPanel addAPPTreePanel;
	private JLabel appLabel, treeStateLabel;
	private final JTextField appTreeTextField;
	private JCheckBox treeStateCheckBox;
	private CustomTable table;
	private Dashboard dash;

	public AddApplicationTreeFrame(CustomTable table, Dashboard dash) {
		this.table = table;
		this.dash = dash;
		setModal(true);
		appLabel = new JLabel();
		appLabel.setText("Application:");
		treeStateLabel = new JLabel();
		treeStateLabel.setText("Visible");
		appTreeTextField = new JTextField(15);
		appTreeTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				treeStateCheckBox.requestFocusInWindow();
			}
		});
		treeStateCheckBox = new JCheckBox();
		treeStateCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addButton.requestFocusInWindow();
			}
		});
		addButton = new JButton("Add");
		cancelButton = new JButton("Cancel");
		addAPPTreePanel = new JPanel(new SpringLayout());
		addAPPTreePanel.add(appLabel);
		addAPPTreePanel.add(treeStateLabel);
		addAPPTreePanel.add(appTreeTextField);
		addAPPTreePanel.add(treeStateCheckBox);
		addAPPTreePanel.add(addButton);
		add(addAPPTreePanel, BorderLayout.CENTER);
		addButton.addActionListener(this);
		addAPPTreePanel.add(cancelButton);
		cancelButton.addActionListener(this);
		setTitle("Add Application Tree Account");
		SpringUtilities.makeCompactGrid(addAPPTreePanel, 3, 2, 10, 10, 10, 10);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		JButton theButton = (JButton) ae.getSource();
		if (theButton.getText().equals("Add")) {
			String application = appTreeTextField.getText();
			Boolean visibility = treeStateCheckBox.isSelected();
			int rowCount = ((CustomTableModel) table.getModel()).getRowCount();
			ArrayList<Object> rowData = new ArrayList<Object>();
			rowData.add(application);
			rowData.add(visibility);
			INIFilesFactory.addINIFileItemToSection(UtilMethodsFactory.getConsoleConfig(), "Applications", application, visibility);
			((CustomTableModel) table.getModel()).addRow(rowCount, rowData);
			dash.addTreeTabPaneTab(application);
			this.dispose();
		} else {
			this.dispose();
		}
	}
}
