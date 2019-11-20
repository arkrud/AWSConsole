package com.arkrud.TreeInterface;

public interface TreeNodeState {
	String getNodeText();

	String getNodeScreenName();

	boolean isSelected();

	void setSelected(boolean selected);

	String getAWSAccountAlias();

	void setAWSAccountAlias(String accountAlias);
}