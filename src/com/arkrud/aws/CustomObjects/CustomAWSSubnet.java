package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeFlowLogsRequest;
import com.amazonaws.services.ec2.model.DescribeFlowLogsResult;
import com.amazonaws.services.ec2.model.DescribeRouteTablesRequest;
import com.amazonaws.services.ec2.model.DescribeRouteTablesResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.FlowLog;
import com.amazonaws.services.ec2.model.Route;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.SubnetIpv6CidrBlockAssociation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
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

public class CustomAWSSubnet extends Subnet implements CustomAWSObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Subnet subnet;
	private AWSAccount account;
	private String objectNickName = "Subnet";
	private String[] flowLogsTableColumnHeaders = { "Name", "Flow Log ID", "Filter", "Destination Type", "Destination Name", "IAM Role ARN", "Maximum aggregation interval", "Creation Time", "Status", "Log Line Format" };
	private String[] parametersTableColumnHeaders = { "Name", "Subnet ID", "State", "VPC", "IP4 CIDR", "Available IPv4", "Availability Zone", "Availability Zone ID", "Default Subnet" };
	private String[] routeTableTableColumnHeaders = { "Destination", "Target" };
	
	private JLabel[] parametersOverviewHeaderLabels = { new JLabel("Name: "), new JLabel("ID: "), new JLabel("State: "), new JLabel("VPC: "), new JLabel("IP4 CIDR: "), new JLabel("Available IPv4 Addresses: "), new JLabel("Availability Zone: "),
			new JLabel("Availability Zone ID: "), new JLabel("Default Subnet: "), new JLabel("Subnet ARN: "), new JLabel("Route Table: ") };

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
		return UtilMethodsFactory.getNameTag(getTags());
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return parametersTableColumnHeaders;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { "Select single " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { objectNickName + " Detailes" };
		return menus;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Detailes" };
		return menus;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		if (UtilMethodsFactory.getNameTag(getTags()) == null) {
			summaryData.add("-");
		} else {
			summaryData.add(UtilMethodsFactory.getNameTag(getTags()));
		}
		summaryData.add(getSubnetId());
		summaryData.add(getState());
		summaryData.add(getVpcId());
		summaryData.add(getCidrBlock());
		summaryData.add(getAvailableIpAddressCount());
		summaryData.add(getAvailabilityZone());
		summaryData.add(getAzID(getAvailabilityZone()));
		summaryData.add(getDefaultForAz());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(UtilMethodsFactory.getNameTag(getTags()));
		summaryData.add(getSubnetId());
		summaryData.add(getState());
		summaryData.add(getVpcId());
		summaryData.add(getCidrBlock());
		summaryData.add(getAvailableIpAddressCount());
		summaryData.add(getAvailabilityZone());
		summaryData.add(getAzID(getAvailabilityZone()));
		summaryData.add(getDefaultForAz());
		AmazonIdentityManagementClient iamClient = new AmazonIdentityManagementClient(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()));
		summaryData.add("arn:aws:ec2:" + account.getAccountRegion() + ":" + iamClient.getUser().getUser().getArn().split(":")[4] + ":subnet/" + getSubnetId());
		summaryData.add(getRouteTableIdentification());
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
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("subnet");
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("subnet-big");
	}

	@Override
	public String getTreeNodeLeafText() {
		return UtilMethodsFactory.getNameTag(getTags());
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveVPCSubnets(account);
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "Detailes", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "FlowLogs", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "RouteTable", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Detailes for " + UtilMethodsFactory.getNameTag(getTags()) + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public String getObjectAWSID() {
		return getSubnetId();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { "SubnetFlowLogs" } };
		String[][][] columnHeaders0 = { { flowLogsTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		String[][] dataFlags1 = { { "SubnetRouteTable" } };
		String[][][] columnHeaders1 = { { routeTableTableColumnHeaders } };
		map.put(dataFlags1, columnHeaders1);
		String[][] dataFlags2 = { { "Tags" } };
		String[][][] columnHeaders2 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags2, columnHeaders2);
		return map;
	}

	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
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
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " DETAILES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		}
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(objectNickName + " Detailes")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "SubnetFlowLogs" }, { "SubnetRouteTable"}, { "Tags" } };
		String[][] tablePaneHeaders = { { "Flow Logs" }, { "Subnet Route Table" }, { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "SubnetFlowLogs" }, { "SubnetRouteTable"}, { "Tags" } };
		String[][] tablePaneHeaders = { { objectNickName + " Flow Logs" }, { objectNickName + " Route Table" }, { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
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
		String[] listPaneHeaders = { "API Gateway Detailes" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
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
		overviewPanel.getDetailesData(object, object.getAccount(), parametersOverviewHeaderLabels, paneName);
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

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(parametersOverviewHeaderLabels.length);
		return propertyPanelsFieldsCount;
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
		return subnet.getCidrBlock();
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

	private ArrayList<CustomAWSSubnet> retriveVPCSubnets(AWSAccount account) {
		ArrayList<CustomAWSSubnet> customAWSSubnets = new ArrayList<CustomAWSSubnet>();
		try {
			AmazonEC2 client = EC2Common.connectToEC2(account);
			DescribeSubnetsRequest describeSubnetsRequest = new DescribeSubnetsRequest();
			DescribeSubnetsResult describeSubnetsResult = client.describeSubnets(describeSubnetsRequest);
			Iterator<Subnet> subnetsIterator = describeSubnetsResult.getSubnets().iterator();
			while (subnetsIterator.hasNext()) {
				customAWSSubnets.add(new CustomAWSSubnet(subnetsIterator.next()));
			}
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		return customAWSSubnets;
	}

	public String getAzID(String azName) {
		AmazonEC2 client = EC2Common.connectToEC2(account);
		DescribeAvailabilityZonesResult result = client.describeAvailabilityZones(new DescribeAvailabilityZonesRequest().withZoneNames(azName));
		return result.getAvailabilityZones().get(0).getZoneId();
	}

	public ArrayList<ArrayList<Object>> getSubnetFlowLogs() {
		ArrayList<ArrayList<Object>> flowLogsData = new ArrayList<ArrayList<Object>>();
		AmazonEC2 client = EC2Common.connectToEC2(account);
		DescribeFlowLogsRequest request = new DescribeFlowLogsRequest();
		DescribeFlowLogsResult result = client.describeFlowLogs(request);
		for (int i = 0; i < result.getFlowLogs().size(); i++) {
			ArrayList<Object> flowLogData = new ArrayList<Object>();
			FlowLog log = result.getFlowLogs().get(i);
			if (log.getResourceId().equals(getVpcId())) {
				UtilMethodsFactory.addIfNotNull(flowLogData, UtilMethodsFactory.getNameTag(log.getTags()));
				flowLogData.add(log.getFlowLogId());
				flowLogData.add(log.getTrafficType());
				flowLogData.add(log.getLogDestinationType());
				flowLogData.add(log.getLogDestination());
				UtilMethodsFactory.addIfNotNull(flowLogData, log.getDeliverLogsPermissionArn());
				flowLogData.add(log.getMaxAggregationInterval());
				flowLogData.add(log.getCreationTime());
				flowLogData.add(log.getDeliverLogsStatus());
				flowLogData.add(log.getLogFormat());
				flowLogsData.add(flowLogData);
			}
		}
		return flowLogsData;
	}

	private DescribeRouteTablesResult routTableRequest () {
		AmazonEC2 client = EC2Common.connectToEC2(account);
		DescribeRouteTablesRequest request = new DescribeRouteTablesRequest();
		Filter f = new Filter().withName("association.subnet-id").withValues(getSubnetId());
		DescribeRouteTablesResult result = client.describeRouteTables(request.withFilters(f));
		return result;
	}
	
	public ArrayList<ArrayList<Object>> getSubnetRoutes() {
		ArrayList<ArrayList<Object>> routesData = new ArrayList<ArrayList<Object>>();
		RouteTable routeTable = routTableRequest().getRouteTables().get(0);
		for (int i = 0; i < routeTable.getRoutes().size(); i++) {
			ArrayList<Object> routeData = new ArrayList<Object>();
			String target = "";
			String cIDR = "";
			Route route = routeTable.getRoutes().get(i);
			if (route.getGatewayId() != null) {
				target = route.getGatewayId();
				cIDR = route.getDestinationCidrBlock();
			} else if (route.getInstanceId() != null) {
				target = route.getInstanceId();
				cIDR = route.getDestinationCidrBlock();
			} else if (route.getNatGatewayId() != null) {
				target = route.getNatGatewayId();
				cIDR = route.getDestinationCidrBlock();
			} else if (route.getLocalGatewayId() != null) {
				target = route.getLocalGatewayId();
				cIDR = route.getDestinationCidrBlock();
			} else if (route.getVpcPeeringConnectionId() != null) {
				target = route.getVpcPeeringConnectionId();
				cIDR = route.getDestinationCidrBlock();
			} else if (route.getTransitGatewayId() != null) {
				target = route.getTransitGatewayId();
				cIDR = route.getDestinationCidrBlock();
			}
			if (route.getDestinationCidrBlock() == null) {
				cIDR = route.getDestinationPrefixListId();
			}
			routeData.add(cIDR);
			routeData.add(target);
			routesData.add(routeData);
		}
		return routesData;
	}
	
	private String getRouteTableIdentification() {
		return routTableRequest().getRouteTables().get(0).getRouteTableId() + "/" + UtilMethodsFactory.getNameTag(routTableRequest().getRouteTables().get(0).getTags());
	}
	
}
