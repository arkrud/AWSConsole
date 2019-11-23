package com.arkrud.UI;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.arkrud.Util.ProcessTask;
import com.arkrud.aws.CustomObjects.CustomEC2Instance;

public class SHHAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CustomEC2Instance instance;
	private JTextField textPane;

	public SHHAction(CustomEC2Instance instance, JTextField textPane) {
		this.instance = instance;
		this.textPane = textPane;
	}

	public SHHAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
		super(text, icon);
		putValue(SHORT_DESCRIPTION, desc);
		putValue(MNEMONIC_KEY, mnemonic);
	}

	public void actionPerformed(ActionEvent e) {
		if (instance.getPlatform() == null) {
			ProcessTask processTask = new ProcessTask("centos", instance.getPrivateIpAddress(), instance.getKeyName());
			try {
				processTask.call();
			} catch (Exception e1) {
			}
		} else {
			if (instance.getPlatform().equals("windows")) {
				JOptionPane.showMessageDialog(textPane, "This instance has Windows OS. Please use RDP to connect instead", "SSH is not Supported for Windows", JOptionPane.WARNING_MESSAGE);
			} else {
				ProcessTask processTask = new ProcessTask("centos", instance.getPrivateIpAddress(), instance.getKeyName());
				try {
					processTask.call();
				} catch (Exception e1) {
				}
			}
		}
	}
}
