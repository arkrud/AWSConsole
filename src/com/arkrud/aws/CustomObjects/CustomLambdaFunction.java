package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.eventbridge.AmazonEventBridge;
import com.amazonaws.services.eventbridge.AmazonEventBridgeClientBuilder;
import com.amazonaws.services.eventbridge.model.ListRuleNamesByTargetRequest;
import com.amazonaws.services.eventbridge.model.ListRulesRequest;
import com.amazonaws.services.eventbridge.model.Rule;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementAsyncClientBuilder;
import com.amazonaws.services.identitymanagement.model.ListRolePoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListRolePoliciesResult;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.DeadLetterConfig;
import com.amazonaws.services.lambda.model.EnvironmentResponse;
import com.amazonaws.services.lambda.model.FileSystemConfig;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.GetPolicyRequest;
import com.amazonaws.services.lambda.model.ImageConfigResponse;
import com.amazonaws.services.lambda.model.Layer;
import com.amazonaws.services.lambda.model.LayersListItem;
import com.amazonaws.services.lambda.model.ListFunctionsRequest;
import com.amazonaws.services.lambda.model.ListFunctionsResult;
import com.amazonaws.services.lambda.model.ListLayersRequest;
import com.amazonaws.services.lambda.model.ListLayersResult;
import com.amazonaws.services.lambda.model.ListTagsRequest;
import com.amazonaws.services.lambda.model.TracingConfigResponse;
import com.amazonaws.services.lambda.model.VpcConfigResponse;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.LinkLikeButton;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.StaticFactories.EC2Common;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomLambdaFunction extends FunctionConfiguration implements CustomAWSObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FunctionConfiguration functionConfiguration;
	private AWSAccount account;
	private String objectNickName = "LambdaFunction";
	private String[] lambdasFunctionsTableColumnHeaders = { "Lambda Name", "Runtime", "Handler", "Description", "Memory", "Timout", "VPC ID" };
	private String[] lambdaFunctionLayersTableColumnHeaders = { "Layer Name", "Version", "Version ARN", "Create Date", "Runtimes", "Layer ARN" };
	private String[] lambdaFunctionVariablesTableColumnHeaders = { "Name", "Value" };
	private String[] lambdaFunctionTriggersTableColumnHeaders = { "Trigger Name", "Description", "Event Bus Name", "Schedule Expression", "State", "ARN", "Event Pattern", "Role ARN" };
	private String[] lambdaVPCSubnetsColumnHeaders = { "Subnet Id", "Availability Zone", "CIDR Block" };
	private String[] lambdaVPCSGColumnHeaders = { "Security Group Name", "Security Group ID", "Description" };
	private JLabel[] lambdaFunctionGeneralPropertiesHeaderLabels = { new JLabel("Lambda Name: "), new JLabel("Runtime: "), new JLabel("Handler: "), new JLabel("Description: "), new JLabel("Memory: "), new JLabel("Timout: "), new JLabel("VPC ID: ") };
	private JLabel[] lambdaFunctionAsdvancedPropertiesHeaderLabels = { new JLabel("Code Size: "), new JLabel("Code SHA256: "), new JLabel("Function ARN: "), new JLabel("Modified: "), new JLabel("KSM Key ARN: "), new JLabel("Update Status: "),
			new JLabel("Update Reason: "), new JLabel("Update Reason Code: "), new JLabel("Revision ID: "), new JLabel("Package Type: "), new JLabel("State: "), new JLabel("State Reason: "), new JLabel("Version: "), new JLabel("Signing Job ARN: "),
			new JLabel("Signing Job Profile Version ARN: "), new JLabel(" Master ARN: ") };

	public CustomLambdaFunction() {
		super();
	}

	public CustomLambdaFunction(FunctionConfiguration functionConfiguration) {
		this.functionConfiguration = functionConfiguration;
	}

	@Override
	public String getCodeSha256() {
		return functionConfiguration.getCodeSha256();
	}

	@Override
	public Long getCodeSize() {
		return functionConfiguration.getCodeSize();
	}

	@Override
	public DeadLetterConfig getDeadLetterConfig() {
		return functionConfiguration.getDeadLetterConfig();
	}

	@Override
	public String getDescription() {
		return functionConfiguration.getDescription();
	}

	@Override
	public EnvironmentResponse getEnvironment() {
		return functionConfiguration.getEnvironment();
	}

	@Override
	public List<FileSystemConfig> getFileSystemConfigs() {
		return functionConfiguration.getFileSystemConfigs();
	}

	@Override
	public String getFunctionArn() {
		return functionConfiguration.getFunctionArn();
	}

	@Override
	public String getFunctionName() {
		return functionConfiguration.getFunctionName();
	}

	@Override
	public String getHandler() {
		return functionConfiguration.getHandler();
	}

	@Override
	public ImageConfigResponse getImageConfigResponse() {
		return functionConfiguration.getImageConfigResponse();
	}

	@Override
	public String getKMSKeyArn() {
		return functionConfiguration.getKMSKeyArn();
	}

	@Override
	public String getLastModified() {
		return functionConfiguration.getLastModified();
	}

	@Override
	public String getLastUpdateStatus() {
		return functionConfiguration.getLastUpdateStatus();
	}

	@Override
	public String getLastUpdateStatusReason() {
		return functionConfiguration.getLastUpdateStatusReason();
	}

	@Override
	public String getLastUpdateStatusReasonCode() {
		return functionConfiguration.getLastUpdateStatusReasonCode();
	}

	@Override
	public List<Layer> getLayers() {
		return functionConfiguration.getLayers();
	}

	@Override
	public String getMasterArn() {
		return functionConfiguration.getMasterArn();
	}

	@Override
	public Integer getMemorySize() {
		return functionConfiguration.getMemorySize();
	}

	@Override
	public String getPackageType() {
		return functionConfiguration.getPackageType();
	}

	@Override
	public String getRevisionId() {
		return functionConfiguration.getRevisionId();
	}

	@Override
	public String getRole() {
		return functionConfiguration.getRole();
	}

	@Override
	public String getRuntime() {
		return functionConfiguration.getRuntime();
	}

	@Override
	public String getSigningJobArn() {
		return functionConfiguration.getSigningJobArn();
	}

	@Override
	public String getSigningProfileVersionArn() {
		return functionConfiguration.getSigningProfileVersionArn();
	}

	@Override
	public String getState() {
		return functionConfiguration.getState();
	}

	@Override
	public String getStateReason() {
		return functionConfiguration.getStateReason();
	}

	@Override
	public String getStateReasonCode() {
		return functionConfiguration.getStateReasonCode();
	}

	@Override
	public Integer getTimeout() {
		return functionConfiguration.getTimeout();
	}

	@Override
	public TracingConfigResponse getTracingConfig() {
		return functionConfiguration.getTracingConfig();
	}

	@Override
	public String getVersion() {
		return functionConfiguration.getVersion();
	}

	@Override
	public VpcConfigResponse getVpcConfig() {
		return functionConfiguration.getVpcConfig();
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Settings" };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return lambdasFunctionsTableColumnHeaders;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { "Select single " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { objectNickName + " Settings" };
		return menus;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("functions");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("function");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getFunctionName());
		summaryData.add(getRuntime());
		summaryData.add(getHandler());
		summaryData.add(getDescription());
		summaryData.add(getMemorySize());
		summaryData.add(getTimeout());
		summaryData.add(getVpcConfig().getVpcId());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getFunctionName());
		summaryData.add(getRuntime());
		summaryData.add(getHandler());
		summaryData.add(getDescription());
		summaryData.add(getMemorySize());
		summaryData.add(getTimeout());
		summaryData.add(getVpcConfig().getVpcId());
		return summaryData;
	}

	public ArrayList<Object> getLambdaFunctionAdvancedPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		UtilMethodsFactory.addIfNotNull(summaryData, getCodeSize());
		UtilMethodsFactory.addIfNotNull(summaryData, getCodeSha256());
		UtilMethodsFactory.addIfNotNull(summaryData, getFunctionArn());
		UtilMethodsFactory.addIfNotNull(summaryData, getKMSKeyArn());
		UtilMethodsFactory.addIfNotNull(summaryData, getLastModified());
		UtilMethodsFactory.addIfNotNull(summaryData, getLastUpdateStatus());
		UtilMethodsFactory.addIfNotNull(summaryData, getLastUpdateStatusReason());
		UtilMethodsFactory.addIfNotNull(summaryData, getLastUpdateStatusReasonCode());
		UtilMethodsFactory.addIfNotNull(summaryData, getRevisionId());
		UtilMethodsFactory.addIfNotNull(summaryData, getPackageType());
		UtilMethodsFactory.addIfNotNull(summaryData, getState());
		UtilMethodsFactory.addIfNotNull(summaryData, getStateReason());
		UtilMethodsFactory.addIfNotNull(summaryData, getVersion());
		UtilMethodsFactory.addIfNotNull(summaryData, getSigningJobArn());
		UtilMethodsFactory.addIfNotNull(summaryData, getSigningProfileVersionArn());
		UtilMethodsFactory.addIfNotNull(summaryData, getMasterArn());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		ArrayList<ArrayList<Object>> tagsData = new ArrayList<ArrayList<Object>>();
		Map<String, String> tags = getTags();
		if (tags == null) {
			ArrayList<Object> tagData = new ArrayList<Object>();
			tagData.add("-");
			tagData.add("-");
			tagsData.add(tagData);
		} else {
			Iterator<Map.Entry<String, String>> itr = tags.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, String> entry = itr.next();
				ArrayList<Object> tagData = new ArrayList<Object>();
				tagData.add(entry.getKey());
				tagData.add(entry.getValue());
				tagsData.add(tagData);
			}
		}
		return tagsData;
	}

	@Override
	public String getDocumentTabHeaders(String paneIdentifier) {
		String[] documentPaneIdentifiers = { "ResourceBasedPolicy", "IAMRole" };
		String[] documentPaneIPaneHeaders = { "Resource-Based Policy", "IAM Role" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneIPaneHeaders);
	}

	@Override
	public String getDocumentTabToolTips(String paneIdentifier) {
		String[] documentPaneIdentifiers = { "ResourceBasedPolicy", "IAMRole" };
		String[] documentPaneIPaneHeaders = { "Lambda Function Permissions To Acess AWS Resources", "Grants the function permission to use AWS services" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneIPaneHeaders);
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveLambdaFunctions(account, true, appFilter);
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
		events.add(KeyEvent.VK_6);
		events.add(KeyEvent.VK_7);
		events.add(KeyEvent.VK_8);
		return events;
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "Settings", objectNickName + "Advanced" };
		String[] listPaneHeaders = { "Settings", "Advanced" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "Settings", objectNickName + "Advanced" };
		String[] listPaneHeaders = { "API Gateway Settings", "Lambda Fuinction Advanced" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getObjectAWSID() {
		return getFunctionArn();
	}

	@Override
	public String getObjectName() {
		return getFunctionName();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { "LambdaFunctionLayers" } };
		String[][][] columnHeaders0 = { { lambdaFunctionLayersTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		String[][] dataFlags1 = { { "LambdaFunctionVariables" } };
		String[][][] columnHeaders1 = { { lambdaFunctionVariablesTableColumnHeaders } };
		map.put(dataFlags1, columnHeaders1);
		String[][] dataFlags2 = { { "LambdaFunctionTriggers" } };
		String[][][] columnHeaders2 = { { lambdaFunctionTriggersTableColumnHeaders } };
		map.put(dataFlags2, columnHeaders2);
		String[][] dataFlags3 = { { "LambdaFunctionVPC", "LambdaVPCSG" } };
		String[][][] columnHeaders3 = { { lambdaVPCSubnetsColumnHeaders, lambdaVPCSGColumnHeaders } };
		map.put(dataFlags3, columnHeaders3);
		String[][] dataFlags4 = { { "Tags" } };
		String[][][] columnHeaders4 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags4, columnHeaders4);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "Settings", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "Advanced", "ListInfoPane");
		propertiesPaneTabs.put("ResourceBasedPolicy", "DocumentInfoPane");
		propertiesPaneTabs.put("IAMRole", "DocumentInfoPane");
		propertiesPaneTabs.put(objectNickName + "Layers", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "Variables", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "Triggers", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "VPC", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Settings for " + getFunctionName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(lambdaFunctionGeneralPropertiesHeaderLabels.length);
		propertyPanelsFieldsCount.add(lambdaFunctionAsdvancedPropertiesHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "LambdaFunctionLayers" }, { "LambdaFunctionVariables" }, { "LambdaFunctionTriggers" }, { "LambdaFunctionVPC" }, { "LambdaVPCSG" }, { "Tags" } };
		String[][] tablePaneHeaders = { { "Lambda Function Layers" }, { "Lambda Function Variables" }, { "Lambda Function Triggers" }, { "Lambda VPC Settings" }, { "Lambda VPC SG" }, { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "LambdaFunctionLayers" }, { "LambdaFunctionVariables" }, { "LambdaFunctionTriggers" }, { "LambdaFunctionVPC" }, { "LambdaVPCSG" }, { "Tags" } };
		String[][] tablePaneHeaders = { { objectNickName + " Layers" }, { objectNickName + " Variables" },{ objectNickName + " Triggers" }, { objectNickName + " Lambda VPC Subnets" }, { objectNickName + " Lambda VPC SG" }, { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTreeNodeLeafText() {
		return getFunctionName();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(objectNickName + " Settings")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " SETTINGS")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals("LambdaFunctionAdvanced")) {
			overviewPanel.getDetailesData(object, object.getAccount(), lambdaFunctionAsdvancedPropertiesHeaderLabels, paneName);
		} else {
			overviewPanel.getDetailesData(object, object.getAccount(), lambdaFunctionGeneralPropertiesHeaderLabels, paneName);
		}
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

	private ArrayList<CustomLambdaFunction> retriveLambdaFunctions(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomLambdaFunction> customLambdaFunctions = new ArrayList<CustomLambdaFunction>();
		List<FunctionConfiguration> functionConfigurationList = new ArrayList<>();
		AWSLambda lambdaClient = AWSLambdaClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName())
				.build();
		try {
			String marker = null;
			ListFunctionsResult lfr;
			do {
				lfr = lambdaClient.listFunctions(new ListFunctionsRequest().withMarker(marker));
				functionConfigurationList.addAll(lfr.getFunctions());
				marker = lfr.getNextMarker();
			} while (marker != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < functionConfigurationList.size(); i++) {
			if (appFilter != null) {
				if (filtered) {
					if (functionConfigurationList.get(i).getFunctionName().matches(appFilter)) {
						customLambdaFunctions.add(new CustomLambdaFunction(functionConfigurationList.get(i)));
					}
				} else {
					customLambdaFunctions.add(new CustomLambdaFunction(functionConfigurationList.get(i)));
				}
			} else {
				if (filtered) {
					if (functionConfigurationList.get(i).getFunctionName().matches(UtilMethodsFactory.getMatchString(account))) {
						customLambdaFunctions.add(new CustomLambdaFunction(functionConfigurationList.get(i)));
					}
				} else {
					customLambdaFunctions.add(new CustomLambdaFunction(functionConfigurationList.get(i)));
				}
			}
		}
		return customLambdaFunctions;
	}

	public Map<String, String> getTags() {
		AWSLambda lambdaClient = AWSLambdaClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName())
				.build();
		Map<String, String> tags = lambdaClient.listTags(new ListTagsRequest().withResource(getFunctionArn())).getTags();
		if (tags == null) {
		} else {
			if (tags.isEmpty()) {
				tags.put("No Tags Defined", " ");
			}
		}
		return tags;
	}

	public String getPolicy() {
		AWSLambda lambdaClient = AWSLambdaClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName())
				.build();
		String policy = lambdaClient.getPolicy(new GetPolicyRequest().withFunctionName(getFunctionName())).getPolicy();
		return policy;
	}

	private String getRolePolicyDocument(String roleARN) {
		AmazonIdentityManagement amazonIdentityManagementClient = AmazonIdentityManagementAsyncClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		ListRolePoliciesResult roleResult = amazonIdentityManagementClient.listRolePolicies(new ListRolePoliciesRequest().withRoleName(roleARN));
		return roleResult.getPolicyNames().get(0);
	}

	public String getRolePolicyDocumentText() {
		return CustomIAMRole.getIAMRolePolicyDocument(getRolePolicyDocument(getRole().split("/")[1]), getRole().split("/")[1], getAccount().getAccountAlias());
	}

	public ArrayList<ArrayList<Object>> getLayersData() {
		AWSLambda lambdaClient = AWSLambdaClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName())
				.build();
		List<LayersListItem> layersItemList = new ArrayList<>();
		try {
			String marker = null;
			ListLayersResult llr;
			do {
				llr = lambdaClient.listLayers(new ListLayersRequest().withMarker(marker));
				layersItemList.addAll(llr.getLayers());
				marker = llr.getNextMarker();
			} while (marker != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		ArrayList<ArrayList<Object>> layersData = new ArrayList<ArrayList<Object>>();
		if (getLayers().size() == 0) {
			ArrayList<Object> layerData = new ArrayList<Object>();
			layerData.add("");
			layerData.add("");
			layerData.add("");
			layerData.add("");
			layerData.add("");
			layerData.add("");
			layersData.add(layerData);
		} else {
			for (int i = 0; i < layersItemList.size(); i++) {
				ArrayList<Object> layerData = new ArrayList<Object>();
				if (layersItemList.get(i).getLatestMatchingVersion().getLayerVersionArn().equals(getLayers().get(0).getArn())) {
					layerData.add(layersItemList.get(i).getLayerName());
					layerData.add(layersItemList.get(i).getLatestMatchingVersion().getVersion());
					layerData.add(layersItemList.get(i).getLatestMatchingVersion().getLayerVersionArn());
					layerData.add(layersItemList.get(i).getLatestMatchingVersion().getCreatedDate());
					layerData.add(layersItemList.get(i).getLatestMatchingVersion().getCompatibleRuntimes().stream().map(String::toUpperCase).collect(Collectors.joining(",")));
					layerData.add(layersItemList.get(i).getLayerArn());
					layersData.add(layerData);
				}
			}
		}
		return layersData;
	}

	public ArrayList<ArrayList<Object>> getVPCSubnetsData() {
		ArrayList<ArrayList<Object>> subnetsData = new ArrayList<ArrayList<Object>>();
		List<String> subnetIDs = getVpcConfig().getSubnetIds();
		for (int i = 0; i < subnetIDs.size(); i++) {
			ArrayList<Object> subnetData = new ArrayList<Object>();
			AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName())
					.build();
			Subnet subnet = EC2Common.connectToEC2(account).describeSubnets(new DescribeSubnetsRequest().withSubnetIds(subnetIDs.get(i))).getSubnets().get(0);
			subnetData.add(subnetIDs.get(i));
			subnetData.add(subnet.getAvailabilityZone());
			subnetData.add(subnet.getCidrBlock());
			subnetsData.add(subnetData);
		}
		return subnetsData;
	}

	public ArrayList<ArrayList<Object>> getVPCSecurutyGroupsData() {
		ArrayList<ArrayList<Object>> securutyGroupsData = new ArrayList<ArrayList<Object>>();
		List<String> securityGroupIDs = getVpcConfig().getSecurityGroupIds();
		for (int i = 0; i < securityGroupIDs.size(); i++) {
			ArrayList<Object> securutyGroupData = new ArrayList<Object>();
			AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName())
					.build();
			SecurityGroup securityGroup = ec2Client.describeSecurityGroups(new DescribeSecurityGroupsRequest().withGroupIds(securityGroupIDs.get(i))).getSecurityGroups().get(0);
			//securutyGroupData.add(securityGroupIDs.get(i));
			LinkLikeButton linkLikeIDButton = new LinkLikeButton(securityGroupIDs.get(i));
			linkLikeIDButton.setAccount(account);
			securutyGroupData.add(linkLikeIDButton);
			securutyGroupData.add(securityGroup.getGroupName());
			securutyGroupData.add(securityGroup.getDescription());
			securutyGroupsData.add(securutyGroupData);
		}
		return securutyGroupsData;
	}

	public ArrayList<ArrayList<Object>> getFunctionVariablesData() {
		ArrayList<ArrayList<Object>> variablesData = new ArrayList<ArrayList<Object>>();
		Map<String, String> variables = getEnvironment().getVariables();
		if (variables.size() == 0) {
			ArrayList<Object> variableData = new ArrayList<Object>();
			variableData.add("");
			variableData.add("");
			variablesData.add(variableData);
		} else {
			Iterator<Map.Entry<String, String>> itr = variables.entrySet().iterator();
			while (itr.hasNext()) {
				ArrayList<Object> variableData = new ArrayList<Object>();
				Map.Entry<String, String> entry = itr.next();
				variableData.add(entry.getKey());
				variableData.add(entry.getValue());
				variablesData.add(variableData);
			}
		}
		return variablesData;
	}

	public ArrayList<ArrayList<Object>> getTriggersData() {
		ArrayList<ArrayList<Object>> eventsData = new ArrayList<ArrayList<Object>>();
		AmazonEventBridge amazonEventBridge = AmazonEventBridgeClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		List<String> events = amazonEventBridge.listRuleNamesByTarget(new ListRuleNamesByTargetRequest().withTargetArn(getFunctionArn())).getRuleNames();
		if (events.size() == 0) {
			ArrayList<Object> eventData = new ArrayList<Object>();
			eventData.add("");
			eventData.add("");
			eventData.add("");
			eventData.add("");
			eventData.add("");
			eventData.add("");
			eventData.add("");
			eventData.add("");
			eventsData.add(eventData);
		} else {
			for (int i = 0; i < events.size(); i++) {
				Rule rule = amazonEventBridge.listRules(new ListRulesRequest().withNamePrefix(events.get(0))).getRules().get(i);
				ArrayList<Object> eventData = new ArrayList<Object>();
				eventData.add(rule.getName());
				eventData.add(rule.getDescription());
				eventData.add(rule.getEventBusName());
				eventData.add(rule.getScheduleExpression());
				eventData.add(rule.getState());
				eventData.add(rule.getArn());
				if (rule.getEventPattern() == null) {
					eventData.add("-");
				} else {
					eventData.add(rule.getEventPattern());
				}
				if (rule.getRoleArn() == null) {
					eventData.add("-");
				} else {
					eventData.add(rule.getRoleArn());
				}
				eventsData.add(eventData);
			}
		}
		return eventsData;
	}
}
