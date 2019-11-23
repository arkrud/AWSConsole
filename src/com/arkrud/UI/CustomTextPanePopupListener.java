package com.arkrud.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;

import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.CustomObjects.CustomEC2Instance;

public class CustomTextPanePopupListener extends MouseAdapter implements ActionListener, PropertyChangeListener {
	private JPopupMenu popupMenu;
	private AWSAccount account;
	private String fieldName;

	public CustomTextPanePopupListener(JPopupMenu popupMenu, AWSAccount account, String fieldName) {
		super();
		this.popupMenu = popupMenu;
		this.account = account;
		this.fieldName = fieldName;
	}

	private void showPopup(MouseEvent e) {
		JTextField textPane = (JTextField) e.getSource();
		Action actions[] = textPane.getActions();
		Action copyAction = TextUtilities.findAction(actions, DefaultEditorKit.copyAction);
		textPane.selectAll();
		if (e.isPopupTrigger()) {
			popupMenu.removeAll();
			JMenuItem menuItem = new JMenuItem(copyAction);
			menuItem.setText("Copy");
			menuItem.addActionListener(this);
			popupMenu.add(menuItem);
			if (fieldName.contains("Instance ID")) {
				CustomEC2Instance instance = CustomEC2Instance.retriveOneEC2Instance(account, textPane.getText());
				Action shhAction = new SHHAction(instance, textPane);
				JMenuItem shhMenuItem = new JMenuItem(shhAction);
				shhMenuItem.setText("SHH In");
				shhMenuItem.addActionListener(this);
				popupMenu.add(shhMenuItem);
			} else if (fieldName.contains("Key Name")) {
				Action keyImportAction = new KeyImportAction(textPane);
				JMenuItem keyImportItem = new JMenuItem(keyImportAction);
				keyImportItem.setText("Import Key");
				keyImportItem.addActionListener(this);
				popupMenu.add(keyImportItem);
			} 
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		showPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		showPopup(e);
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
}
