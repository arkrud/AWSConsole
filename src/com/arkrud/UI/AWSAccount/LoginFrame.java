package com.arkrud.UI.AWSAccount;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import org.jasypt.properties.PropertyValueEncryptionUtils;

import com.arkrud.Shareware.SpringUtilities;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.UtilMethodsFactory;

/**
 * Main Class For Java based Fat client for AWS Cloud.<br>
 * Opens login screen if password protected user is added. <br>
 * Opens the dashboard with top tree element collapsed.
 */
@SuppressWarnings("serial")
public class LoginFrame extends JFrame implements ActionListener { // NO_UCD (unused code)
	private JButton submitButton, cancelButton;
	private JPanel loginDialogPanel, imagePanel, overPanel;
	private JLabel userNameLabel, passwordLabel, iconLabel;
	private JTextField userNameTextField, passwordTextField;

	/**
	 * Sole constructor of <code>LoginFrame</code> object.
	 *
	 */
	public LoginFrame() {
		// AWS image panel
		ImageIcon icon = UtilMethodsFactory.createImageIcon("images/aws-big.jpg");
		iconLabel = new JLabel();
		iconLabel.setIcon(icon);
		imagePanel = new JPanel();
		imagePanel.add(iconLabel);
		// Login credentials and controls panel
		loginDialogPanel = new JPanel(new SpringLayout());
		userNameLabel = new JLabel();
		userNameLabel.setText("Username:");
		userNameTextField = new JTextField(15);
		userNameTextField.setMaximumSize(userNameTextField.getMinimumSize());
		userNameTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				passwordTextField.requestFocusInWindow();
			}
		});
		passwordLabel = new JLabel();
		passwordLabel.setText("Password:");
		passwordTextField = new JPasswordField(15);
		passwordTextField.setMaximumSize(passwordTextField.getMinimumSize());
		loginDialogPanel.add(userNameLabel);
		loginDialogPanel.add(userNameTextField);
		loginDialogPanel.add(passwordLabel);
		loginDialogPanel.add(passwordTextField);
		submitButton = new JButton("Login");
		cancelButton = new JButton("Cancel");
		submitButton.addActionListener(this);
		cancelButton.addActionListener(this);
		loginDialogPanel.add(submitButton);
		loginDialogPanel.add(cancelButton);
		SpringUtilities.makeCompactGrid(loginDialogPanel, 3, 2, 10, 10, 10, 10);
		passwordTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				submitButton.requestFocusInWindow();
				submitButton.doClick();
			}
		});
		// Login Window panel
		overPanel = new JPanel(new SpringLayout());
		overPanel.add(loginDialogPanel);
		overPanel.add(imagePanel);
		SpringUtilities.makeCompactGrid(overPanel, 1, 2, 10, 10, 10, 10);
		// Add Login Window panel to Login Window
		setTitle("Login To AWS Console");
		add(overPanel, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		JButton theButton = (JButton) ae.getSource();
		if (theButton.getText().equals("Login")) {
			String value1 = userNameTextField.getText();
			String value2 = passwordTextField.getText();
			if (INIFilesFactory.readINI(UtilMethodsFactory.getConsoleConfig()).hasSection("Security")) {
				String decryptedPassword = PropertyValueEncryptionUtils.decrypt(INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), "Security", "password"), UtilMethodsFactory.getEncryptor());
				if (value1.contains(INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), "Security", "user")) && value2.contains(decryptedPassword)) {
					showDashboard();
					this.dispose();
				} else {
					JOptionPane.showMessageDialog(this, "Incorrect login or password", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				showDashboard();
				this.dispose();
			}
		} else {
			this.dispose();
		}
	}

	/**
	 * Show Dashboard on the screen. <br>
	 *
	 */
	private static void showDashboard() {
		Dashboard dash = null;
		try {
			dash = new Dashboard();
		} catch (Exception e) {
			e.printStackTrace();
		}
		dash.setVisible(true);
	}

	/**
	 * Main method to launch application.
	 *
	 */
	public static void main(String[] args) throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			if (INIFilesFactory.hasINIFileSection(UtilMethodsFactory.getConsoleConfig(), "Security")) {
				LoginFrame frame = new LoginFrame();
				frame.setSize(400, 150);
				frame.setResizable(false);
				// Get the size of the screen
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				// Determine the new location of the window
				int w = frame.getSize().width;
				int h = frame.getSize().height;
				int x = (dim.width - w) / 2;
				int y = (dim.height - h) / 2;
				// Move the window
				frame.setLocation(x, y);
				frame.setVisible(true);
			} else {
				showDashboard();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
