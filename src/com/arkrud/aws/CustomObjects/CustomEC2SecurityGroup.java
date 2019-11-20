package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.SecurityGroup;
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

public class CustomEC2SecurityGroup extends SecurityGroup implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private AWSAccount account;
	private SecurityGroup securityGroup;
	private String objectNickName = "Security Group";
	private String action = "Delete";
	private String[] securityGroupsRulesTableColumnHeaders = { "Rule Type", "Protocol", "Port Range", "Source Type", "Range" };
	private JLabel[] securityGroupOverviewHeaderLabels = { new JLabel("Group Name:"), new JLabel("Group ID:"), new JLabel("Group Description:"), new JLabel("VPC ID:") };
	private String[] securityGroupsTableColumnHeaders = { "Group Name", "GroupID", "Owner", "VPC ID", "Description", "Tags", "Ingress", "Egress" };

	public CustomEC2SecurityGroup() {
		super();
	}

	public CustomEC2SecurityGroup(AWSAccount account, String groupID, String appFilter) {
		List<CustomEC2SecurityGroup> groups = getSecurityGroups(account, true, appFilter);
		Iterator<CustomEC2SecurityGroup> groupsIterator = groups.iterator();
		while (groupsIterator.hasNext()) {
			CustomEC2SecurityGroup group = groupsIterator.next();
			if (group.getGroupId().contains(groupID)) {
				this.securityGroup = group;
				this.account = account;
			}
		}
	}

	public CustomEC2SecurityGroup(AWSAccount account, String groupID, boolean filtered, String appFilter) {
		CustomEC2SecurityGroup securityGroup = new CustomEC2SecurityGroup();
		List<CustomEC2SecurityGroup> groups = getSecurityGroups(account, false, appFilter);
		Iterator<CustomEC2SecurityGroup> groupsIterator = groups.iterator();
		while (groupsIterator.hasNext()) {
			CustomEC2SecurityGroup group = groupsIterator.next();
			if (group.getGroupId().contains(groupID)) {
				securityGroup = group;
			}
		}
		this.account = account;
		this.securityGroup = securityGroup;
	}

	public CustomEC2SecurityGroup(SecurityGroup securityGroup) {
		super();
		this.securityGroup = securityGroup;
	}

	public static String createSecurityGroup(String account, String vpcId, String groupName, String groupDescription) {
		CreateSecurityGroupRequest csgr = new CreateSecurityGroupRequest();
		csgr.withVpcId(vpcId).withGroupName(groupName).withDescription(groupDescription);
		CreateSecurityGroupResult createSecurityGroupResult = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account)).createSecurityGroup(csgr);
		return createSecurityGroupResult.getGroupId();
	}

	public ArrayList<ArrayList<Object>> getAllSecurityGroupsData(CustomTreeContainer container) {
		ArrayList<ArrayList<Object>> securityGroupsData = new ArrayList<ArrayList<Object>>();
		@SuppressWarnings("unchecked")
		Iterator<CustomEC2SecurityGroup> it = (Iterator<CustomEC2SecurityGroup>) container.getEc2Objects().iterator();
		while (it.hasNext()) {
			securityGroupsData.add(groupData(it));
		}
		return securityGroupsData;
	}

	public ArrayList<ArrayList<Object>> getVPCSecurityGroupsData(CustomTreeContainer container, String vpcID) {
		ArrayList<ArrayList<Object>> securityGroupsData = new ArrayList<ArrayList<Object>>();
		@SuppressWarnings("unchecked")
		Iterator<CustomEC2SecurityGroup> it = (Iterator<CustomEC2SecurityGroup>) container.getEc2Objects().iterator();
		while (it.hasNext()) {
			CustomEC2SecurityGroup group = it.next();
			if (group.getVpcId().equals(vpcID)) {
				securityGroupsData.add(groupData(it));
			}
		}
		return securityGroupsData;
	}

	private ArrayList<Object> groupData(Iterator<CustomEC2SecurityGroup> it) {
		CustomEC2SecurityGroup group = it.next();
		ArrayList<Object> securityGroupData = new ArrayList<Object>();
		securityGroupData.add(UtilMethodsFactory.getEC2ObjectFilterTag(getTags(), "Name"));
		securityGroupData.add(group.getGroupId());
		securityGroupData.add(group);
		if (group.getVpcId() == null) {
			securityGroupData.add("Not in VPC");
		} else {
			securityGroupData.add(group.getVpcId());
		}
		securityGroupData.add(group.getDescription());
		securityGroupData.add(false);
		return securityGroupData;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return securityGroupsTableColumnHeaders;
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

	private void delete() {
		DeleteSecurityGroupRequest deleteSecurityGroupRequest = new DeleteSecurityGroupRequest().withGroupId(getGroupId());
		EC2Common.connectToEC2(AwsCommon.getAWSCredentials(getAccount().getAccountAlias())).deleteSecurityGroup(deleteSecurityGroupRequest);
	}

	private void deleteSecurityGroup(AWSAccount account, CustomEC2SecurityGroup customEC2SecurityGroup, DefaultMutableTreeNode node, JTree tree, Dashboard dash) {
		UtilMethodsFactory.removeAction(account, customEC2SecurityGroup, node, tree, dash, action, objectNickName);
	}

	private void deleteSecurityGroup(CustomTable table) {
		UtilMethodsFactory.removeAction(table, "delete", "Volume");
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("secgroups");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("secgroup");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getGroupName());
		summaryData.add(getGroupId());
		summaryData.add(getDescription());
		summaryData.add(getVpcId());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getGroupId());
		summaryData.add(getOwnerId());
		if (getVpcId() == null) {
			summaryData.add("Not in VPC");
		} else {
			summaryData.add(getVpcId());
		}
		summaryData.add(getDescription());
		summaryData.add("Tags");
		summaryData.add("Ingress");
		summaryData.add("Egress");
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return UtilMethodsFactory.getAWSObjectTagsData(getTags().iterator());
	}

	@Override
	public String getDescription() {
		return securityGroup.getDescription();
	}

	private String getDestinationType(String destination) {
		String destinationType = "";
		if (destination.contains("0.0.0.0/0")) {
			destinationType = "Anywhere";
		} else {
			destinationType = "Custom IP";
		}
		return destinationType;
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return getSecurityGroups(account, true, appFilter);
	}

	@Override
	public String getGroupId() {
		return securityGroup.getGroupId();
	}

	@Override
	public String getGroupName() {
		return securityGroup.getGroupName();
	}

	@Override
	public List<IpPermission> getIpPermissions() {
		return securityGroup.getIpPermissions();
	}

	@Override
	public List<IpPermission> getIpPermissionsEgress() {
		return securityGroup.getIpPermissionsEgress();
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		events.add(KeyEvent.VK_2);
		events.add(KeyEvent.VK_3);
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
	public String getObjectAWSID() {
		return getGroupId();
	}

	@Override
	public String getObjectName() {
		return getGroupName();
	}

	@Override
	public String getOwnerId() {
		return securityGroup.getOwnerId();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags = { { "Ingress" }, { "Egress" }, { "Tags" } };
		String[][][] columnHeaders = { { securityGroupsRulesTableColumnHeaders }, { securityGroupsRulesTableColumnHeaders }, { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags, columnHeaders);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + " Details", "ListInfoPane");
		propertiesPaneTabs.put("Ingress", "TableInfoPane");
		propertiesPaneTabs.put("Egress", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + securityGroup.getGroupName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@SuppressWarnings("deprecation")
	private ArrayList<CustomEC2SecurityGroup> getSecurityGroups(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomEC2SecurityGroup> securityGroups = new ArrayList<CustomEC2SecurityGroup>();
		DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();
		DescribeSecurityGroupsResult result = null;
		try {
			AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			client.setEndpoint("ec2." + account.getAccountRegion() + ".amazonaws.com");
			result = client.describeSecurityGroups(request);
			Iterator<SecurityGroup> sgIterator = result.getSecurityGroups().iterator();
			while (sgIterator.hasNext()) {
				SecurityGroup securityGroup = sgIterator.next();
				if (appFilter != null) {
					if (filtered) {
						if (securityGroup.getGroupName().matches(appFilter)) {
							securityGroups.add(new CustomEC2SecurityGroup(securityGroup));
						}
					} else {
						securityGroups.add(new CustomEC2SecurityGroup(securityGroup));
					}
				} else {
					if (filtered) {
						if (securityGroup.getGroupName().matches(UtilMethodsFactory.getMatchString(account))) {
							securityGroups.add(new CustomEC2SecurityGroup(securityGroup));
						}
					} else {
						securityGroups.add(new CustomEC2SecurityGroup(securityGroup));
					}
				}
			}
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		return securityGroups;
	}

	private String getServiceType(String portRange, String protocol) {
		String type = "";
		if (protocol.contains("tcp")) {
			if (portRange.contains("1521")) {
				type = "Oracle-RDS";
			} else if (portRange.contains("5432")) {
				type = "PostgressSQL";
			} else if (portRange.contains("5439")) {
				type = "Redshift";
			} else if (portRange.contains("3389")) {
				type = "RDP";
			} else if (portRange.contains("3306")) {
				type = "MTSQL/Aurora";
			} else if (portRange.contains("1433")) {
				type = "MS SQL";
			} else if (portRange.contains("995")) {
				type = "POP3S";
			} else if (portRange.contains("993")) {
				type = "IMAPS";
			} else if (portRange.contains("465")) {
				type = "SMTPS";
			} else if (portRange.contains("443")) {
				type = "HTTPS";
			} else if (portRange.contains("389")) {
				type = "LDAP";
			} else if (portRange.contains("143")) {
				type = "IMAP";
			} else if (portRange.contains("110")) {
				type = "POP3";
			} else if (portRange.contains("80")) {
				type = "HTTP";
			} else if (portRange.contains("53")) {
				type = "DNS(TCP)";
			} else if (portRange.contains("25")) {
				type = "SMTP";
			} else if (portRange.contains("22")) {
				type = "SSH";
			} else if (portRange.contains("0 - 65535")) {
				type = "All TCP";
			} else {
				type = "Custom TCP Rule";
			}
		} else if (protocol.contains("udp")) {
			if (portRange.contains("53")) {
				type = "DNS(UDP)";
			} else if (portRange.contains("0 - 65535")) {
				type = "All UDP";
			} else {
				type = "Custom UDP Rule";
			}
		} else if (protocol.contains("icmp")) {
			if (portRange.contains("8 - -1")) {
				type = "Custom ISMP Rule";
			} else {
				type = "All ICMP";
			}
		} else if (protocol.contains("All")) {
			type = "All traffic";
		} else {
			type = "Custom Protocol";
		}
		return type;
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "Ingress" }, { "Egress" }, { "Tags" } };
		String[][] tablePaneHeaders = { { "Ingress" }, { "Egress" }, { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "Ingress" }, { "Egress" }, { "Tags" } };
		String[][] tablePaneToolTips = { { "Ingress Rules" }, { "Egress Rules" }, { "Security Groups Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public List<Tag> getTags() {
		return securityGroup.getTags();
	}

	@Override
	public String getTreeNodeLeafText() {
		return getGroupName();
	}

	@Override
	public String getVpcId() {
		return securityGroup.getVpcId();
	}

	public ArrayList<ArrayList<Object>> groupPolicyRulesData(String rulesType, boolean editable) {
		ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
		Iterator<IpPermission> permissionsIterator;
		if (rulesType.contains("Ingress")) {
			permissionsIterator = getIpPermissions().iterator();
		} else {
			permissionsIterator = getIpPermissionsEgress().iterator();
		}
		while (permissionsIterator.hasNext()) {
			IpPermission permissions = permissionsIterator.next();
			@SuppressWarnings("deprecation")
			Iterator<String> rangesIterator = permissions.getIpRanges().iterator();
			while (rangesIterator.hasNext()) {
				ArrayList<Object> permissionsData = new ArrayList<Object>();
				String range = rangesIterator.next();
				String ports = "";
				String protocol = "";
				if (permissions.getFromPort() == null && permissions.getToPort() == null) {
					ports = "All";
					protocol = "All";
				} else {
					if (permissions.getFromPort().intValue() != permissions.getToPort().intValue()) {
						ports = permissions.getFromPort() + " - " + permissions.getToPort();
					} else {
						ports = permissions.getFromPort() + "";
					}
					if ((permissions.getToPort().intValue() == (new Integer(-1)).intValue())) {
						ports = "N/A";
					}
					if (icmpCodes().containsKey(permissions.getFromPort())) {
						protocol = icmpCodes().get(permissions.getFromPort());
					} else {
						protocol = permissions.getIpProtocol();
					}
				}
				permissionsData.add(getServiceType(ports, permissions.getIpProtocol()));
				permissionsData.add(protocol);
				permissionsData.add(ports);
				permissionsData.add(getDestinationType(range));
				permissionsData.add(range);
				if (editable) {
					permissionsData.add(false);
				}
				data.add(permissionsData);
			}
		}
		return data;
	}

	private Hashtable<Integer, String> icmpCodes() {
		Hashtable<Integer, String> ismp = new Hashtable<Integer, String>();
		ismp.put(0, "Echo Reply");
		ismp.put(3, "Destination Unreachable");
		ismp.put(4, "Source Quench");
		ismp.put(5, "Redirect");
		ismp.put(6, "Alternate Host Address");
		ismp.put(8, "Echo Request");
		ismp.put(9, "Router Advertisement");
		ismp.put(10, "Router Selection");
		ismp.put(11, "Time Exceeded");
		ismp.put(12, "Parameter Problem");
		ismp.put(13, "Timestamp");
		ismp.put(14, "Timestamp Reply");
		ismp.put(15, "Information Request");
		ismp.put(16, "Information Reply");
		ismp.put(17, "Address Mask Request");
		ismp.put(18, "Address Mask Reply");
		ismp.put(30, "Traceroute");
		ismp.put(31, "Datagram Conversion Error");
		ismp.put(32, "Mobile Host Redirect");
		ismp.put(33, "IPv6 Where-Are-You");
		ismp.put(34, "IPv6 I-Am-Here");
		ismp.put(35, "Mobile Registration Request");
		ismp.put(36, "Mobile Registration Reply");
		ismp.put(37, "Domain Name Request");
		ismp.put(38, "Domain Name Reply");
		ismp.put(39, "SKIP");
		ismp.put(40, "Photuris");
		return ismp;
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(action)) {
			deleteSecurityGroup(table);
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
			deleteSecurityGroup(account, (CustomEC2SecurityGroup) object, node, tree, dash);
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals(objectNickName + " Details")) {
			overviewPanel.getDetailesData(object, object.getAccount(), securityGroupOverviewHeaderLabels, paneName);
		}
	}

	@Override
	public void remove() {
		delete();
	}

	public static ArrayList<String> serviceType() {
		ArrayList<String> serviceTypes = new ArrayList<String>();
		serviceTypes.add("Custom TCP Rule");
		serviceTypes.add("Custom UDP Rule");
		serviceTypes.add("Custom ICMP Rule");
		serviceTypes.add("Custom Protocol");
		serviceTypes.add("All TCP");
		serviceTypes.add("All UDP");
		serviceTypes.add("All ICMP");
		serviceTypes.add("All traffic");
		serviceTypes.add("SSH");
		serviceTypes.add("SMTP");
		serviceTypes.add("DNS (UDP)");
		serviceTypes.add("DNS (TCP)");
		serviceTypes.add("HTTP");
		serviceTypes.add("POP3");
		serviceTypes.add("IMAP");
		serviceTypes.add("LDAP");
		serviceTypes.add("HTTPS");
		serviceTypes.add("SMTPS");
		serviceTypes.add("IMAPS");
		serviceTypes.add("POP3S");
		serviceTypes.add("MS SQL");
		serviceTypes.add("MYSQL/Aurora");
		serviceTypes.add("RDP");
		serviceTypes.add("Redshift");
		serviceTypes.add("PostgreSQL");
		serviceTypes.add("Oracle-RDS");
		return serviceTypes;
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
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(securityGroupOverviewHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}
}
