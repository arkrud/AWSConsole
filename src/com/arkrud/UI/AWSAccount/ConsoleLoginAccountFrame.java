package com.arkrud.UI.AWSAccount;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.jasypt.properties.PropertyValueEncryptionUtils;

import com.arkrud.Shareware.SpringUtilities;
import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.UtilMethodsFactory;

public class ConsoleLoginAccountFrame extends JDialog implements ActionListener {
	static final long serialVersionUID = 1L;
	private JButton accountManageButton, cancelButton;
	private JPanel consoleLoginAccountPanel;
	private JLabel userLabel, passwordLabel;
	private final JTextField userTextField, passwordTextField;
	private JMenuItem addUser;

	public ConsoleLoginAccountFrame(JMenuItem addUser) {
		this.addUser = addUser;
		setModal(true);
		userLabel = new JLabel();
		userLabel.setText("User:");
		userTextField = new JTextField(6);
		userTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				passwordTextField.requestFocusInWindow();
			}
		});
		passwordLabel = new JLabel();
		passwordLabel.setText("Password:");
		passwordTextField = new JPasswordField(15);
		accountManageButton = new JButton();
		if (INIFilesFactory.readINI(UtilMethodsFactory.getConsoleConfig()).hasSection("Security")) {
			accountManageButton.setText("Edit");
			userTextField.setText(INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), "Security", "user"));
			passwordTextField.setText(INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), "Security", "password"));
		} else {
			accountManageButton.setText("Add ");
		}
		cancelButton = new JButton("Cancel");
		consoleLoginAccountPanel = new JPanel(new SpringLayout());
		consoleLoginAccountPanel.add(userLabel);
		consoleLoginAccountPanel.add(userTextField);
		consoleLoginAccountPanel.add(passwordLabel);
		consoleLoginAccountPanel.add(passwordTextField);
		consoleLoginAccountPanel.add(cancelButton);
		consoleLoginAccountPanel.add(accountManageButton);
		accountManageButton.addActionListener(this);
		cancelButton.addActionListener(this);
		add(consoleLoginAccountPanel, BorderLayout.CENTER);
		setTitle("Manage AWS Account");
		SpringUtilities.makeCompactGrid(consoleLoginAccountPanel, 3, 2, 10, 10, 10, 10);
		passwordTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				accountManageButton.requestFocusInWindow();
				accountManageButton.doClick();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		JButton theButton = (JButton) ae.getSource();
		String user = userTextField.getText();
		String password = passwordTextField.getText();
		if (theButton.getText().equals("Add ")) {
			HashMap<String, String> credentials = new HashMap<String, String>();
			credentials.put("user", user);
			credentials.put("password", PropertyValueEncryptionUtils.encrypt(password, UtilMethodsFactory.getEncryptor()));
			INIFilesFactory.addINIFileSection(UtilMethodsFactory.getConsoleConfig(), "Security", credentials);
			addUser.setText("Update User");
			this.dispose();
		} else if (theButton.getText().equals("Edit")) {
			INIFilesFactory.updateINIFileItems(UtilMethodsFactory.getConsoleConfig(), "Security", user, "user");
			INIFilesFactory.updateINIFileItems(UtilMethodsFactory.getConsoleConfig(), "Security", PropertyValueEncryptionUtils.encrypt(password, UtilMethodsFactory.getEncryptor()), "password");
			this.dispose();
		} else {
			this.dispose();
		}
	}
}
