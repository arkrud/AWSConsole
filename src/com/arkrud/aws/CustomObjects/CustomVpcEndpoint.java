package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeNetworkInterfacesRequest;
import com.amazonaws.services.ec2.model.DescribeVpcEndpointsRequest;
import com.amazonaws.services.ec2.model.DescribeVpcEndpointsResult;
import com.amazonaws.services.ec2.model.DnsEntry;
import com.amazonaws.services.ec2.model.LastError;
import com.amazonaws.services.ec2.model.NetworkInterface;
import com.amazonaws.services.ec2.model.SecurityGroupIdentifier;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.VpcEndpoint;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.StaticFactories.EC2Common;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomVpcEndpoint extends VpcEndpoint implements CustomAWSObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VpcEndpoint vpcEndpoint;
	private AWSAccount account;
	private String objectNickName = "VPCEndpoint";
	private String[] vpcEndpointTableColumnHeaders = { "Name", "ID", "VPC ID", "Service Name", "Endpoint Type", "Status", "Creation Time" };
	private String[] vpcEndpointDNSNamesTableColumnHeaders = { "DNS Name", "DNS Zone ID" };
	private String[] vpcEndpointSubnetsTableColumnHeaders = { "Subnet Name", "CIDR Block", "Availability Zone", "Availability Zone ID", "SNetwork Interface ID" };
	private String[] vpcEndpointSGTableColumnHeaders = { "Name", "ID", "Group Object Name", "Description" };
	private JLabel[] vpcEndpointOverviewHeaderLabels = { new JLabel("Name: "), new JLabel("ID: "), new JLabel("VPC ID: "), new JLabel("Service Name: "), new JLabel("Endpoint Type: "), new JLabel("Status: "), new JLabel("Creation Time: ") };

	public CustomVpcEndpoint() {
		super();
	}

	public CustomVpcEndpoint(VpcEndpoint vpcEndpoint) {
		this.vpcEndpoint = vpcEndpoint;
	}

	@Override
	public Date getCreationTimestamp() {
		return vpcEndpoint.getCreationTimestamp();
	}

	@Override
	public List<DnsEntry> getDnsEntries() {
		return vpcEndpoint.getDnsEntries();
	}

	@Override
	public List<SecurityGroupIdentifier> getGroups() {
		return vpcEndpoint.getGroups();
	}

	@Override
	public LastError getLastError() {
		return vpcEndpoint.getLastError();
	}

	@Override
	public List<String> getNetworkInterfaceIds() {
		return vpcEndpoint.getNetworkInterfaceIds();
	}

	@Override
	public String getOwnerId() {
		return vpcEndpoint.getOwnerId();
	}

	@Override
	public String getPolicyDocument() {
		return vpcEndpoint.getPolicyDocument();
	}

	@Override
	public Boolean getPrivateDnsEnabled() {
		return vpcEndpoint.getPrivateDnsEnabled();
	}

	@Override
	public Boolean getRequesterManaged() {
		return vpcEndpoint.getRequesterManaged();
	}

	@Override
	public List<String> getRouteTableIds() {
		return vpcEndpoint.getRouteTableIds();
	}

	@Override
	public String getServiceName() {
		return vpcEndpoint.getServiceName();
	}

	@Override
	public String getState() {
		return vpcEndpoint.getState();
	}

	@Override
	public List<String> getSubnetIds() {
		return vpcEndpoint.getSubnetIds();
	}

	@Override
	public List<Tag> getTags() {
		return vpcEndpoint.getTags();
	}

	@Override
	public String getVpcEndpointId() {
		return vpcEndpoint.getVpcEndpointId();
	}

	@Override
	public String getVpcEndpointType() {
		return vpcEndpoint.getVpcEndpointType();
	}

	@Override
	public String getVpcId() {
		return vpcEndpoint.getVpcId();
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Detailes" };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return vpcEndpointTableColumnHeaders;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { "Select single " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { objectNickName + " Detailes" };
		return menus;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("vpcendpoint-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("vpcendpoint");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		if (UtilMethodsFactory.getNameTag(getTags()) == null) {
			summaryData.add("-");
		} else {
			summaryData.add(UtilMethodsFactory.getNameTag(getTags()));
		}
		summaryData.add(getVpcEndpointId());
		summaryData.add(getVpcId());
		summaryData.add(getServiceName());
		summaryData.add(getVpcEndpointType());
		summaryData.add(getState());
		summaryData.add(getCreationTimestamp());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		if (UtilMethodsFactory.getNameTag(getTags()) == null) {
			summaryData.add("-");
		} else {
			summaryData.add(UtilMethodsFactory.getNameTag(getTags()));
		}
		summaryData.add(getVpcEndpointId());
		summaryData.add(getVpcId());
		summaryData.add(getServiceName());
		summaryData.add(getVpcEndpointType());
		summaryData.add(getState());
		summaryData.add(getCreationTimestamp());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		ArrayList<ArrayList<Object>> tagsData = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < getTags().size(); i++) {
			ArrayList<Object> tagData = new ArrayList<Object>();
			tagData.add(getTags().get(i).getKey());
			tagData.add(getTags().get(i).getValue());
			tagsData.add(tagData);
		}
		return tagsData;
	}

	@Override
	public String getDocumentTabHeaders(String paneIdentifier) {
		String[] documentPaneIdentifiers = { "VPCEndpointPolicy" };
		String[] documentPaneIPaneHeaders = { "Execution Policy" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneIPaneHeaders);
	}

	@Override
	public String getDocumentTabToolTips(String paneIdentifier) {
		String[] documentPaneIdentifiers = { "VPCEndpointPolicy" };
		String[] documentPaneIPaneHeaders = { "Execution Policy" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneIPaneHeaders);
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveVPCEndpoints(account, true, appFilter);
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		events.add(KeyEvent.VK_2);
		events.add(KeyEvent.VK_3);
		events.add(KeyEvent.VK_4);
		events.add(KeyEvent.VK_5);
		return events;
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "Detailes" };
		String[] listPaneHeaders = { "Detailes" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "Detailes" };
		String[] listPaneHeaders = { "Topic Detailes" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getObjectAWSID() {
		return UtilMethodsFactory.getNameTag(getTags());
	}

	@Override
	public String getObjectName() {
		return UtilMethodsFactory.getNameTag(getTags());
	}
	// private String[] vpcEndpointDNSNamesTableColumnHeaders = { "DNS Name", "DNS Zone ID" };
	// private String[] vpcEndpointSubnetsTableColumnHeaders = { "Availability Zone", "Availability Zone ID", "CIDR Block", "SNetwork Interface ID" };
	// private String[] vpcEndpointSGTableColumnHeaders = { "Name", "ID", "Group Object Name", "DEscription" };

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { "VPCEndpointDNSNames" } };
		String[][][] columnHeaders0 = { { vpcEndpointDNSNamesTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		String[][] dataFlags1 = { { "VPCEndpointSubnets" } };
		String[][][] columnHeaders1 = { { vpcEndpointSubnetsTableColumnHeaders } };
		map.put(dataFlags1, columnHeaders1);
		String[][] dataFlags2 = { { "VPCEndpointSecurutyGroups" } };
		String[][][] columnHeaders2 = { { vpcEndpointSGTableColumnHeaders } };
		map.put(dataFlags2, columnHeaders2);
		String[][] dataFlags3 = { { "Tags" } };
		String[][][] columnHeaders3 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags3, columnHeaders3);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		vpcEndpointSubnetsData();
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "Detailes", "ListInfoPane");
		propertiesPaneTabs.put("VPCEndpointPolicy", "DocumentInfoPane");
		propertiesPaneTabs.put("VPCEndpointDNSNames", "TableInfoPane");
		propertiesPaneTabs.put("VPCEndpointSubnets", "TableInfoPane");
		propertiesPaneTabs.put("VPCEndpointSecurutyGroups", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Detailes for " + UtilMethodsFactory.getNameTag(getTags()) + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(vpcEndpointOverviewHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "VPCEndpointDNSNames" }, { "VPCEndpointSubnets" }, { "VPCEndpointSecurutyGroups" }, { "Tags" } };
		String[][] tablePaneHeaders = { { objectNickName + " VPC Endpoint DNS Names" }, { objectNickName + " VPC Endpoint Subnets" }, { objectNickName + " VPC Endpoint Securuty Groups" }, { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "VPCEndpointDNSNames" }, { "VPCEndpointSubnets" }, { "VPCEndpointSecurutyGroups" }, { "Tags" } };
		String[][] tablePaneHeaders = { { objectNickName + " VPC Endpoint DNS Names" }, { objectNickName + " VPC Endpoint Subnets" }, { objectNickName + " VPC Endpoint Securuty Groups" }, { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTreeNodeLeafText() {
		return UtilMethodsFactory.getNameTag(getTags());
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(objectNickName + " Detailes")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " DETAILES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		overviewPanel.getDetailesData(object, object.getAccount(), vpcEndpointOverviewHeaderLabels, paneName);
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		CustomTableViewInternalFrame theFrame = null;
		try {
			theFrame = new CustomTableViewInternalFrame(getpropertiesPaneTitle(), UtilMethodsFactory.generateEC2ObjectPropertiesPane(customAWSObject, jScrollableDesktopPan));
			UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(getpropertiesPaneTitle(), jScrollableDesktopPan, theFrame);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(theFrame, "This " + objectNickName + " is Alredy Deleted", objectNickName + " Gone", JOptionPane.WARNING_MESSAGE);
		}
	}

	private ArrayList<CustomVpcEndpoint> retriveVPCEndpoints(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomVpcEndpoint> customVPCEndpoints = new ArrayList<CustomVpcEndpoint>();
		ArrayList<VpcEndpoint> endpoints = new ArrayList<VpcEndpoint>();
		AmazonEC2 client = EC2Common.connectToEC2(account);
		try {
			String nextToken = null;
			DescribeVpcEndpointsResult result;
			do {
				result = client.describeVpcEndpoints(new DescribeVpcEndpointsRequest().withNextToken(nextToken));
				endpoints.addAll(result.getVpcEndpoints());
				nextToken = result.getNextToken();
			} while (nextToken != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < endpoints.size(); i++) {
			if (appFilter != null) {
				if (filtered) {
					if (UtilMethodsFactory.getNameTag(endpoints.get(i).getTags()).matches(appFilter)) {
						customVPCEndpoints.add(new CustomVpcEndpoint(endpoints.get(i)));
					}
				} else {
					endpoints.add(new CustomVpcEndpoint(endpoints.get(i)));
				}
			} else {
				if (filtered) {
					if (UtilMethodsFactory.getNameTag(endpoints.get(i).getTags()).matches(UtilMethodsFactory.getMatchString(account))) {
						endpoints.add(new CustomVpcEndpoint(endpoints.get(i)));
					}
				} else {
					endpoints.add(new CustomVpcEndpoint(endpoints.get(i)));
				}
			}
		}
		return customVPCEndpoints;
	}

	public ArrayList<ArrayList<Object>> getVPCEndpointDNSNames() {
		ArrayList<ArrayList<Object>> vpcEndpointDNSNames = new ArrayList<ArrayList<Object>>();
		List<DnsEntry> dnsNames = getDnsEntries();
		for (int i = 0; i < dnsNames.size(); i++) {
			ArrayList<Object> vpcEndpointDNSName = new ArrayList<Object>();
			vpcEndpointDNSName.add(dnsNames.get(i).getDnsName());
			vpcEndpointDNSName.add(dnsNames.get(i).getHostedZoneId());
			vpcEndpointDNSNames.add(vpcEndpointDNSName);
		}
		return vpcEndpointDNSNames;
	}

	public ArrayList<ArrayList<Object>> vpcEndpointSubnetsData() {
		ArrayList<ArrayList<Object>> vpcEndpointSubnetsData = new ArrayList<ArrayList<Object>>();
		List<String> subnetIDs = getSubnetIds();
		for (int i = 0; i < subnetIDs.size(); i++) {
			ArrayList<Object> vpcEndpointSubnetData = new ArrayList<Object>();
			CustomAWSSubnet customAWSSubnet = new CustomAWSSubnet(account, subnetIDs.get(i));
			vpcEndpointSubnetData.add(customAWSSubnet.getSubnetId());
			vpcEndpointSubnetData.add(customAWSSubnet.getCidrBlock());
			vpcEndpointSubnetData.add(customAWSSubnet.getAvailabilityZone());
			vpcEndpointSubnetData.add(customAWSSubnet.getAzID(customAWSSubnet.getAvailabilityZone()));
			vpcEndpointSubnetData.add(getNetInterfaceNameForSubnet(customAWSSubnet.getSubnetId()));
			vpcEndpointSubnetsData.add(vpcEndpointSubnetData);
		}
		return vpcEndpointSubnetsData;
	}

	private String getNetInterfaceNameForSubnet(String subnetID) {
		String interfaceId = "";
		List<String> interfaceIDs = getNetworkInterfaceIds();
		for (int i = 0; i < interfaceIDs.size(); i++) {
			DescribeNetworkInterfacesRequest request = new DescribeNetworkInterfacesRequest().withNetworkInterfaceIds(interfaceIDs.get(i));
			AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			Collection<NetworkInterface> interfaces = client.describeNetworkInterfaces(request).getNetworkInterfaces();
			Iterator<NetworkInterface> iterator = interfaces.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getSubnetId().equals(subnetID)) {
					interfaceId = interfaceIDs.get(i);
				}
			}
		}
		return interfaceId;
	}

	public ArrayList<ArrayList<Object>> vpcEndpointSGData() {
		ArrayList<ArrayList<Object>> vpcEndpointSGsData = new ArrayList<ArrayList<Object>>();
		List<SecurityGroupIdentifier> securityGroups = getGroups();
		for (int i = 0; i < securityGroups.size(); i++) {
			ArrayList<Object> vpcEndpointSGData = new ArrayList<Object>();
			CustomEC2SecurityGroup customEC2SEcurityGroup = new CustomEC2SecurityGroup(account, securityGroups.get(i).getGroupId());
			vpcEndpointSGData.add(UtilMethodsFactory.getEC2ObjectFilterTag(customEC2SEcurityGroup.getTags(), "Name"));
			vpcEndpointSGData.add(customEC2SEcurityGroup.getGroupId());
			vpcEndpointSGData.add(customEC2SEcurityGroup.getGroupName());
			vpcEndpointSGData.add(customEC2SEcurityGroup.getDescription());
			vpcEndpointSGsData.add(vpcEndpointSGData);
		}
		return vpcEndpointSGsData;
	}
}
