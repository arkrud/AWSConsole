package com.arkrud.UI;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class CustomProgressBar extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JProgressBar pb;

	public JProgressBar getPb() {
		return pb;
	}

	public void setPb(JProgressBar pb) {
		this.pb = pb;
	}

	public CustomProgressBar(boolean modalState, boolean decorationState, String title) {
		setUndecorated(decorationState);
		int x, y;
		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		// Determine the new location of the window
		int w = getSize().width;
		int h = getSize().height;
		if (decorationState) {
			setSize(180, 30);
			y = (dim.height - h) / 2 + 10;
			x = (dim.width - w) / 2 - 90;
		} else {
			setSize(250, 70);
			y = (dim.height - h) / 2;
			x = (dim.width - w) / 2;
		}
		// Move the window
		setLocation(x, y);
		setVisible(false);
		createBufferStrategy(1);
		setTitle(title);
		setModal(modalState);
		setResizable(false);
		JPanel progressBarPanel = new JPanel();
		pb = new JProgressBar(0, 100);
		pb.setStringPainted(true);
		pb.setString("Retrieving Objects");
		progressBarPanel.add(pb);
		add(progressBarPanel);
	}
}
