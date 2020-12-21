package com.arkrud.UI.Dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.arkrud.TreeInterface.CustomTree;
import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.UtilMethodsFactory;
import com.tomtessier.scrollabledesktop.BaseInternalFrame;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class Dashboard extends JFrame implements InternalFrameListener, WindowListener {
	private static final long serialVersionUID = 1L;
	public static Hashtable<String, BaseInternalFrame> INTERNAL_FRAMES = new Hashtable<String, BaseInternalFrame>();
	private JMenuBar jJMenuBar = null;
	private JScrollableDesktopPane jScrollableDesktopPane = null;
	private CustomTree customTree;
	private CustomTree configTree;
	private JTabbedPane treeTabbedPane;

	//// Constructor
	public Dashboard() throws Exception {
		super();
		super.addWindowListener(this);
		initialize();
	}

	// Initialization of the visual interface
	void initialize() throws Exception {
		// Create dashboard menu items and add menu to dashboard
		jJMenuBar = new JMenuBar();
		jJMenuBar.add(new DashboardMenu(this));
		this.setJMenuBar(jJMenuBar);
		// Create dashboard interface
		JPanel jContentPane = new JPanel();
		jContentPane.setLayout(new BorderLayout());
		JSplitPane jSplitPane = new JSplitPane();
		jSplitPane.setDividerLocation(350);
		jSplitPane.setRightComponent(getJScrollableDesktopPane());
		JScrollPane jScrollPane = new JScrollPane();
		treeTabbedPane = new JTabbedPane();
		configTree = getCustomTree("config");
		configTree.expandAllNodes();
		treeTabbedPane.addTab("Configuration", null, configTree, null);
		ArrayList<String> trees = new ArrayList<String>();
		INIFilesFactory.getAppTreesConfigInfo(UtilMethodsFactory.getConsoleConfig());
		Iterator<ArrayList<Object>> it = INIFilesFactory.getAppTreesConfigInfo(UtilMethodsFactory.getConsoleConfig()).iterator();
		while (it.hasNext()) {
			ArrayList<Object> appData = it.next();
			if (((Boolean) appData.get(1))) {
				trees.add((String) appData.get(0));
			}
		}
		int x = 0;
		while (x < trees.size()) {
			customTree = getCustomTree(trees.get(x));
			// After "All Applications" and specific applications trees are created the Region and Accounts nodes are expanded
			customTree.expandTwoDeep();
			treeTabbedPane.addTab(trees.get(x), null, customTree, null);
			x++;
		}
		jScrollPane.setViewportView(treeTabbedPane);
		jSplitPane.setLeftComponent(jScrollPane);
		jContentPane.add(jSplitPane, BorderLayout.CENTER);
		this.setContentPane(jContentPane);
		this.setTitle("Dashboard");
		this.setBounds(new Rectangle(0, 0, 1500, 850));
	}

	public void addTreeTabPaneTab(String appName) {
		CustomTree tree = getCustomTree(appName);
		tree.expandTwoDeep();
		treeTabbedPane.addTab(appName, null, configTree, null);
	}

	public void removeTreeTabPaneTab(String appName) {
		int count = treeTabbedPane.getTabCount();
		for (int i = 0; i < count; i++) {
			try {
				String label = treeTabbedPane.getTitleAt(i);
				if (label.equals(appName)) {
					treeTabbedPane.remove(i);
				}
			} catch (Exception e) {
			}
		}
	}

	// Get reference to JScrollableDesktopPane object
	public JScrollableDesktopPane getJScrollableDesktopPane() {
		if (jScrollableDesktopPane == null) {
			jScrollableDesktopPane = new JScrollableDesktopPane(jJMenuBar);			
		}
		return jScrollableDesktopPane;
	}

	// Get reference to CustomTree navigation object to manipulate tree nodes from other interface objects
	public CustomTree getCustomTree(String type) {
		if (type.equals("config")) {
			if (customTree == null) {
				customTree = new CustomTree(this, type);
			}
			return customTree;
		} else {
			configTree = new CustomTree(this, type);
			return configTree;
		}
	}

	// Overwrites
	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
	}

	// Remove internal frame info from static data structure when window is closed
	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		BaseInternalFrame thisFrame = (BaseInternalFrame) e.getSource();
		Dashboard.INTERNAL_FRAMES.remove(thisFrame.getTitle());
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	// Exit application
	@Override
	public void windowClosing(WindowEvent arg0) {
		UtilMethodsFactory.exitApp();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}