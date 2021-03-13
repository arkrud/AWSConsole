package com.arkrud.TreeInterface;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.model.Bucket;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AWSService;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.S3Folder;
import com.arkrud.aws.CustomObjects.CustomAWSObject;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	private HashMap<String, ImageIcon> images = new HashMap<String, ImageIcon>();

	public CustomTreeCellRenderer(HashMap<String, ImageIcon> images) {
		this.images = images;
	}

	// Change tree node icons and text
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		DefaultMutableTreeNode n = (DefaultMutableTreeNode) value;
		Object obj = n.getUserObject();
		if (obj instanceof AwsCommon) {
			setIcon(images.get("aws"));
			setText(((AwsCommon)obj).getNodeText());
		} else if (obj instanceof AWSAccount) {
			AWSAccount e = (AWSAccount) n.getUserObject();
			setIcon(images.get("account"));
			setText(e.getAccountAlias() + " (" + e.getAccountRegion() + ")");
		} else if (obj instanceof AWSService) {
			AWSService e = (AWSService) n.getUserObject();
			if (e.getAwsServiceName().contains("S3 Service")) {
				setIcon(images.get("s3"));
			} else if (e.getAwsServiceName().contains("EC2 Service")) {
				setIcon(images.get("ec2"));
			} else if (e.getAwsServiceName().contains("Route53")) {
				setIcon(images.get("route53"));
			} else if (e.getAwsServiceName().contains("Cloud Formation")) {
				setIcon(images.get("cf"));
			} else if (e.getAwsServiceName().contains("IAM")) {
				setIcon(images.get("iam"));
			} else if (e.getAwsServiceName().contains("VPC")) {
				setIcon(images.get("network"));
			} else if (e.getAwsServiceName().contains("SNS")) {
				setIcon(images.get("sns"));
			} else if (e.getAwsServiceName().contains("API")) {
				setIcon(images.get("apigateway"));
			} else if (e.getAwsServiceName().contains("Lambda")) {
				setIcon(images.get("lambda"));
			} else if (e.getAwsServiceName().contains("Systems Manager")) {
				setIcon(images.get("sm"));
			}
			setText(e.getAwsServiceName());
		} else if (obj instanceof Bucket) {
			Bucket e = (Bucket) n.getUserObject();
			setIcon(images.get("s3bucket"));
			setText(e.getName());
		} else if (obj instanceof S3Folder) {
			S3Folder e = (S3Folder) n.getUserObject();
			setIcon(images.get("folder"));
			setText(e.getFolderName());
		} else if (obj instanceof Region) {
			Region e = (Region) n.getUserObject();
			setIcon(images.get("region"));
			setText(e.getName());
		} else if (obj instanceof CustomTreeContainer) {
			CustomTreeContainer e = (CustomTreeContainer) n.getUserObject();
			setText(e.getContainerName());
			setIcon(e.getAssociatedImage());
		} else {
			setIcon(((CustomAWSObject) obj).getAssociatedImage());
			setText(((CustomAWSObject) obj).getTreeNodeLeafText());
		}
		return this;
	}

	@Override
	public Color getBackgroundNonSelectionColor() {
		return Color.WHITE;
	}

	@Override
	public Color getBackgroundSelectionColor() {
		return Color.LIGHT_GRAY;
	}

	@Override
	public Color getBackground() {
		return Color.WHITE;
	}
}
