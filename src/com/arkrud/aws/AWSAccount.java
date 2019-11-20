package com.arkrud.aws;

import java.util.ArrayList;
import java.util.Iterator;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.arkrud.TreeInterface.TreeNodeState;
import com.arkrud.UI.InterfaceFilter;

public class AWSAccount implements TreeNodeState {
	private String accountAlias = "";
	private String accountKey = "";
	private String accountSecret = "";
	private String accountRegion = "";
	private ArrayList<AWSService> awsServices = new ArrayList<AWSService>();
	private ArrayList<InterfaceFilter> filters = new ArrayList<InterfaceFilter>();
	private boolean selected;
	private Region region;

	// Getters & setters
	public String getAccountRegion() {
		return accountRegion;
	}

	public void setAccountRegion(String accountRegion) {
		this.accountRegion = accountRegion;
	}

	public String getAccountAlias() {
		return accountAlias;
	}

	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}

	public String getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}

	public String getAccountSecret() {
		return accountSecret;
	}

	public void setAccountSecret(String accountSecret) {
		this.accountSecret = accountSecret;
	}

	public ArrayList<InterfaceFilter> getFilters() {
		return filters;
	}

	public void addFilter(InterfaceFilter filterString) {
		filters.add(filterString);
	}

	public void removeFilter(InterfaceFilter filterString) {
		filters.remove(filterString);
	}

	// Work with AWS services for AWS Account
	// get list of all AWS Services associated with account
	public ArrayList<AWSService> getAwsServices() {
		return awsServices;
	}

	// Associate AWS service with AWS account
	public void addAWSService(AWSService theAWSService) {
		awsServices.add(theAWSService);
	}

	// Check if AWS account object has specific AWS service associated
	public boolean hasServiceAdded(String serviceName) {
		boolean name = false;
		Iterator<AWSService> it = awsServices.iterator();
		while (it.hasNext()) {
			String sn = it.next().getAwsServiceName();
			if (sn.contains(serviceName)) {
				name = true;
				break;
			} else {
				name = false;
			}
		}
		return name;
	}

	// Disassociate AWS service with AWS account
	public void removeAWSService(String serviceName) {
		int x = 0;
		while (x < awsServices.size()) {
			if (awsServices.get(x).getAwsServiceName().contains(serviceName)) {
				awsServices.remove(x);
			}
			x++;
		}
	}

	// Get AWS account Region object for every AWS region screen name
	public Region getAccontRegionObject() {
		if (accountRegion.contains("us-east-1")) {
			region = Region.getRegion(Regions.US_EAST_1);
		} else if (accountRegion.contains("eu-west-1")) {
			region = Region.getRegion(Regions.EU_WEST_1);
		} else if (accountRegion.contains("us-west-2")) {
			region = Region.getRegion(Regions.US_WEST_2);
		}
		return region;
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub tg
		return super.toString() + getAccountAlias();
	}

	@Override
	public String getNodeText() {
		return getAccountAlias();
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
		return getAccountAlias();
	}
}
