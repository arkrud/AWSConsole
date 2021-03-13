package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.DomainName;
import com.amazonaws.services.apigateway.model.EndpointConfiguration;
import com.amazonaws.services.apigateway.model.GetDomainNamesRequest;
import com.amazonaws.services.apigateway.model.GetDomainNamesResult;
import com.amazonaws.services.apigateway.model.GetIntegrationRequest;
import com.amazonaws.services.apigateway.model.GetIntegrationResult;
import com.amazonaws.services.apigateway.model.GetResourcesRequest;
import com.amazonaws.services.apigateway.model.GetResourcesResult;
import com.amazonaws.services.apigateway.model.GetRestApisRequest;
import com.amazonaws.services.apigateway.model.GetRestApisResult;
import com.amazonaws.services.apigateway.model.GetStagesRequest;
import com.amazonaws.services.apigateway.model.GetStagesResult;
import com.amazonaws.services.apigateway.model.Method;
import com.amazonaws.services.apigateway.model.MethodSetting;
import com.amazonaws.services.apigateway.model.Resource;
import com.amazonaws.services.apigateway.model.RestApi;
import com.amazonaws.services.apigateway.model.Stage;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomAPIGateway extends RestApi implements CustomAWSObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RestApi restApi;
	private AWSAccount account;
	private String objectNickName = "APIGateway";
	Stage stageOfInterest;
	Resource resourceOfInterest;
	private String[] apisTableColumnHeaders = { "Name", "Description", "ID", "Protocol", "Endpoint Type", "Cretaed" };
	private String[] stagesTableColumnHeaders = { "Stage Name", "Deployment ID", "Create Date", "Last Update Date", "Settings", "Advanced Settings" };
	private String[] resourcesTableColumnHeaders = { "Resource ID", "Parent ID", "Path", "Name", "Methods" };
	private JLabel[] apiOverviewHeaderLabels = { new JLabel("Name: "), new JLabel("Description: "), new JLabel("ID: "), new JLabel("Protocol: "), new JLabel("Endpoint Type: "), new JLabel("Endpoint ID: "), new JLabel("Cretaed: "),
			new JLabel("API Key Source: "), new JLabel("Binary Media Types: "), new JLabel("Default Endpoint: "), new JLabel("Minimum Compression Size: "), new JLabel("Version: "), new JLabel("Warnings: ") };
	private JLabel[] stageAdvancesSettingsHeaderLabels = { new JLabel("Tracing Enabled: "), new JLabel("Cache Cluster Enabled: "), new JLabel("Cache Cluster Size: "), new JLabel("Cache Cluster Status: "), new JLabel("WAF ACL: "),
			new JLabel("Stage Variables: "), new JLabel("Document Version: "), new JLabel("Access Logging: "), new JLabel("Access Logging Destination Arn: "), new JLabel("Access Logging Format: "), new JLabel("Canary Settings: "),
			new JLabel("Canary Settings Deployment Id: "), new JLabel("Canary Settings Percent Traffic: "), new JLabel("Canary Settings Stage Variable Overrides: "), new JLabel("Canary Settings Use Stage Cache: ") };

	public CustomAPIGateway() {
		super();
	}

	public CustomAPIGateway(RestApi restApi) {
		this.restApi = restApi;
	}

	@Override
	public String getApiKeySource() {
		return restApi.getApiKeySource();
	}

	@Override
	public List<String> getBinaryMediaTypes() {
		return restApi.getBinaryMediaTypes();
	}

	@Override
	public Date getCreatedDate() {
		return restApi.getCreatedDate();
	}

	@Override
	public String getDescription() {
		return restApi.getDescription();
	}

	@Override
	public EndpointConfiguration getEndpointConfiguration() {
		return restApi.getEndpointConfiguration();
	}

	@Override
	public String getId() {
		return restApi.getId();
	}

	@Override
	public Integer getMinimumCompressionSize() {
		return restApi.getMinimumCompressionSize();
	}

	@Override
	public String getName() {
		return restApi.getName();
	}

	@Override
	public String getPolicy() {
		return restApi.getPolicy();
	}

	@Override
	public String getVersion() {
		return restApi.getVersion();
	}

	@Override
	public List<String> getWarnings() {
		return restApi.getWarnings();
	}
	
	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Settings" };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return apisTableColumnHeaders;
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
		return UtilMethodsFactory.populateInterfaceImages().get("apigateway");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("apigateway");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getName());
		summaryData.add(getDescription());
		summaryData.add(getId());
		summaryData.add("REST");
		summaryData.add(getEndpointConfiguration().getTypes().get(0));
		List<String> endpointIdsList = getEndpointConfiguration().getVpcEndpointIds();
		if (endpointIdsList != null) {
			summaryData.add(String.join(",", endpointIdsList));
		} else {
			summaryData.add("-");
		}
		summaryData.add(getCreatedDate());
		String apiKeySource = getApiKeySource();
		if (apiKeySource != null) {
			summaryData.add(apiKeySource);
		} else {
			summaryData.add("-");
		}
		List<String> binaryMediaTypes = getBinaryMediaTypes();
		if (binaryMediaTypes != null) {
			summaryData.add(String.join(",", binaryMediaTypes));
		} else {
			summaryData.add("-");
		}
		if (getDisableExecuteApiEndpoint()) {
			summaryData.add("Disabled");
		} else {
			summaryData.add(" https://" + getId() + ".execute-api.us-east-1.amazonaws.com");
		}
		Integer minimumCompressionSize = getMinimumCompressionSize();
		if (minimumCompressionSize != null) {
			summaryData.add(minimumCompressionSize);
		} else {
			summaryData.add("-");
		}
		String version = getVersion();
		if (version != null) {
			summaryData.add(version);
		} else {
			summaryData.add("-");
		}
		List<String> warnings = getWarnings();
		if (warnings != null) {
			summaryData.add(String.join(",", warnings));
		} else {
			summaryData.add("-");
		}
		return summaryData;
	}

	@Override
	public Boolean getDisableExecuteApiEndpoint() {
		return restApi.getDisableExecuteApiEndpoint();
	}

	@Override
	public Map<String, String> getTags() {
		Map<String, String> tags = restApi.getTags();
		if (tags == null) {
		} else {
			if (tags.isEmpty()) {
				tags.put("No Tags Defined", " ");
			}
		}
		return tags;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getName());
		summaryData.add(getDescription());
		summaryData.add(getId());
		summaryData.add("REST");
		summaryData.add(getEndpointConfiguration().getTypes().get(0));
		summaryData.add(getCreatedDate());
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
		String[] documentPaneIdentifiers = { "ResourcePolicy" };
		String[] documentPaneIPaneHeaders = { "Resource Policy" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneIPaneHeaders);
	}

	@Override
	public String getDocumentTabToolTips(String paneIdentifier) {
		String[] documentPaneIdentifiers = { "ResourcePolicy" };
		String[] documentPaneIPaneHeaders = { "Configure access control to this private API" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneIPaneHeaders);
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveAPIGateways(account, true, appFilter);
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
		String[] listPansIdentifiers = { objectNickName + "Settings" };
		String[] listPaneHeaders = { "Settings" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "Settings" };
		String[] listPaneHeaders = { "API Gateway Settings" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getObjectAWSID() {
		return getId();
	}

	@Override
	public String getObjectName() {
		return getName();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { "APIGatewayResources" } };
		String[][][] columnHeaders0 = { { resourcesTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		String[][] dataFlags1 = { { "APIGatewayStages" } };
		String[][][] columnHeaders1 = { { stagesTableColumnHeaders } };
		map.put(dataFlags1, columnHeaders1);
		String[][] dataFlags2 = { { "Tags" } };
		String[][][] columnHeaders2 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags2, columnHeaders2);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "Settings", "ListInfoPane");
		propertiesPaneTabs.put("ResourcePolicy", "DocumentInfoPane");
		propertiesPaneTabs.put(objectNickName + "Resources", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "Stages", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Settings for " + getName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(apiOverviewHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "APIGatewayResources" }, { "APIGatewayStages" }, { "Tags" } };
		String[][] tablePaneHeaders = { { "Resources" }, { "Stages" }, { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "APIGatewayResources" }, { "APIGatewayStages" }, { "Tags" } };
		String[][] tablePaneHeaders = { { objectNickName + " Resources" }, { objectNickName + " Stages" }, { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTreeNodeLeafText() {
		return getName();
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
		if (paneName.equals("StagesAdvancedSettings")) {
			overviewPanel.getDetailesData(object, object.getAccount(), stageAdvancesSettingsHeaderLabels, paneName);
		} else {
			overviewPanel.getDetailesData(object, object.getAccount(), apiOverviewHeaderLabels, paneName);
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

	private ArrayList<CustomAPIGateway> retriveAPIGateways(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomAPIGateway> capig = new ArrayList<CustomAPIGateway>();
		List<RestApi> apiGateWaysList = new ArrayList<>();
		AmazonApiGateway apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		try {
			String position = null;
			GetRestApisResult rslt;
			do {
				rslt = apiGatWayClient.getRestApis(new GetRestApisRequest().withPosition(position));
				apiGateWaysList.addAll(rslt.getItems());
				position = rslt.getPosition();
			} while (position != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < apiGateWaysList.size(); i++) {
			if (appFilter != null) {
				if (filtered) {
					if (apiGateWaysList.get(i).getName().matches(appFilter)) {
						capig.add(new CustomAPIGateway(apiGateWaysList.get(i)));
					}
				} else {
					capig.add(new CustomAPIGateway(apiGateWaysList.get(i)));
				}
			} else {
				if (filtered) {
					if (apiGateWaysList.get(i).getName().matches(UtilMethodsFactory.getMatchString(account))) {
						capig.add(new CustomAPIGateway(apiGateWaysList.get(i)));
					}
				} else {
					capig.add(new CustomAPIGateway(apiGateWaysList.get(i)));
				}
			}
		}
		return capig;
	}

	public static List<DomainName> fetchApiCustomDomains(AWSAccount account) {
		List<DomainName> domainNamesList = new ArrayList<>();
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
					.withRegion(account.getAccontRegionObject().getName()).build();
			GetDomainNamesResult dnr;
			String position = null;
			do {
				dnr = apiGatWayClient.getDomainNames(new GetDomainNamesRequest().withPosition(position));
				domainNamesList.addAll(dnr.getItems());
				position = dnr.getPosition();
			} while (position != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		//domainNamesList.get(0).get
		
		return domainNamesList;
	}

	public static List<String> getAPIGatewayIntegration(AWSAccount account, String restApiId) {
		List<String> integrationPropertioes = new ArrayList<>();
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
					.withRegion(account.getAccontRegionObject().getName()).build();
			GetIntegrationResult dnr;
			dnr = apiGatWayClient.getIntegration(new GetIntegrationRequest().withRestApiId(restApiId).withResourceId("gw81gp").withHttpMethod("POST"));
			integrationPropertioes.add(dnr.getType());
			integrationPropertioes.add(dnr.getUri());
		} catch (Exception e) {
			System.out.println(e);
		}
		return integrationPropertioes;
	}

	public ArrayList<ArrayList<Object>> getAPIResources() {
		ArrayList<ArrayList<Object>> apiResourcesData = new ArrayList<ArrayList<Object>>();
		List<Resource> resources = new ArrayList<>();
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
					.withRegion(account.getAccontRegionObject().getName()).build();
			GetResourcesResult rsr;
			String position = null;
			do {
				rsr = apiGatWayClient.getResources(new GetResourcesRequest().withRestApiId(getId()));
				resources.addAll(rsr.getItems());
				position = rsr.getPosition();
			} while (position != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		int i = 0;
		while (i < resources.size()) {
			ArrayList<Object> apiResourceData = new ArrayList<Object>();
			apiResourceData.add(resources.get(i).getId());
			if (resources.get(i).getParentId() == null) {
				apiResourceData.add("-");
			} else {
				apiResourceData.add(resources.get(i).getParentId());
			}
			if (resources.get(i).getPath() == null) {
				apiResourceData.add("-");
			} else {
				apiResourceData.add(resources.get(i).getPath());
			}
			if (resources.get(i).getPathPart() == null) {
				apiResourceData.add("-");
			} else {
				apiResourceData.add(resources.get(i).getPathPart());
			}
			Map<String, Method> methods = resources.get(i).getResourceMethods();
			Iterator<Map.Entry<String, Method>> itr = null;
			String methodsString = "";
			if (methods != null) {
				itr = methods.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry<String, Method> entry = itr.next();
					methodsString = methodsString + entry.getKey();
				}
			} else {
				methodsString = " ";
			}
			// methodsString = methodsString.substring(0, methodsString.length() - 1);
			apiResourceData.add("Methods And Integartions");
			// apiResourceData.add("Methods");
			apiResourcesData.add(apiResourceData);
			i++;
		}
		return apiResourcesData;
	}

	public Resource getAPIResource(String apiID, String resourceId) {
		AmazonApiGateway apiGatWayClient;
		List<Resource> resources = new ArrayList<>();
		Resource resource = null;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
					.withRegion(account.getAccontRegionObject().getName()).build();
			GetResourcesResult rsr;
			String position = null;
			do {
				rsr = apiGatWayClient.getResources(new GetResourcesRequest().withRestApiId(getId()));
				resources.addAll(rsr.getItems());
				position = rsr.getPosition();
			} while (position != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		int i = 0;
		while (i < resources.size()) {
			if (resources.get(i).getId().equals(resourceId)) {
				resource = resources.get(i);
			}
			i++;
		}
		return resource;
	}

	public ArrayList<ArrayList<Object>> getAPIStages() {
		ArrayList<ArrayList<Object>> apiStagesData = new ArrayList<ArrayList<Object>>();
		List<Stage> stages = new ArrayList<>();
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
					.withRegion(account.getAccontRegionObject().getName()).build();
			GetStagesResult sr;
			sr = apiGatWayClient.getStages(new GetStagesRequest().withRestApiId(getId()));
			stages.addAll(sr.getItem());
		} catch (Exception e) {
			System.out.println(e);
		}
		int i = 0;
		while (i < stages.size()) {
			ArrayList<Object> apiResourceData = new ArrayList<Object>();
			apiResourceData.add(stages.get(i).getStageName());
			apiResourceData.add(stages.get(i).getDeploymentId());
			apiResourceData.add(stages.get(i).getCreatedDate());
			apiResourceData.add(stages.get(i).getLastUpdatedDate());
			apiResourceData.add("Method Settings");
			apiResourceData.add("Advanced Settings");
			apiStagesData.add(apiResourceData);
			i++;
		}
		return apiStagesData;
	}

	public ArrayList<Object> getStagesAdvancedSettingsData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(stageOfInterest.getTracingEnabled());
		summaryData.add(stageOfInterest.getCacheClusterEnabled());
		if (stageOfInterest.getCacheClusterEnabled()) {
			summaryData.add(stageOfInterest.getCacheClusterSize());
			summaryData.add(stageOfInterest.getCacheClusterStatus());
		} else {
			summaryData.add(" - ");
			summaryData.add(" - ");
		}
		if (stageOfInterest.getWebAclArn() != null) {
			summaryData.add(stageOfInterest.getWebAclArn());
		} else {
			summaryData.add("WAF is Not Used");
		}
		if (stageOfInterest.getVariables() != null) {
			String variables = "";
			for (Map.Entry<String, String> entry : stageOfInterest.getVariables().entrySet())
				variables = variables + "Variable = " + entry.getKey() + ", Value = " + entry.getValue() + "\\n";
			summaryData.add(stageOfInterest.getVariables());
		} else {
			summaryData.add("Variable Undefined");
		}
		if (stageOfInterest.getDocumentationVersion() != null) {
			summaryData.add(stageOfInterest.getDocumentationVersion());
		} else {
			summaryData.add(" - ");
		}
		if (stageOfInterest.getAccessLogSettings() != null) {
			summaryData.add("Access Logging Enabled");
			summaryData.add(stageOfInterest.getAccessLogSettings().getDestinationArn());
			summaryData.add(stageOfInterest.getAccessLogSettings().getFormat());
		} else {
			summaryData.add("Access Logging Disabled");
			summaryData.add(" - ");
			summaryData.add(" - ");
		}
		if (stageOfInterest.getCanarySettings() != null) {
			summaryData.add("Enabled");
			summaryData.add(stageOfInterest.getCanarySettings().getDeploymentId());
			summaryData.add(stageOfInterest.getCanarySettings().getPercentTraffic());
			summaryData.add(stageOfInterest.getCanarySettings().getStageVariableOverrides());
			summaryData.add(stageOfInterest.getCanarySettings().getUseStageCache());
		} else {
			summaryData.add("Disabled");
			summaryData.add(" - ");
			summaryData.add(" - ");
			summaryData.add(" - ");
			summaryData.add(" - ");
		}
		return summaryData;
	}

	public Stage getAPIStage(String apiID, String stageName) {
		List<Stage> stages = new ArrayList<>();
		Stage stage = null;
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
					.withRegion(account.getAccontRegionObject().getName()).build();
			GetStagesResult sr;
			sr = apiGatWayClient.getStages(new GetStagesRequest().withRestApiId(getId()));
			stages.addAll(sr.getItem());
		} catch (Exception e) {
			System.out.println(e);
		}
		int i = 0;
		while (i < stages.size()) {
			if (stages.get(i).getStageName().equals(stageName)) {
				stage = stages.get(i);
			}
			i++;
		}
		return stage;
	}

	public ArrayList<ArrayList<Object>> getStageMethodsData() {
		ArrayList<ArrayList<Object>> stageMethodsData = new ArrayList<ArrayList<Object>>();
		Map<String, MethodSetting> methodSetting = new HashMap<String, MethodSetting>();
		methodSetting = stageOfInterest.getMethodSettings();
		Iterator<Map.Entry<String, MethodSetting>> itr = methodSetting.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, MethodSetting> entry = itr.next();
			ArrayList<Object> stageMethodData = new ArrayList<Object>();
			stageMethodData.add(entry.getKey().replaceAll("~1", "/"));
			stageMethodData.add(entry.getValue().getMetricsEnabled());
			stageMethodData.add(entry.getValue().getLoggingLevel());
			stageMethodData.add(entry.getValue().getThrottlingBurstLimit());
			stageMethodData.add(entry.getValue().getThrottlingRateLimit());
			stageMethodData.add(entry.getValue().getCachingEnabled());
			stageMethodData.add(entry.getValue().getCacheTtlInSeconds());
			stageMethodData.add(entry.getValue().getCacheDataEncrypted());
			stageMethodData.add(entry.getValue().getRequireAuthorizationForCacheControl());
			stageMethodData.add(entry.getValue().getUnauthorizedCacheControlHeaderStrategy());
			stageMethodsData.add(stageMethodData);
		}
		return stageMethodsData;
	}

	public ArrayList<ArrayList<Object>> getResourceMethodsData() {
		ArrayList<ArrayList<Object>> resourceMethodsData = new ArrayList<ArrayList<Object>>();
		Map<String, Method> methods = new HashMap<String, Method>();
		List<Resource> resources = new ArrayList<>();
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
					.withRegion(account.getAccontRegionObject().getName()).build();
			GetResourcesResult rsr;
			String position = null;
			do {
				rsr = apiGatWayClient.getResources(new GetResourcesRequest().withRestApiId(getId()));
				resources.addAll(rsr.getItems());
				position = rsr.getPosition();
			} while (position != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		int i = 0;
		while (i < resources.size()) {
			if (resources.get(i).getId().equals(resourceOfInterest.getId())) {
				methods = resources.get(i).getResourceMethods();
				}
			i++;
		}
		if (methods == null) {
			System.out.println("Resource has no methods");
		} else {
			Iterator<Map.Entry<String, Method>> itr = methods.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, Method> entry = itr.next();
				ArrayList<Object> methodRuleData = new ArrayList<Object>();
				methodRuleData.add(entry.getKey());
				try {
					apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
							.withRegion(account.getAccontRegionObject().getName()).build();
					GetIntegrationResult dnr;
					dnr = apiGatWayClient.getIntegration(new GetIntegrationRequest().withRestApiId(getId()).withResourceId(resourceOfInterest.getId()).withHttpMethod(entry.getKey()));
					methodRuleData.add(dnr.getType());
					methodRuleData.add(dnr.getUri().split(":")[11].split("/")[0]);
				} catch (Exception e) {
					System.out.println(e);
				}
				resourceMethodsData.add(methodRuleData);
			}
		}
		return resourceMethodsData;
	}

	public Stage getStageOfInterest() {
		return stageOfInterest;
	}

	public void setStageOfInterest(Stage stageOfInterest) {
		this.stageOfInterest = stageOfInterest;
	}

	public Resource getResourceOfInterest() {
		return resourceOfInterest;
	}

	public void setResourceOfInterest(Resource resourceOfInterest) {
		this.resourceOfInterest = resourceOfInterest;
	}
}
