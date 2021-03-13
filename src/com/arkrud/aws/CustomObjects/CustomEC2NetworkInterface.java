package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeNetworkInterfacesRequest;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.NetworkInterface;
import com.amazonaws.services.ec2.model.NetworkInterfaceAssociation;
import com.amazonaws.services.ec2.model.NetworkInterfaceAttachment;
import com.amazonaws.services.ec2.model.NetworkInterfaceIpv6Address;
import com.amazonaws.services.ec2.model.NetworkInterfacePrivateIpAddress;
import com.amazonaws.services.ec2.model.Tag;
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

public class CustomEC2NetworkInterface extends NetworkInterface implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private AWSAccount account;
	private NetworkInterface networkInterface;
	String[] tableColumnHeaders = { "Interface Name", "Interface ID", "Subnet ID", "VPC ID", "Zone", "Security Group", "Description", "Instance ID", "Status", "IP v4 Public IP", "Primary Private IP", "Secondayr Private IPs", "IP v6 IPs", "Owner ID",
			"Private DNS", "Source/Destination Check" };
	private String objectNickName = "Network Interface";
	private String action = "Delete";

	public CustomEC2NetworkInterface() {
		super();
	}

	public CustomEC2NetworkInterface(NetworkInterface networkInterface) {
		this.networkInterface = networkInterface;
	}

	public CustomEC2NetworkInterface(AWSAccount account, String interfaceID, String appFilter) {
		CustomEC2NetworkInterface customEC2NetworkInterface = new CustomEC2NetworkInterface();
		List<CustomEC2NetworkInterface> interfaces = retriveEC2NetworkInterfaces(account, appFilter);
		Iterator<CustomEC2NetworkInterface> interfacesIterator = interfaces.iterator();
		while (interfacesIterator.hasNext()) {
			CustomEC2NetworkInterface inter = interfacesIterator.next();
			if (inter.getNetworkInterfaceId().contains(interfaceID)) {
				customEC2NetworkInterface = inter;
			}
		}
		this.account = account;
		this.networkInterface = customEC2NetworkInterface;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return tableColumnHeaders;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName + "(s)" };
		return menus;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName + "(s)" };
		return menus;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("interface-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("interface");
	}

	private String getAssociatedPublicIP() {
		if (getAssociation() != null) {
			return getAssociation().getPublicIp();
		} else {
			return "-";
		}
	}

	@Override
	public NetworkInterfaceAssociation getAssociation() {
		return networkInterface.getAssociation();
	}

	private String getAtachedInstanceID() {
		if (getAttachment() == null) {
			return "-";
		}
		if (getAttachment().getInstanceId() != null) {
			return getAttachment().getInstanceId();
		} else {
			return "-";
		}
	}

	@Override
	public NetworkInterfaceAttachment getAttachment() {
		return networkInterface.getAttachment();
	}

	@Override
	public String getAvailabilityZone() {
		return networkInterface.getAvailabilityZone();
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getObjectName());
		summaryData.add(getNetworkInterfaceId());
		summaryData.add(getSubnetId());
		summaryData.add(getVpcId());
		summaryData.add(getAvailabilityZone());
		summaryData.add(getGroups());
		summaryData.add(getDescription());
		if (getAttachment() == null) {
			summaryData.add("-");
		} else {
			if (getAttachment().getInstanceId() != null) {
				summaryData.add(new CustomEC2Instance(getAccount(), getAtachedInstanceID(), false, null));
			} else {
				summaryData.add("-");
			}
		}
		summaryData.add(getStatus());
		summaryData.add(getAssociatedPublicIP());
		summaryData.add(getPrivateIpAddress());
		summaryData.add(getPrivateIpAddressesList());
		summaryData.add(getIPV6Addresses());
		summaryData.add(getOwnerId());
		summaryData.add(getPrivateDnsName());
		summaryData.add(getSourceDestCheck());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getNetworkInterfaceId());
		summaryData.add(getSubnetId());
		summaryData.add(getVpcId());
		summaryData.add(getAvailabilityZone());
		summaryData.add(getSecurityGroupsString());
		summaryData.add(getDescription());
		summaryData.add(getAtachedInstanceID());
		summaryData.add(getStatus());
		summaryData.add(getAssociatedPublicIP());
		summaryData.add(getPrivateIpAddress());
		summaryData.add(getSecondaryPrivateIpAddresses());
		summaryData.add(getIPV6Addresses());
		summaryData.add(getOwnerId());
		summaryData.add(getPrivateDnsName());
		summaryData.add(getSourceDestCheck());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return UtilMethodsFactory.getAWSObjectTagsData(getTagSet().iterator());
	}

	@Override
	public String getDescription() {
		return networkInterface.getDescription();
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
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveEC2NetworkInterfaces(account, appFilter);
	}

	@Override
	public List<GroupIdentifier> getGroups() {
		return networkInterface.getGroups();
	}

	@Override
	public String getInterfaceType() {
		return networkInterface.getInterfaceType();
	}

	@Override
	public List<NetworkInterfaceIpv6Address> getIpv6Addresses() {
		return networkInterface.getIpv6Addresses();
	}

	private String getIPV6Addresses() {
		if (!getIpv6Addresses().isEmpty()) {
			return String.join(",", getPrivateIpV6AddressesList());
		} else {
			return "-";
		}
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		return events;
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + " Details" };
		String[] tablePaneHeaders = { "Detailes" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + " Details" };
		String[] tablePaneToolTips = { objectNickName + " Properties" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public String getMacAddress() {
		return networkInterface.getMacAddress();
	}

	@Override
	public String getNetworkInterfaceId() {
		return networkInterface.getNetworkInterfaceId();
	}

	@Override
	public String getObjectAWSID() {
		return getNetworkInterfaceId();
	}

	@Override
	public String getObjectName() {
		if (networkInterface.getAttachment() == null) {
			return "For " + networkInterface.getDescription();
		} else {
			if (networkInterface.getAttachment().getInstanceId() != null) {
				return "For instance " + networkInterface.getAttachment().getInstanceId();
			} else {
				return "For " + networkInterface.getDescription();
			}
		}
	}

	@Override
	public String getOwnerId() {
		return networkInterface.getOwnerId();
	}

	@Override
	public String getPrivateDnsName() {
		return networkInterface.getPrivateDnsName();
	}

	@Override
	public String getPrivateIpAddress() {
		return networkInterface.getPrivateIpAddress();
	}

	@Override
	public List<NetworkInterfacePrivateIpAddress> getPrivateIpAddresses() {
		return networkInterface.getPrivateIpAddresses();
	}

	private List<String> getPrivateIpAddressesList() {
		List<String> privateIpAddresses = new ArrayList<String>();
		Stream<NetworkInterfacePrivateIpAddress> intrfaces = getPrivateIpAddresses().stream();
		intrfaces.skip(1).forEach(i -> privateIpAddresses.add(i.getPrivateIpAddress()));
		return privateIpAddresses;
	}

	private List<String> getPrivateIpV6AddressesList() {
		List<String> privateIpAddresses = new ArrayList<String>();
		Stream<NetworkInterfaceIpv6Address> intrfaces = getIpv6Addresses().stream();
		intrfaces.skip(1).forEach(i -> privateIpAddresses.add(i.getIpv6Address()));
		return privateIpAddresses;
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags = { { "Tags" } };
		String[][][] columnHeaders = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags, columnHeaders);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> summaryDataPaneTabs = new LinkedHashMap<String, String>();
		summaryDataPaneTabs.put(objectNickName + " Details", "ListInfoPane");
		summaryDataPaneTabs.put("Tags", "TableInfoPane");
		return summaryDataPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + getNetworkInterfaceId() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(UtilMethodsFactory.convertStringArrayToLabels(tableColumnHeaders).length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getRequesterId() {
		return networkInterface.getRequesterId();
	}

	@Override
	public Boolean getRequesterManaged() {
		return networkInterface.getRequesterManaged();
	}

	private String getSecondaryPrivateIpAddresses() {
		if (getPrivateIpAddressesList().size() > 1) {
			return String.join(",", getPrivateIpAddressesList());
		} else {
			return "-";
		}
	}

	private String getSecurityGroupsString() {
		List<String> securityGroupsIDs = new ArrayList<String>();
		Stream<GroupIdentifier> groupsIdentifiers = getGroups().stream();
		groupsIdentifiers.forEach(gi -> securityGroupsIDs.add(gi.getGroupId()));
		return String.join(",", securityGroupsIDs);
	}

	@Override
	public Boolean getSourceDestCheck() {
		return networkInterface.getSourceDestCheck();
	}

	@Override
	public String getStatus() {
		return networkInterface.getStatus();
	}

	@Override
	public String getSubnetId() {
		return networkInterface.getSubnetId();
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "Tags" } };
		String[][] tablePaneToolTips = { { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "Tags" } };
		String[][] tablePaneToolTips = { { "Volume Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public List<Tag> getTagSet() {
		return networkInterface.getTagSet();
	}

	@Override
	public String getTreeNodeLeafText() {
		return getObjectName();
	}

	@Override
	public String getVpcId() {
		return networkInterface.getVpcId();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(action)) {
		} else if (actionString.contains(objectNickName + " Properties")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " PROPERTIES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		} else if (actionString.equals(action.toUpperCase() + " " + objectNickName.toUpperCase())) {
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals(objectNickName + " Details")) {
			overviewPanel.getDetailesData(object, object.getAccount(), UtilMethodsFactory.convertStringArrayToLabels(tableColumnHeaders), paneName);
		}
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

	private ArrayList<CustomEC2NetworkInterface> retriveEC2NetworkInterfaces(AWSAccount account, String appFilter) {
		ArrayList<CustomEC2NetworkInterface> networkInterfaces = new ArrayList<CustomEC2NetworkInterface>();
		DescribeNetworkInterfacesRequest request = new DescribeNetworkInterfacesRequest();
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		Collection<NetworkInterface> interfaces = client.describeNetworkInterfaces(request).getNetworkInterfaces();
		Iterator<NetworkInterface> interfacesIterator = interfaces.iterator();
		while (interfacesIterator.hasNext()) {
			NetworkInterface networkInterface = interfacesIterator.next();
			if (networkInterface.getGroups().size() > 0) {
				if (appFilter != null) {
					if (networkInterface.getGroups().get(0).getGroupName().matches(appFilter)) {
						CustomEC2NetworkInterface customEC2NetworkInterface = new CustomEC2NetworkInterface(networkInterface);
						networkInterfaces.add(customEC2NetworkInterface);
					}
				} else {
					if (networkInterface.getGroups().get(0).getGroupName().matches(UtilMethodsFactory.getMatchString(account))) {
						CustomEC2NetworkInterface customEC2NetworkInterface = new CustomEC2NetworkInterface(networkInterface);
						networkInterfaces.add(customEC2NetworkInterface);
					}
				}
			}
		}
		return networkInterfaces;
	}
	
	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		CustomTableViewInternalFrame theFrame = new CustomTableViewInternalFrame(getpropertiesPaneTitle(), UtilMethodsFactory.generateEC2ObjectPropertiesPane(customAWSObject, jScrollableDesktopPan));
		UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(getpropertiesPaneTitle(), jScrollableDesktopPan, theFrame);
	}
}
