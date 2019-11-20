package com.arkrud.TableInterface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableCellRenderer;

import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.elasticloadbalancingv2.model.Listener;
import com.amazonaws.services.elasticloadbalancingv2.model.Rule;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.arkrud.UI.LinkLikeButton;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.S3Folder;
import com.arkrud.aws.CustomObjects.CustomAWSObject;

public class CustomTableCellRenderer extends JLabel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	private HashMap<String, ImageIcon> images = new HashMap<String, ImageIcon>();
	private boolean isGrayOut = false;

	public CustomTableCellRenderer(HashMap<String, ImageIcon> images) {
		this.images = images;
		// MUST do this for background to show up
		setOpaque(true);
	}

	public boolean isGrayOut() {
		return isGrayOut;
	}

	public void setGrayOut(boolean isGrayOut) {
		this.isGrayOut = isGrayOut;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object columnObject, boolean isSelected, boolean hasFocus, int row, int column) {
		// Render various object types with specific icons and text
		Font font = this.getFont();
		setFont(font.deriveFont(Font.BOLD));
		if (columnObject instanceof S3ObjectSummary) {
			// Render various S3 object types with icons used for this file types in OS and text describing file name with extension
			S3ObjectSummary theS3ObjectSummary = (S3ObjectSummary) columnObject;
			// Set object text as file name with extension
			String fleName = theS3ObjectSummary.getKey().split("/")[theS3ObjectSummary.getKey().split("/").length - 1];
			setText(fleName);
			// Instantiate FileSystemVies object to get icons associated for various file types
			FileSystemView fileSystemView = FileSystemView.getFileSystemView();
			// Create new instance of file object in console location sub-directory "extensions" to refer to for extension icon lookup
			File file = null;
			String fileExtensionLookupFileName = "";
			if (fleName.contains(".")) {
				// Take extension from file name
				fileExtensionLookupFileName = "ex" + "." + fleName.split("\\.")[fleName.split("\\.").length - 1];
			} else {
				// If file has no extension
				fileExtensionLookupFileName = "noext";
			}
			file = new File(UtilMethodsFactory.getConfigPath() + "extensions\\" + fileExtensionLookupFileName);
			// After file object created create the file itself so it can be parsed to extract extension specific icon
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (isGrayOut && isSelected) {
				ImageIcon icon = (ImageIcon) fileSystemView.getSystemIcon(file);
				Image normalImage = icon.getImage();
				Image grayImage = GrayFilter.createDisabledImage(normalImage);
				ImageIcon theIcon = new ImageIcon(grayImage);
				setIcon(theIcon);
			} else {
				// Set extracted icon to be rendered
				setIcon(fileSystemView.getSystemIcon(file));
			}
		} else if (columnObject instanceof Owner) {
			Owner owner = (Owner) columnObject;
			setText(owner.getDisplayName());
			setIcon(null);
		} else if (columnObject instanceof Stack) {
			// Render CF Stack column
			Stack stack = (Stack) columnObject;
			// Set Stack column text to Stack name
			setText(stack.getStackName());
		} else if (columnObject instanceof Listener) {
			Listener listener = (Listener) columnObject;
			setText(listener.getProtocol());
		} else if (columnObject instanceof Rule) {
			Rule rule = (Rule) columnObject;
			setText(rule.getRuleArn());
		} else if (columnObject instanceof AWSAccount) {
			// Render CF Stack column
			AWSAccount account = (AWSAccount) columnObject;
			// Set Stack column text to Stack name
			setText(account.getAccountAlias());
		} else if (columnObject instanceof S3Folder) {
			S3Folder s3Folder = (S3Folder) columnObject;
			// Set folder column text to folder name
			setText(s3Folder.getFolderName());
			// Render S3 folders with folder icon
			setIcon(images.get("folder"));
		} else if (columnObject instanceof LinkLikeButton) {
			setText(((LinkLikeButton) columnObject).getText());
			setForeground(Color.BLUE);
		} else if (columnObject instanceof String) {
			setHorizontalAlignment(SwingConstants.CENTER);
			setText((String) columnObject);
			setForeground(Color.BLACK);
			if (((String) columnObject).equals("ALARM") || ((String) columnObject).equals("OutOfService")) {
				setForeground(Color.RED);
			} else if (((String) columnObject).equals("OK") || ((String) columnObject).equals("InService")) {
				setForeground(Color.GREEN);
			}
			setIcon(null);
		} else if (columnObject instanceof Integer) {
			setHorizontalAlignment(SwingConstants.CENTER);
			setText(Integer.toString((Integer) columnObject));
		} else {
			if (columnObject != null) {
				setText(((CustomAWSObject) columnObject).getTreeNodeLeafText());
			}
		}
		return this;
	}
}