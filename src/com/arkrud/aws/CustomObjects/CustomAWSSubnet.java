package com.arkrud.aws.CustomObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.SubnetIpv6CidrBlockAssociation;
import com.amazonaws.services.ec2.model.Tag;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.StaticFactories.EC2Common;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomAWSSubnet extends Subnet implements CustomAWSObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Subnet subnet;
	private AWSAccount account;
	private String objectNickName = "Subnet";

	public CustomAWSSubnet() {
		super();
	}

	public CustomAWSSubnet(Subnet subnet) {
		this.subnet = subnet;
	}

	public CustomAWSSubnet(AWSAccount account, String subnetID) {

		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		DescribeSubnetsRequest describeSubnetsRequest = new DescribeSubnetsRequest().withSubnetIds(subnetID);
		DescribeSubnetsResult describeSubnetsResult = client.describeSubnets(describeSubnetsRequest);
		this.subnet = describeSubnetsResult.getSubnets().get(0);
		this.account = account;
	}

	@Override
	public String getObjectName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getAssociatedImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTreeNodeLeafText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveVPCSubnets(account, appFilter);
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getpropertiesPaneTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectAWSID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAccount(AWSAccount account) {
		// TODO Auto-generated method stub
	}

	@Override
	public AWSAccount getAccount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getkeyEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		// TODO Auto-generated method stub
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDocumentTabHeaders(String paneIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDocumentTabToolTips(String paneIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAvailabilityZone() {
		return subnet.getAvailabilityZone();
	}

	@Override
	public Integer getAvailableIpAddressCount() {
		return subnet.getAvailableIpAddressCount();
	}

	@Override
	public String getCidrBlock() {
		return super.getCidrBlock();
	}

	@Override
	public Boolean getDefaultForAz() {
		return subnet.getDefaultForAz();
	}

	@Override
	public Boolean isDefaultForAz() {
		return subnet.isDefaultForAz();
	}

	@Override
	public Boolean getMapPublicIpOnLaunch() {
		return subnet.getMapPublicIpOnLaunch();
	}

	@Override
	public Boolean isMapPublicIpOnLaunch() {
		return subnet.isMapPublicIpOnLaunch();
	}

	@Override
	public String getState() {
		return subnet.getState();
	}

	@Override
	public String getSubnetId() {
		return subnet.getSubnetId();
	}

	@Override
	public String getVpcId() {
		return subnet.getVpcId();
	}

	@Override
	public Boolean getAssignIpv6AddressOnCreation() {
		return subnet.getAssignIpv6AddressOnCreation();
	}

	@Override
	public Boolean isAssignIpv6AddressOnCreation() {
		return subnet.isAssignIpv6AddressOnCreation();
	}

	@Override
	public List<SubnetIpv6CidrBlockAssociation> getIpv6CidrBlockAssociationSet() {
		return subnet.getIpv6CidrBlockAssociationSet();
	}

	@Override
	public List<Tag> getTags() {
		return subnet.getTags();
	}

	private ArrayList<CustomAWSSubnet> retriveVPCSubnets(AWSAccount account, String appFilter) {
		ArrayList<CustomAWSSubnet> customAWSSubnet = new ArrayList<CustomAWSSubnet>();
		try {
			AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			DescribeSubnetsRequest describeSubnetsRequest = new DescribeSubnetsRequest();
			DescribeSubnetsResult describeSubnetsResult = client.describeSubnets(describeSubnetsRequest);
			Iterator<Subnet> subnetsIterator = describeSubnetsResult.getSubnets().iterator();
			while (subnetsIterator.hasNext()) {
				if (appFilter != null) {
					if (UtilMethodsFactory.getEC2ObjectFilterTag(subnet.getTags(), "Service").matches(appFilter)) {
						customAWSSubnet.add(new CustomAWSSubnet(subnetsIterator.next()));
					}
				} else {
					if (UtilMethodsFactory.getEC2ObjectFilterTag(subnet.getTags(), "Service").matches(UtilMethodsFactory.getMatchString(account))) {
						customAWSSubnet.add(new CustomAWSSubnet(subnetsIterator.next()));
					}
				}
			}
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		return customAWSSubnet;
	}
}
