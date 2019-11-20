package com.arkrud.UI;

import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class LabelLikeTextPane extends JTextPane {
	private static final long serialVersionUID = 1L;

	public LabelLikeTextPane(String text, boolean rightJustified) {
		// labelLikeTextPane.setContentType("text/html"); // let the text pane know this is what you want
		setEditable(true); // as before
		setBackground(null); // this is the same as a JLabel
		setBorder(null);
		setText(text);
		if (rightJustified) {
			SimpleAttributeSet attribs = new SimpleAttributeSet();
			StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
			StyleConstants.setBold(attribs, true);
			StyleConstants.setFontFamily(attribs, Font.SANS_SERIF);
			StyleConstants.setFontSize(attribs, 12);
			setParagraphAttributes(attribs, true);
		} else {
			setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		}
	}
}
