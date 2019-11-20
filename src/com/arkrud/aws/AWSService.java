package com.arkrud.aws;

import com.arkrud.TreeInterface.TreeNodeState;

public class AWSService implements TreeNodeState {
	private String accountAlias = "";
	private String awsServiceName = "";
	private String awsServiceDescription = "";
	private AWSAccount theAccount;
	private boolean selected;

	public AWSAccount getTheAccount() {
		return theAccount;
	}

	public void setTheAccount(AWSAccount theAccount) {
		this.theAccount = theAccount;
	}

	public String getAwsServiceName() {
		return awsServiceName;
	}

	public void setAwsServiceName(String awsServiceName) {
		this.awsServiceName = awsServiceName;
	}

	public String getAwsServiceDescription() {
		return awsServiceDescription;
	}

	public void setAwsServiceDescription(String awsServiceDescription) {
		this.awsServiceDescription = awsServiceDescription;
	}

	@Override
	public String getNodeText() {
		// TODO Auto-generated method stub
		return getAwsServiceName();
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getAWSAccountAlias() {
		return accountAlias;
	}

	@Override
	public void setAWSAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}

	@Override
	public String getNodeScreenName() {
		// TODO Auto-generated method stub
		return getAwsServiceName();
	}
}
