package com.arkrud.UI.Dashboard;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.AWSAccount.ConsoleLoginAccountFrame;
import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.CustomObjects.CustomAPIGateway;
import com.tomtessier.scrollabledesktop.BaseInternalFrame;

public class DashboardMenu extends JMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JMenuItem exit, addDashboardUser, clearUser, addAppTree;
	private Dashboard dash;

	public DashboardMenu(Dashboard dash) {
		super();
		this.dash = dash;
		setText("Edit");
		exit = new JMenuItem("Exit");
		addDashboardUser = new JMenuItem();
		clearUser = new JMenuItem("Clear Security");
		if (INIFilesFactory.readINI(UtilMethodsFactory.getConsoleConfig()).hasSection("Security")) {
			addDashboardUser.setText("Update User");
		} else {
			addDashboardUser.setText("Add User");
		}
		if (INIFilesFactory.readINI(UtilMethodsFactory.getConsoleConfig()).hasSection("Security")) {
			clearUser.setEnabled(true);
		} else {
			clearUser.setEnabled(false);
		}
		exit.addActionListener(this);
		addAppTree = new JMenuItem("Manage Application Trees");
		addAppTree.addActionListener(this);
		addDashboardUser.addActionListener(this);
		clearUser.addActionListener(this);
		add(addDashboardUser);
		add(clearUser);
		add(addAppTree);
		add(exit);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String menuText = ((JMenuItem) e.getSource()).getText();
		if (menuText.contains("Exit")) {
			UtilMethodsFactory.exitApp();
		} else if (menuText.contains("Add User")) {
			showConsoleLoginAccountFrame(addDashboardUser);
		} else if (menuText.contains("Manage Application Trees")) {
			HashMap<String, String> applications = new HashMap<String, String>();
			//applications.put("vts", "true");
			INIFilesFactory.addINIFileSection(UtilMethodsFactory.getConsoleConfig(), "Applications", applications);
			ArrayList<String> columnHeaders = new ArrayList<String>();
			columnHeaders.add("Application");
			columnHeaders.add("Custom Tree Visible");
			CustomTable appsTreesCondfigTable = new CustomTable(dash, columnHeaders, dash.getJScrollableDesktopPane(), "Applications", false);
			appsTreesCondfigTable.setDash(dash);
			BaseInternalFrame theFrame = new CustomTableViewInternalFrame("Application Trees Configuration", appsTreesCondfigTable);
			UtilMethodsFactory.addInternalFrameToScrolableDesctopPane("Application Trees Configuration", dash.getJScrollableDesktopPane(), theFrame);
			} else if (menuText.contains("Update User")) {
			showConsoleLoginAccountFrame(addDashboardUser);
		} else if (menuText.contains("Clear Security")) {
			int response = JOptionPane.showConfirmDialog(null, "Do you want to disable security?", "Disable security", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.NO_OPTION) {
			} else if (response == JOptionPane.YES_OPTION) {
				INIFilesFactory.removeAccountFromINIFile(UtilMethodsFactory.getConsoleConfig(), "Security");
			} else if (response == JOptionPane.CLOSED_OPTION) {
			}
		}
	}

	private void showConsoleLoginAccountFrame(JMenuItem addUser) {
		ConsoleLoginAccountFrame consoleLoginAccountFrame = new ConsoleLoginAccountFrame(addUser);
		consoleLoginAccountFrame.setSize(350, 140);
		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		// Determine the new location of the window
		int w = consoleLoginAccountFrame.getSize().width;
		int h = consoleLoginAccountFrame.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		// Move the window
		consoleLoginAccountFrame.setLocation(x, y);
		consoleLoginAccountFrame.setVisible(true);
	}

	private void addApplicationTree(JMenuItem addUser) {
	}
}
