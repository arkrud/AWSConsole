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

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancingv2.model.AvailabilityZone;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupAttributesRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupAttributesResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupsRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupsResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetHealthRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetHealthResult;
import com.amazonaws.services.elasticloadbalancingv2.model.Matcher;
import com.amazonaws.services.elasticloadbalancingv2.model.Tag;
import com.amazonaws.services.elasticloadbalancingv2.model.TagDescription;
import com.amazonaws.services.elasticloadbalancingv2.model.TargetGroup;
import com.amazonaws.services.elasticloadbalancingv2.model.TargetGroupAttribute;
import com.amazonaws.services.elasticloadbalancingv2.model.TargetHealthDescription;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.LinkLikeButton;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomEC2TargetGroup extends TargetGroup implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private AWSAccount account;
	private String objectNickName = "TargetGroup";
	private TargetGroup targetGroup;
	private String[] tgTableColumnHeaders = { "Name", "ARN", "Port", "Protocol", "Target Type", "Load Balancer", "VPC ID" };
	private String[] elbTargetsTableColumnHeaders = { "Name", "Instance ID", "Port", "Availability Zone", "Health", "Reason" };
	private String[] elbAZsTableColumnHeaders = { "Availability Zone", "Subnet ID", "Target Count", "Health" };
	private String[] attributesTableColumnHeaders = { "Attribute", "Value" };
	private JLabel[] tgOverviewHeaderLabels = { new JLabel("Name: "), new JLabel("ARN: "), new JLabel("Port: "), new JLabel("Protocol: "),
			new JLabel("Target Type: "), new JLabel("Load Balancer: "), new JLabel("VPC ID: ") };
	private JLabel[] tgHealthCheckHeaderLabels = { new JLabel("Protocol:"), new JLabel("Path:"), new JLabel("Port:"), new JLabel("Healthy threshold:"),
			new JLabel("Unhealthy threshold:"), new JLabel("Timeout:"), new JLabel("Interval:"), new JLabel("Success codes:") };

	public CustomEC2TargetGroup() {
		super();
	}

	public CustomEC2TargetGroup(TargetGroup targetGroup) {
		this.targetGroup = targetGroup;
	}

	public CustomEC2TargetGroup(AWSAccount account, String tgName, boolean filtered, String appFilter) {
		List<CustomEC2TargetGroup> object = retriveEC2TargetGroups(account, filtered, appFilter);
		Iterator<CustomEC2TargetGroup> iterator = object.iterator();
		while (iterator.hasNext()) {
			CustomEC2TargetGroup customEC2TargetGroup = iterator.next();
			if (customEC2TargetGroup.getTargetGroupName().equals(tgName)) {
				this.targetGroup = customEC2TargetGroup;
				this.account = account;
			}
		}
	}

	public CustomEC2TargetGroup(AWSAccount account, String arn) {
		CustomEC2TargetGroup targetGroup = retriveEC2TargetGroup(account, arn);
		this.targetGroup = targetGroup;
		this.account = account;
	}

	private ArrayList<CustomEC2TargetGroup> retriveEC2TargetGroups(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomEC2TargetGroup> tgs = new ArrayList<CustomEC2TargetGroup>();
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeTargetGroupsRequest request = new DescribeTargetGroupsRequest();
		DescribeTargetGroupsResult result = elbClient.describeTargetGroups(request);
		Iterator<TargetGroup> it = result.getTargetGroups().iterator();
		CustomEC2TargetGroup customEC2TargetGroup = null;
		while (it.hasNext()) {
			TargetGroup targetGroup = it.next();
			customEC2TargetGroup = new CustomEC2TargetGroup(targetGroup);
			if (appFilter != null) {
				if (filtered) {
					if (targetGroup.getTargetGroupName().matches(appFilter)) {
						tgs.add(customEC2TargetGroup);
					}
				} else {
					tgs.add(customEC2TargetGroup);
				}
			} else {
				if (filtered) {
					if (targetGroup.getTargetGroupName().matches(UtilMethodsFactory.getMatchString(account))) {
						tgs.add(customEC2TargetGroup);
					}
				} else {
					tgs.add(customEC2TargetGroup);
				}
			}
			customEC2TargetGroup.setAccount(account);
		}
		return tgs;
	}

	private CustomEC2TargetGroup retriveEC2TargetGroup(AWSAccount account, String arn) {
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeTargetGroupsRequest request = new DescribeTargetGroupsRequest().withTargetGroupArns(arn);
		DescribeTargetGroupsResult result = elbClient.describeTargetGroups(request);
		Iterator<TargetGroup> it = result.getTargetGroups().iterator();
		CustomEC2TargetGroup customEC2TargetGroup = null;
		while (it.hasNext()) {
			TargetGroup targetGroup = it.next();
			customEC2TargetGroup = new CustomEC2TargetGroup(targetGroup);
		}
		return customEC2TargetGroup;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties" };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return tgTableColumnHeaders;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { "Select single " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { objectNickName + " Properties" };
		return menus;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("targetgroup-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("targetgroup");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getTargetGroupName());
		summaryData.add(getTargetGroupArn());
		summaryData.add(getPort());
		summaryData.add(getProtocol());
		summaryData.add(getTargetType());
		summaryData.add(getTGELBs(getLoadBalancerArns()));
		summaryData.add(getVpcId());
		return summaryData;
	}

	private List<String> getTGELBNames(List<String> elbARNs) {
		List<String> elbs = new ArrayList<String>();
		Iterator<String> it = elbARNs.iterator();
		while (it.hasNext()) {
			String arn = it.next();
			elbs.add(CustomEC2ELBV2.retriveEC2V2ELB(getAccount(), arn).getLoadBalancerName());
		}
		return elbs;
	}

	private List<CustomEC2ELBV2> getTGELBs(List<String> elbARNs) {
		List<CustomEC2ELBV2> elbs = new ArrayList<CustomEC2ELBV2>();
		Iterator<String> it = elbARNs.iterator();
		while (it.hasNext()) {
			String arn = it.next();
			elbs.add(CustomEC2ELBV2.retriveEC2V2ELB(getAccount(), arn));
		}
		return elbs;
	}

	public ArrayList<Object> getHealthCheckPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getHealthCheckProtocol());
		summaryData.add(getHealthCheckPath());
		summaryData.add(getHealthCheckPort());
		summaryData.add(getHealthyThresholdCount());
		summaryData.add(getUnhealthyThresholdCount());
		summaryData.add(getHealthCheckTimeoutSeconds());
		summaryData.add(getHealthCheckIntervalSeconds());
		summaryData.add(getMatcher().getHttpCode());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getTargetGroupName());
		summaryData.add(getTargetGroupArn());
		summaryData.add(getPort());
		summaryData.add(getProtocol());
		summaryData.add(getTargetType());
		summaryData.add(String.join(",", getTGELBNames(getLoadBalancerArns())));
		summaryData.add(getVpcId());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return getTGTagsData();
	}

	public ArrayList<ArrayList<Object>> getTGAttributesData() {
		ArrayList<ArrayList<Object>> attributesData = new ArrayList<ArrayList<Object>>();
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeTargetGroupAttributesRequest describeLoadBalancerAttributesRequest = new DescribeTargetGroupAttributesRequest()
				.withTargetGroupArn(getTargetGroupArn());
		DescribeTargetGroupAttributesResult result = elbClient.describeTargetGroupAttributes(describeLoadBalancerAttributesRequest);
		Iterator<TargetGroupAttribute> tgListenersIterator = result.getAttributes().iterator();
		while (tgListenersIterator.hasNext()) {
			ArrayList<Object> attributeData = new ArrayList<Object>();
			TargetGroupAttribute tgAttribute = tgListenersIterator.next();
			attributeData.add(tgAttribute.getKey());
			attributeData.add(tgAttribute.getValue());
			attributesData.add(attributeData);
		}
		return attributesData;
	}

	public ArrayList<ArrayList<Object>> getTGTargetsData() {
		ArrayList<ArrayList<Object>> tgTargetsData = new ArrayList<ArrayList<Object>>();
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeTargetHealthRequest request = new DescribeTargetHealthRequest().withTargetGroupArn(getTargetGroupArn());
		DescribeTargetHealthResult result = elbClient.describeTargetHealth(request);
		Iterator<TargetHealthDescription> targetHealthDescriptionsIterator = result.getTargetHealthDescriptions().iterator();
		while (targetHealthDescriptionsIterator.hasNext()) {
			TargetHealthDescription targetHealthDescription = targetHealthDescriptionsIterator.next();
			ArrayList<Object> tgTargetData = new ArrayList<Object>();
			tgTargetData.add(new CustomEC2Instance(getAccount(), targetHealthDescription.getTarget().getId()));
			LinkLikeButton linkLikeIDButton = new LinkLikeButton(targetHealthDescription.getTarget().getId());
			linkLikeIDButton.setAccount(account);
			linkLikeIDButton.setName("TableLinkLikeButton");
			tgTargetData.add(linkLikeIDButton);
			tgTargetData.add(targetHealthDescription.getTarget().getPort());
			tgTargetData.add(new CustomEC2Instance(getAccount(), targetHealthDescription.getTarget().getId()).getPlacement().getAvailabilityZone());
			tgTargetData.add(targetHealthDescription.getTargetHealth().getState());
			String reason = "";
			if (targetHealthDescription.getTargetHealth().getReason() == null) {
				reason = "HealthCheck Passed";
			} else {
				reason = targetHealthDescription.getTargetHealth().getReason();
			}
			tgTargetData.add(reason);
			tgTargetsData.add(tgTargetData);
		}
		return tgTargetsData;
	}

	public int getTGTargetsInAZCount(String azName) {
		int tgTargetsInAZCount = 0;
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeTargetHealthRequest request = new DescribeTargetHealthRequest().withTargetGroupArn(getTargetGroupArn());
		DescribeTargetHealthResult result = elbClient.describeTargetHealth(request);
		Iterator<TargetHealthDescription> targetHealthDescriptionsIterator = result.getTargetHealthDescriptions().iterator();
		while (targetHealthDescriptionsIterator.hasNext()) {
			TargetHealthDescription targetHealthDescription = targetHealthDescriptionsIterator.next();
			if ((new CustomEC2Instance(getAccount(), targetHealthDescription.getTarget().getId()).getPlacement().getAvailabilityZone().equals(azName))) {
				tgTargetsInAZCount++;
			}
		}
		return tgTargetsInAZCount;
	}

	public String getTGTargetsInAZStatus(String azName) {
		String tgTargetsInAZStatus = "unhealthy";
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeTargetHealthRequest request = new DescribeTargetHealthRequest().withTargetGroupArn(getTargetGroupArn());
		DescribeTargetHealthResult result = elbClient.describeTargetHealth(request);
		Iterator<TargetHealthDescription> targetHealthDescriptionsIterator = result.getTargetHealthDescriptions().iterator();
		while (targetHealthDescriptionsIterator.hasNext()) {
			TargetHealthDescription targetHealthDescription = targetHealthDescriptionsIterator.next();
			if ((targetHealthDescription.getTargetHealth().getState()).equals("healthy")) {
				tgTargetsInAZStatus = "healthy";
				break;
			} else {
				tgTargetsInAZStatus = "unhealthy";
			}
		}
		return tgTargetsInAZStatus;
	}

	public ArrayList<ArrayList<Object>> getTGTargetsAZsData() {
		ArrayList<ArrayList<Object>> tgTargetsAZsData = new ArrayList<ArrayList<Object>>();
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest().withLoadBalancerArns(getLoadBalancerArns().get(0));
		DescribeLoadBalancersResult lbr = elbClient.describeLoadBalancers(request);
		Iterator<AvailabilityZone> lbrZonesIterator = lbr.getLoadBalancers().get(0).getAvailabilityZones().iterator();
		while (lbrZonesIterator.hasNext()) {
			AvailabilityZone availabilityZone = lbrZonesIterator.next();
			ArrayList<Object> tgTargetsAZData = new ArrayList<Object>();
			tgTargetsAZData.add(availabilityZone.getZoneName());
			tgTargetsAZData.add(availabilityZone.getSubnetId());
			tgTargetsAZData.add(getTGTargetsInAZCount(availabilityZone.getZoneName()));
			tgTargetsAZData.add(getTGTargetsInAZStatus(availabilityZone.getZoneName()));
			tgTargetsAZsData.add(tgTargetsAZData);
		}
		return tgTargetsAZsData;
	}

	private ArrayList<ArrayList<Object>> getTGTagsData() {
		ArrayList<ArrayList<Object>> elbTagsData = new ArrayList<ArrayList<Object>>();
		DescribeTagsRequest describeTagsRequest = new DescribeTagsRequest().withResourceArns(getTargetGroupArn());
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeTagsResult response = elbClient.describeTags(describeTagsRequest);
		Iterator<TagDescription> tagsDercriptionIterator = response.getTagDescriptions().iterator();
		while (tagsDercriptionIterator.hasNext()) {
			TagDescription descr = tagsDercriptionIterator.next();
			Iterator<Tag> tagsIterator = descr.getTags().iterator();
			while (tagsIterator.hasNext()) {
				ArrayList<Object> elbTagData = new ArrayList<Object>();
				Tag tag = tagsIterator.next();
				elbTagData.add(tag.getKey());
				elbTagData.add(tag.getValue());
				elbTagsData.add(elbTagData);
			}
		}
		return elbTagsData;
	}

	@Override
	public String getDocumentTabHeaders(String paneIdentifier) {
		return null;
	}

	@Override
	public String getDocumentTabToolTips(String paneIdentifier) {
		return null;
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveEC2TargetGroups(account, true, appFilter);
	}

	@Override
	public Boolean getHealthCheckEnabled() {
		return targetGroup.getHealthCheckEnabled();
	}

	@Override
	public Integer getHealthCheckIntervalSeconds() {
		return targetGroup.getHealthCheckIntervalSeconds();
	}

	@Override
	public String getHealthCheckPath() {
		return targetGroup.getHealthCheckPath();
	}

	@Override
	public String getHealthCheckPort() {
		return targetGroup.getHealthCheckPort();
	}

	@Override
	public String getHealthCheckProtocol() {
		return targetGroup.getHealthCheckProtocol();
	}

	@Override
	public Integer getHealthCheckTimeoutSeconds() {
		return targetGroup.getHealthCheckTimeoutSeconds();
	}

	@Override
	public Integer getHealthyThresholdCount() {
		return targetGroup.getHealthyThresholdCount();
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		events.add(KeyEvent.VK_2);
		events.add(KeyEvent.VK_3);
		events.add(KeyEvent.VK_4);
		return events;
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "General", objectNickName + "HealthCheck" };
		String[] listPaneHeaders = { "General", "HealthCheck" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "General", objectNickName + "HealthCheck" };
		String[] listPaneToolTips = { "General Target Group Properties", "Target Group Health Check" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneToolTips);
	}

	@Override
	public List<String> getLoadBalancerArns() {
		return targetGroup.getLoadBalancerArns();
	}

	@Override
	public Matcher getMatcher() {
		return targetGroup.getMatcher();
	}

	@Override
	public String getObjectAWSID() {
		return null;
	}

	@Override
	public String getObjectName() {
		return getTargetGroupName();
	}

	@Override
	public Integer getPort() {
		return targetGroup.getPort();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { "TargetGroupTargets", "TargetGroupAvailabilityZones" } };
		String[][][] columnHeaders0 = { { elbTargetsTableColumnHeaders, elbAZsTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		String[][] dataFlags1 = { { "TargetGroupAttributes" } };
		String[][][] columnHeaders1 = { { attributesTableColumnHeaders } };
		map.put(dataFlags1, columnHeaders1);
		String[][] dataFlags2 = { { "Tags" } };
		String[][][] columnHeaders2 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags2, columnHeaders2);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "General", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "HealthCheck", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "Targets", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "Attributes", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + getTargetGroupName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(tgOverviewHeaderLabels.length);
		propertyPanelsFieldsCount.add(tgHealthCheckHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getProtocol() {
		return targetGroup.getProtocol();
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "TargetGroupTargets" }, { "TargetGroupAvailabilityZones" }, { "TargetGroupAttributes" }, { "Tags" } };
		String[][] tablePaneHeaders = { { "Targets" }, { "AvailabilityZones" }, { "Attributes" }, { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "TargetGroupTargets" }, { "TargetGroupAvailabilityZones" }, { "TargetGroupAttributes" }, { "Tags" } };
		String[][] tablePaneToolTips = { { objectNickName + " Targets" }, { objectNickName + " AvailabilityZones" }, { objectNickName + " Attributes" },
				{ objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public String getTargetGroupArn() {
		return targetGroup.getTargetGroupArn();
	}

	@Override
	public String getTargetGroupName() {
		return targetGroup.getTargetGroupName();
	}

	@Override
	public String getTargetType() {
		return targetGroup.getTargetType();
	}

	@Override
	public String getTreeNodeLeafText() {
		return getTargetGroupName();
	}

	@Override
	public Integer getUnhealthyThresholdCount() {
		return targetGroup.getUnhealthyThresholdCount();
	}

	@Override
	public String getVpcId() {
		return targetGroup.getVpcId();
	}

	@Override
	public Boolean isHealthCheckEnabled() {
		return targetGroup.isHealthCheckEnabled();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(objectNickName + " Properties")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " PROPERTIES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals(objectNickName + "General")) {
			overviewPanel.getDetailesData(object, object.getAccount(), tgOverviewHeaderLabels, paneName);
		} else if (paneName.equals(objectNickName + "HealthCheck")) {
			overviewPanel.getDetailesData(object, object.getAccount(), tgHealthCheckHeaderLabels, paneName);
		}
	}

	@Override
	public void remove() {
	}

	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		CustomTableViewInternalFrame theFrame = null;
		try {
			theFrame = new CustomTableViewInternalFrame(getpropertiesPaneTitle(),
					UtilMethodsFactory.generateEC2ObjectPropertiesPane(customAWSObject, jScrollableDesktopPan));
			UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(getpropertiesPaneTitle(), jScrollableDesktopPan, theFrame);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(theFrame, "This " + objectNickName + " is Alredy Deleted", objectNickName + " Gone", JOptionPane.WARNING_MESSAGE);
		}
	}
}
