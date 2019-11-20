package com.arkrud.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;

public class CustomTextPanePopupListener extends MouseAdapter implements ActionListener, PropertyChangeListener {
	private JPopupMenu popupMenu;

	public CustomTextPanePopupListener(JPopupMenu popupMenu) {
		super();
		this.popupMenu = popupMenu;
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
