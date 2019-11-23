package com.arkrud.UI;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.arkrud.Util.UtilMethodsFactory;

public class KeyImportAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textPane;

	public KeyImportAction(JTextField textPane) {
		this.textPane = textPane;
	}

	public KeyImportAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
		super(text, icon);
		putValue(SHORT_DESCRIPTION, desc);
		putValue(MNEMONIC_KEY, mnemonic);
	}

	public void actionPerformed(ActionEvent e) {
		File file = null;
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Import Key Pair Certificate File");
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			String certFileName = UtilMethodsFactory.getConfigPath() + "Certs/" + textPane.getText() + ".ppk";
			File certFile = new File(certFileName);
			UtilMethodsFactory.deleteFile(certFileName);
			try {
				UtilMethodsFactory.copyFile(file, certFile);
				JOptionPane.showConfirmDialog(null, "Key Pair Certificate File import successeded", "Key Pair Certificate File Import", JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e1) {
				JOptionPane.showConfirmDialog(null, "Key Pair Certificate File import failed", "Key Pair Certificate File Import", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
			}
		} else {
		}
	}
}
