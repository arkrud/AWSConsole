package com.arkrud.aws;

import java.util.ArrayList;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.arkrud.TreeInterface.TreeNodeState;
import com.arkrud.aws.CustomObjects.CustomRegionObject;

//AWS SDK Static Methods
public class AwsCommon implements TreeNodeState {
	// Retrieve AWS credentials for EC2 Account
	private boolean selected;
	private String accountAlias = "";
	private String nodeText;
	private AWSAccount account;

	public AwsCommon(String nodeText) {
		super();
		this.nodeText = nodeText;
	}

	public AWSAccount getAccount() {
		return account;
	}

	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	public static AWSCredentials getAWSCredentials(String accountAlias) {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider(accountAlias).getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. " + "Please make sure that your credentials file is at the correct " + "location (~/.aws/credentials), and is in valid format.", e);
		}
		return credentials;
	}

	/**
	 * Retrieve list of available EC2 regions. <br>
	 *
	 * @return <code>ArrayList</code> of AWS <code>CustomRegionObject</code>
	 */
	public static ArrayList<CustomRegionObject> getAWSRegions() {
		CustomRegionObject object;
		ArrayList<CustomRegionObject> regions = new ArrayList<CustomRegionObject>();
		object = new CustomRegionObject();
		object.setRegion(Region.getRegion(Regions.US_EAST_1));
		regions.add(object);
		object = new CustomRegionObject();
		object.setRegion(Region.getRegion(Regions.US_WEST_1));
		regions.add(object);
		object = new CustomRegionObject();
		object.setRegion(Region.getRegion(Regions.US_WEST_2));
		regions.add(object);
		object = new CustomRegionObject();
		object.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
		regions.add(object);
		object = new CustomRegionObject();
		object.setRegion(Region.getRegion(Regions.EU_WEST_1));
		regions.add(object);
		object = new CustomRegionObject();
		return regions;
	}

	@Override
	public String getNodeText() {
		// TODO Auto-generated method stub
		return nodeText;
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
		return nodeText;
	}
}
