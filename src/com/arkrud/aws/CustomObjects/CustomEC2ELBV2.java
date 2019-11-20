package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancingv2.model.Action;
import com.amazonaws.services.elasticloadbalancingv2.model.AvailabilityZone;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeListenersRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeListenersResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancerAttributesRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancerAttributesResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeRulesRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeRulesResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsResult;
import com.amazonaws.services.elasticloadbalancingv2.model.Listener;
import com.amazonaws.services.elasticloadbalancingv2.model.LoadBalancer;
import com.amazonaws.services.elasticloadbalancingv2.model.LoadBalancerAttribute;
import com.amazonaws.services.elasticloadbalancingv2.model.LoadBalancerState;
import com.amazonaws.services.elasticloadbalancingv2.model.Rule;
import com.amazonaws.services.elasticloadbalancingv2.model.RuleCondition;
import com.amazonaws.services.elasticloadbalancingv2.model.Tag;
import com.amazonaws.services.elasticloadbalancingv2.model.TagDescription;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.LinkLikeButton;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.StaticFactories.EC2Common;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomEC2ELBV2 extends LoadBalancer implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private LoadBalancer loadBalancer;
	private AWSAccount account;
	private String objectNickName = "ELBV2";
	Listener listenerOfInterest;
	Rule listenerRule;
	private String[] elbsTableColumnHeaders = { "ELB Name", "DNS Name", "VPC ID", "State", "Availability Zones", "Type", "Created At" };
	private JLabel[] elbOverviewHeaderLabels = { new JLabel("Name: "), new JLabel("ARN: "), new JLabel("DNS Name: "), new JLabel("Scheme: "), new JLabel("Type: "), new JLabel("Availability Zones: "), new JLabel("Creation time: "),
			new JLabel("Hosted Zone: "), new JLabel("State: "), new JLabel("VPC ID: "), new JLabel("IP Address Type: ") };
	private String[] elbListenersTableColumnHeaders = { "Protocol", "Port", "ARN", "Security Policy", "SSL Certificate", "Rules" };
	private String[] securityGroupsTableColumnHeaders = { "Group Name", "GroupID", "Owner", "VPC ID", "Description" };
	private String[] attributesTableColumnHeaders = { "Attribute", "Value" };

	public CustomEC2ELBV2() {
		super();
	}

	public CustomEC2ELBV2(AWSAccount account, String elbName, boolean filtered, String appFilter) {
		List<CustomEC2ELBV2> object = retriveEC2V2ELBs(account, filtered, appFilter);
		Iterator<CustomEC2ELBV2> iterator = object.iterator();
		while (iterator.hasNext()) {
			CustomEC2ELBV2 customEC2ELBv2 = iterator.next();
			if (customEC2ELBv2.getLoadBalancerName().equals(elbName)) {
				this.loadBalancer = customEC2ELBv2;
				this.account = account;
			}
		}
	}

	public CustomEC2ELBV2(AWSAccount account,  boolean filtered, String elbARN, String appFilter) {
		List<CustomEC2ELBV2> object = retriveEC2V2ELBs(account, filtered, appFilter);
		Iterator<CustomEC2ELBV2> iterator = object.iterator();
		while (iterator.hasNext()) {
			CustomEC2ELBV2 customEC2ELBv2 = iterator.next();
			if (customEC2ELBv2.getLoadBalancerArn().equals(elbARN)) {
				this.loadBalancer = customEC2ELBv2;
				this.account = account;
			}
		}
	}

	public CustomEC2ELBV2(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties" };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return elbsTableColumnHeaders;
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
		return UtilMethodsFactory.populateInterfaceImages().get("elbv2-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("elbv2");
	}

	@Override
	public List<AvailabilityZone> getAvailabilityZones() {
		return loadBalancer.getAvailabilityZones();
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getLoadBalancerName());
		summaryData.add(getLoadBalancerArn());
		summaryData.add(getDNSName());
		summaryData.add(getScheme());
		summaryData.add(getType());
		summaryData.add(getAZsCSVList());
		summaryData.add(getCreatedTime());
		summaryData.add(getCanonicalHostedZoneId());
		summaryData.add(getState().getCode());
		summaryData.add(getVpcId());
		summaryData.add(getIpAddressType());
		return summaryData;
	}

	private String getAZsCSVList () {
		Iterator<AvailabilityZone> it = getAvailabilityZones().iterator();
		List<String> getAZsCSVs = new ArrayList<String>();
		while (it.hasNext()) {
			AvailabilityZone availabilityZone = it.next();
			getAZsCSVs.add(availabilityZone.getZoneName());
		}
		return String.join(",", getAZsCSVs);
	}


	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getDNSName());
		summaryData.add(getVpcId());
		summaryData.add(getState().getCode());
		summaryData.add(getAZsCSVList());
		summaryData.add(getType());
		summaryData.add(getCreatedTime());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return getELBTagsData();
	}

	@Override
	public String getCanonicalHostedZoneId() {
		return loadBalancer.getCanonicalHostedZoneId();
	}

	@Override
	public Date getCreatedTime() {
		return loadBalancer.getCreatedTime();
	}

	@Override
	public String getDNSName() {
		return loadBalancer.getDNSName();
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

	public ArrayList<ArrayList<Object>> getELBAttributesData() {
		ArrayList<ArrayList<Object>> attributesData = new ArrayList<ArrayList<Object>>();
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeLoadBalancerAttributesRequest describeLoadBalancerAttributesRequest = new DescribeLoadBalancerAttributesRequest().withLoadBalancerArn(getLoadBalancerArn());
		DescribeLoadBalancerAttributesResult result = elbClient.describeLoadBalancerAttributes(describeLoadBalancerAttributesRequest);
		Iterator<LoadBalancerAttribute> elbListenersIterator = result.getAttributes().iterator();
		while (elbListenersIterator.hasNext()) {
			ArrayList<Object> attributeData = new ArrayList<Object>();
			LoadBalancerAttribute loadBalancerAttribute = elbListenersIterator.next();
			attributeData.add(loadBalancerAttribute.getKey());
			attributeData.add(loadBalancerAttribute.getValue());
			attributesData.add(attributeData);
		}
		return attributesData;
	}

	public ArrayList<ArrayList<Object>> getELBListenersData() {
		ArrayList<ArrayList<Object>> elbListenersData = new ArrayList<ArrayList<Object>>();
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeListenersRequest describeListenersRequest = new DescribeListenersRequest().withLoadBalancerArn(getLoadBalancerArn());
		DescribeListenersResult result = elbClient.describeListeners(describeListenersRequest);
		List<Listener> list = result.getListeners();
		Iterator<Listener> elbListenersIterator = list.iterator();
		while (elbListenersIterator.hasNext()) {
			Listener listener = elbListenersIterator.next();
			ArrayList<Object> listenerData = new ArrayList<Object>();
			listenerData.add(listener);
			listenerData.add(listener.getPort());
			listenerData.add(listener.getListenerArn());
			if (listener.getSslPolicy() != null) {
				listenerData.add(listener.getSslPolicy());
			} else {
				listenerData.add(" - ");
			}
			if (listener.getSslPolicy() != null) {
				listenerData.add(listener.getCertificates().get(0).getCertificateArn());
			} else {
				listenerData.add(" - ");
			}
			listenerData.add("Rules");
			elbListenersData.add(listenerData);
		}
		return elbListenersData;
	}

	public ArrayList<ArrayList<Object>> getELBListenersRulesActionsData() {
		ArrayList<ArrayList<Object>> elbListenerRulesActionsData = new ArrayList<ArrayList<Object>>();
		Iterator<Action> actionsIterator = listenerRule.getActions().iterator();
		while (actionsIterator.hasNext()) {
			Action action = actionsIterator.next();
			ArrayList<Object> elbListenerRuleActionData = new ArrayList<Object>();
			if (action.getRedirectConfig() != null) {
				elbListenerRuleActionData.add(action.getOrder());
			} else {
				elbListenerRuleActionData.add(" - ");
			}
			elbListenerRuleActionData.add(action.getType());
			LinkLikeButton linkLikeIDButton = new LinkLikeButton(action.getTargetGroupArn());
			linkLikeIDButton.setAccount(account);
			linkLikeIDButton.setName("TableLinkLikeButton");
			elbListenerRuleActionData.add(linkLikeIDButton);
			if (action.getRedirectConfig() != null) {
				elbListenerRuleActionData.add(action.getRedirectConfig().getPath());
			} else {
				elbListenerRuleActionData.add(" - ");
			}
			if (action.getRedirectConfig() != null) {
				elbListenerRuleActionData.add(action.getAuthenticateCognitoConfig());
			} else {
				elbListenerRuleActionData.add(" - ");
			}
			if (action.getRedirectConfig() != null) {
				elbListenerRuleActionData.add(action.getAuthenticateOidcConfig());
			} else {
				elbListenerRuleActionData.add(" - ");
			}
			if (action.getRedirectConfig() != null) {
				elbListenerRuleActionData.add(action.getFixedResponseConfig());
			} else {
				elbListenerRuleActionData.add(" - ");
			}
			elbListenerRulesActionsData.add(elbListenerRuleActionData);
		}
		return elbListenerRulesActionsData;
	}

	public ArrayList<ArrayList<Object>> getELBListenersRulesConditionsData() {
		ArrayList<ArrayList<Object>> elbListenerRulesActionsData = new ArrayList<ArrayList<Object>>();
		Iterator<RuleCondition> actionsIterator = listenerRule.getConditions().iterator();
		while (actionsIterator.hasNext()) {
			RuleCondition ruleCondition = actionsIterator.next();
			ArrayList<Object> elbListenerRuleActionData = new ArrayList<Object>();
			if (ruleCondition.getField() != null) {
				elbListenerRuleActionData.add(ruleCondition.getField());
			} else {
				elbListenerRuleActionData.add(" - ");
			}
			if (ruleCondition.getValues().get(0) != null) {
				elbListenerRuleActionData.add(String.join(",", ruleCondition.getValues()));
			} else {
				elbListenerRuleActionData.add(" - ");
			}
			elbListenerRulesActionsData.add(elbListenerRuleActionData);
		}
		return elbListenerRulesActionsData;
	}

	public ArrayList<ArrayList<Object>> getELBListenersRulesData() {
		ArrayList<ArrayList<Object>> elbListenerRulesData = new ArrayList<ArrayList<Object>>();
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeRulesRequest describeRulesRequest = new DescribeRulesRequest().withListenerArn(listenerOfInterest.getListenerArn());
		DescribeRulesResult describeRulesResult = elbClient.describeRules(describeRulesRequest);
		Iterator<Rule> rulesItarator = describeRulesResult.getRules().iterator();
		while (rulesItarator.hasNext()) {
			Rule rule = rulesItarator.next();
			ArrayList<Object> elbListenerRuleData = new ArrayList<Object>();
			elbListenerRuleData.add(rule);
			elbListenerRuleData.add(rule.getIsDefault());
			elbListenerRuleData.add(rule.getPriority());
			// elbListenerRuleData.add(rule.getActions());
			elbListenerRuleData.add("All Actions");
			elbListenerRuleData.add("All Conditions");
			elbListenerRulesData.add(elbListenerRuleData);
		}
		return elbListenerRulesData;
	}

	public ArrayList<ArrayList<Object>> getELBListenersRulesData(String listenerARN) {
		ArrayList<ArrayList<Object>> elbListenerRulesData = new ArrayList<ArrayList<Object>>();
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeListenersRequest describeListenersRequest = new DescribeListenersRequest().withLoadBalancerArn(getLoadBalancerArn());
		DescribeListenersResult result = elbClient.describeListeners(describeListenersRequest);
		List<Listener> list = result.getListeners();
		Iterator<Listener> elbListenersIterator = list.iterator();
		while (elbListenersIterator.hasNext()) {
			Listener listener = elbListenersIterator.next();
			Iterator<Action> rulesItarator = listener.getDefaultActions().iterator();
			while (rulesItarator.hasNext()) {
				Action action = rulesItarator.next();
				ArrayList<Object> elbListenerRuleData = new ArrayList<Object>();
				elbListenerRuleData.add(action.getTargetGroupArn());
				elbListenerRuleData.add(action.getType());
				elbListenerRulesData.add(elbListenerRuleData);
			}
		}
		return elbListenerRulesData;
	}

	public ArrayList<ArrayList<Object>> getELBSecurityGroupsData() {
		ArrayList<ArrayList<Object>> securityGroupsData = new ArrayList<ArrayList<Object>>();
		Iterator<String> elbsecurityGroupsIterator = loadBalancer.getSecurityGroups().iterator();
		while (elbsecurityGroupsIterator.hasNext()) {
			String groupID = elbsecurityGroupsIterator.next();
			DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest().withGroupIds(groupID);
			AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			client.setRegion(account.getAccontRegionObject());
			DescribeSecurityGroupsResult result = client.describeSecurityGroups(request);
			Iterator<SecurityGroup> sgIterator = result.getSecurityGroups().iterator();
			ArrayList<Object> securityGroupData = new ArrayList<Object>();
			while (sgIterator.hasNext()) {
				SecurityGroup securityGroup = sgIterator.next();
				securityGroupData.add(securityGroup.getGroupName());
				securityGroupData.add(securityGroup.getGroupId());
				securityGroupData.add(securityGroup.getVpcId());
				securityGroupData.add(securityGroup.getOwnerId());
				securityGroupData.add(securityGroup.getDescription());
			}
			securityGroupsData.add(securityGroupData);
		}
		return securityGroupsData;
	}

	private ArrayList<ArrayList<Object>> getELBTagsData() {
		ArrayList<ArrayList<Object>> elbTagsData = new ArrayList<ArrayList<Object>>();
		DescribeTagsRequest describeTagsRequest = new DescribeTagsRequest().withResourceArns(getLoadBalancerArn());
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
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
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveEC2V2ELBs(account, true, appFilter);
	}

	@Override
	public String getIpAddressType() {
		return loadBalancer.getIpAddressType();
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

	public Listener getListener(String loadBalancerARN, String listenerARN) {
		Listener theListener = null;
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeListenersRequest describeListenersRequest = new DescribeListenersRequest().withLoadBalancerArn(loadBalancerARN);
		DescribeListenersResult result = elbClient.describeListeners(describeListenersRequest);
		List<Listener> list = result.getListeners();
		Iterator<Listener> elbListenersIterator = list.iterator();
		while (elbListenersIterator.hasNext()) {
			Listener listener = elbListenersIterator.next();
			if (listener.getListenerArn().equals(listenerARN)) {
				theListener = listener;
			}
		}
		return theListener;
	}

	public Listener getListenerOfInterest() {
		return listenerOfInterest;
	}

	public Rule getListenerRule() {
		return listenerRule;
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "General" };
		String[] listPaneHeaders = { "General" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "General" };
		String[] listPaneToolTips = { "General ELB Properties" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneToolTips);
	}

	@Override
	public String getLoadBalancerArn() {
		return loadBalancer.getLoadBalancerArn();
	}

	@Override
	public String getLoadBalancerName() {
		return loadBalancer.getLoadBalancerName();
	}

	@Override
	public String getObjectAWSID() {
		return getLoadBalancerName();
	}

	@Override
	public String getObjectName() {
		return getLoadBalancerName();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { "ELBV2Listeners" } };
		String[][][] columnHeaders0 = { { elbListenersTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		String[][] dataFlags1 = { { "ELBV2SecurityGroups" } };
		String[][][] columnHeaders1 = { { securityGroupsTableColumnHeaders } };
		map.put(dataFlags1, columnHeaders1);
		String[][] dataFlags2 = { { "ELBV2Attributes" } };
		String[][][] columnHeaders2 = { { attributesTableColumnHeaders } };
		map.put(dataFlags2, columnHeaders2);
		String[][] dataFlags3 = { { "Tags" } };
		String[][][] columnHeaders3 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags3, columnHeaders3);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "General", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "Listeners", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "SecurityGroups", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "Attributes", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + getLoadBalancerName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(elbOverviewHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getScheme() {
		return loadBalancer.getScheme();
	}

	@Override
	public List<String> getSecurityGroups() {
		return loadBalancer.getSecurityGroups();
	}

	@Override
	public LoadBalancerState getState() {
		return loadBalancer.getState();
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "ELBV2Listeners" }, { "ELBV2SecurityGroups" }, { "ELBV2Attributes" }, { "Tags" } };
		String[][] tablePaneHeaders = { { "Listeners" }, { "Security Groups" }, { "Attributes" }, { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "ELBV2Listeners" }, { "ELBV2SecurityGroups" }, { "ELBV2Attributes" }, { "Tags" } };
		String[][] tablePaneToolTips = { { objectNickName + " Listeners" }, { objectNickName + " Security Groups" }, { objectNickName + " Attributes" }, { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public String getTreeNodeLeafText() {
		return getLoadBalancerName();
	}

	@Override
	public String getType() {
		return loadBalancer.getType();
	}

	@Override
	public String getVpcId() {
		return loadBalancer.getVpcId();
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
			overviewPanel.getDetailesData(object, object.getAccount(), elbOverviewHeaderLabels, paneName);
		}
	}

	@Override
	public void remove() {
		//// TODO Auto-generated method stub
	}

	private ArrayList<CustomEC2ELBV2> retriveEC2V2ELBs(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomEC2ELBV2> elbs = new ArrayList<CustomEC2ELBV2>();
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest();
		DescribeLoadBalancersResult lbr = elbClient.describeLoadBalancers(request);
		Iterator<LoadBalancer> it = lbr.getLoadBalancers().iterator();
		CustomEC2ELBV2 customEC2ELBV2 = null;
		while (it.hasNext()) {
			LoadBalancer loadBalancer = it.next();
			customEC2ELBV2 = new CustomEC2ELBV2(loadBalancer);
			if (appFilter != null) {
				if (filtered) {
					if (loadBalancer.getLoadBalancerName().matches(appFilter)) {
						elbs.add(customEC2ELBV2);
					}
				} else {
					elbs.add(customEC2ELBV2);
				}
			} else {
				if (filtered) {
					if (loadBalancer.getLoadBalancerName().matches(UtilMethodsFactory.getMatchString(account))) {
						elbs.add(customEC2ELBV2);
					}
				} else {
					elbs.add(customEC2ELBV2);
				}
			}
			customEC2ELBV2.setAccount(account);
		}
		return elbs;
	}


	public static CustomEC2ELBV2 retriveEC2V2ELB(AWSAccount account, String arn) {
		AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest().withLoadBalancerArns(arn);
		DescribeLoadBalancersResult lbr = elbClient.describeLoadBalancers(request);
		Iterator<LoadBalancer> it = lbr.getLoadBalancers().iterator();
		CustomEC2ELBV2 customEC2ELBV2 = null;
		while (it.hasNext()) {
			LoadBalancer loadBalancer = it.next();
			customEC2ELBV2 = new CustomEC2ELBV2(loadBalancer);
		}
		return customEC2ELBV2;
	}


	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	public void setListenerOfInterest(Listener listenerOfInterest) {
		this.listenerOfInterest = listenerOfInterest;
	}

	public void setListenerRule(Rule listenerRule) {
		this.listenerRule = listenerRule;
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
}
