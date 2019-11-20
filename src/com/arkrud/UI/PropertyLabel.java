package com.arkrud.UI;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

public class PropertyLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	public PropertyLabel(String text) {
		super(text);
	}

	@Override
	public void setText(String text) {
		if (text != null) {
			if (text.length() == 0) {
				super.setText("-");
			} else {
				super.setText(text);
			}
		} else {
			super.setText("-");
		}
	}

	@Override
	public void setFont(Font font) {
		// TODO Auto-generated method stub
		super.setFont(new Font("Serif", Font.BOLD, 12));
	}

	@Override
	public void setForeground(Color fg) {
		if (this.getText().contains("ALARM")) {
			super.setForeground(Color.RED);
		} else if (this.getText().contains("OK")) {
			super.setForeground(Color.BLUE);
		} else {
		}
	}
}
