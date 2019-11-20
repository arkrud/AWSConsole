package com.arkrud.UI;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class CustomDocumentFilter extends DocumentFilter {
	private StyledDocument styledDocument;
	private JTextPane textPane;
	private AttributeSet greenAttributeSet, blueAttributeSet, blackAttributeSet, redAttributeSet;
	private Pattern greenPattern, bluePattern, redPattern;

	public CustomDocumentFilter(JTextPane textPane) {
		this.textPane = textPane;
		styledDocument = textPane.getStyledDocument();
		StyleContext styleContext = StyleContext.getDefaultStyleContext();
		greenAttributeSet = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.GREEN);
		blackAttributeSet = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
		blueAttributeSet = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
		redAttributeSet = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.RED);
		greenAttributeSet = styleContext.addAttribute(greenAttributeSet, StyleConstants.Bold, true);
		blueAttributeSet = styleContext.addAttribute(blueAttributeSet, StyleConstants.Bold, true);
		redAttributeSet = styleContext.addAttribute(redAttributeSet, StyleConstants.Bold, true);
		// Use a regular expression to find the words you are looking for
		greenPattern = buildPattern("\"[\\\\a-zA-Z\\d\\s:_\\.\\/\\{\\}\\(\\)\\*:,-@]+\"\\s*[:|,]");
		bluePattern = buildPattern(":\\s*\\[*\\s*\"[\\\\a-zA-Z\\d\\s:_\\.\\/\\{\\}\\]\\[\\(\\)\\*,-@]+\",*");
		redPattern = buildPattern("\"Ref\"|\"Fn::GetAtt\"|\"Fn::Join\"");
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String text, AttributeSet attributeSet) throws BadLocationException {
		super.insertString(fb, offset, text, attributeSet);
		handleTextChanged();
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		super.remove(fb, offset, length);
		handleTextChanged();
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
		super.replace(fb, offset, length, text, attributeSet);
		handleTextChanged();
	}

	/**
	 * Runs your updates later, not during the event notification.
	 */
	private void handleTextChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateTextGreenStyles();
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateTextBlueStyles();
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateTextRedStyles();
			}
		});
	}

	/**
	 * @return
	 */
	private Pattern buildPattern(String patternString) {
		Pattern p = Pattern.compile(patternString);
		return p;
	}

	private void updateTextGreenStyles() {
		// Clear existing styles
		styledDocument.setCharacterAttributes(0, textPane.getText().length(), blackAttributeSet, true);
		// Look for tokens and highlight them
		/*
		 * If you are on Windows, then the TextComponent text (searchPane.getText()) can contain carriage-return and newline characters (\r\n), but the
		 * TextComponent's Styled Document (sSearchPane.getText(0, sSearchPane.getLength())) contains only newline characters (\n).
		 */
		Matcher matcher = greenPattern.matcher(textPane.getText().replaceAll("\r\n", "\n"));
		while (matcher.find()) {
			// Change the color of recognized tokens
			styledDocument.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start() - 1, greenAttributeSet, false);
		}
	}

	private void updateTextBlueStyles() {
		Matcher matcher = bluePattern.matcher(textPane.getText().replaceAll("\r\n", "\n"));
		while (matcher.find()) {
			// Change the color of recognized tokens
			styledDocument.setCharacterAttributes(matcher.start() + 1, matcher.end() - matcher.start(), blueAttributeSet, false);
		}
	}

	private void updateTextRedStyles() {
		Matcher matcher = redPattern.matcher(textPane.getText().replaceAll("\r\n", "\n"));
		while (matcher.find()) {
			// Change the color of recognized tokens
			styledDocument.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), redAttributeSet, false);
		}
	}
}
