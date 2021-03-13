package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
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
import com.amazonaws.services.apigateway.model.BasePathMapping;
import com.amazonaws.services.apigateway.model.DomainName;
import com.amazonaws.services.apigateway.model.EndpointConfiguration;
import com.amazonaws.services.apigateway.model.GetBasePathMappingsRequest;
import com.amazonaws.services.apigateway.model.GetBasePathMappingsResult;
import com.amazonaws.services.apigateway.model.GetDomainNamesRequest;
import com.amazonaws.services.apigateway.model.GetDomainNamesResult;
import com.amazonaws.services.apigateway.model.GetRestApisRequest;
import com.amazonaws.services.apigateway.model.GetRestApisResult;
import com.amazonaws.services.apigateway.model.MutualTlsAuthentication;
import com.amazonaws.services.apigateway.model.RestApi;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomDomainName extends DomainName implements CustomAWSObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String objectNickName = "CustomDomainName";
	private DomainName domainName;
	private AWSAccount account;
	private String[] cdnTableColumnHeaders = { "Domain Name", "TLS Version", "Type", "Hosted Zone", "API Rateway Domain Name", "ACM Certificate ARN", "Status" };
	private String[] mappingsTableColumnHeaders = { "API", "Stage", "Path", "Default Endpoint Enabled" };
	private JLabel[] cdnOverviewHeaderLabels = { new JLabel("Domain Name: "), new JLabel("TLS Version: "), new JLabel("Type: "), new JLabel("Hosted Zone: "), new JLabel("API Rateway Domain Nam: "), new JLabel("ACM Certificate ARN: "),
			new JLabel("Status: ") };

	public CustomDomainName() {
		super();
	}

	public CustomDomainName(DomainName domainName) {
		this.domainName = domainName;
	}

	@Override
	public String getCertificateArn() {
		return domainName.getCertificateArn();
	}

	@Override
	public String getCertificateName() {
		return domainName.getCertificateName();
	}

	@Override
	public Date getCertificateUploadDate() {
		return domainName.getCertificateUploadDate();
	}

	@Override
	public String getDistributionDomainName() {
		return domainName.getDistributionDomainName();
	}

	@Override
	public String getDistributionHostedZoneId() {
		return domainName.getDistributionHostedZoneId();
	}

	@Override
	public String getDomainName() {
		return domainName.getDomainName();
	}

	@Override
	public String getDomainNameStatus() {
		return domainName.getDomainNameStatus();
	}

	@Override
	public String getDomainNameStatusMessage() {
		return domainName.getDomainNameStatusMessage();
	}

	@Override
	public EndpointConfiguration getEndpointConfiguration() {
		return domainName.getEndpointConfiguration();
	}

	@Override
	public MutualTlsAuthentication getMutualTlsAuthentication() {
		return domainName.getMutualTlsAuthentication();
	}

	@Override
	public String getRegionalCertificateArn() {
		return domainName.getRegionalCertificateArn();
	}

	@Override
	public String getRegionalCertificateName() {
		return domainName.getRegionalCertificateName();
	}

	@Override
	public String getRegionalDomainName() {
		return domainName.getRegionalDomainName();
	}

	@Override
	public String getRegionalHostedZoneId() {
		return domainName.getRegionalHostedZoneId();
	}

	@Override
	public String getSecurityPolicy() {
		return domainName.getSecurityPolicy();
	}

	@Override
	public Map<String, String> getTags() {
		return domainName.getTags();
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Settings" };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return cdnTableColumnHeaders;
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
		return UtilMethodsFactory.populateInterfaceImages().get("CustomDomains");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("CustomDomain");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getDomainName());
		summaryData.add(getSecurityPolicy());
		summaryData.add(getEndpointConfiguration().getTypes().get(0));
		summaryData.add(getRegionalHostedZoneId());
		summaryData.add(getRegionalDomainName());
		summaryData.add(getRegionalCertificateArn());
		summaryData.add(getDomainNameStatus());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getDomainName());
		summaryData.add(getSecurityPolicy());
		summaryData.add(getEndpointConfiguration().getTypes().get(0));
		summaryData.add(getRegionalHostedZoneId());
		summaryData.add(getRegionalDomainName());
		summaryData.add(getRegionalCertificateArn());
		summaryData.add(getDomainNameStatus());
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
		return retriveCustomDomainNames(account, true, appFilter);
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		events.add(KeyEvent.VK_2);
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
		String[] listPaneHeaders = { "Custom Doamin Name Settings" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getObjectAWSID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectName() {
		return getDomainName();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { "CustomDomainNameMappings" } };
		String[][][] columnHeaders0 = { { mappingsTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		String[][] dataFlags1 = { { "Tags" } };
		String[][][] columnHeaders1 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags1, columnHeaders1);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "Settings", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "Mappings", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Settings for " + getDomainName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(cdnOverviewHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "CustomDomainNameMappings" }, { "Tags" } };
		String[][] tablePaneHeaders = { { "Mappings" }, { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "CustomDomainNameMappings" }, { "Tags" } };
		String[][] tablePaneHeaders = { { objectNickName + "Mappings" }, { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTreeNodeLeafText() {
		return getDomainName();
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
		overviewPanel.getDetailesData(object, object.getAccount(), cdnOverviewHeaderLabels, paneName);
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

	private static ArrayList<CustomDomainName> retriveCustomDomainNames(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomDomainName> domainNames = new ArrayList<CustomDomainName>();
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
		for (int i = 0; i < domainNamesList.size(); i++) {
			if (appFilter != null) {
				if (filtered) {
					if (domainNamesList.get(i).getDomainName().matches(appFilter)) {
						domainNames.add(new CustomDomainName(domainNamesList.get(i)));
					}
				} else {
					domainNames.add(new CustomDomainName(domainNamesList.get(i)));
				}
			} else {
				if (filtered) {
					if (domainNamesList.get(i).getDomainName().matches(UtilMethodsFactory.getMatchString(account))) {
						domainNames.add(new CustomDomainName(domainNamesList.get(i)));
					}
				} else {
					domainNames.add(new CustomDomainName(domainNamesList.get(i)));
				}
			}
		}
		return domainNames;
	}

	public ArrayList<ArrayList<Object>> retriveCustomDomainNameAPIMappings() {
		ArrayList<ArrayList<Object>> domainNameMappings = new ArrayList<ArrayList<Object>>();
		List<BasePathMapping> domainNameMappingsList = new ArrayList<>();
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(getAccount().getAccountKey(), getAccount().getAccountSecret())))
					.withRegion(account.getAccontRegionObject().getName()).build();
			GetBasePathMappingsResult dnr;
			String position = null;
			do {
				dnr = apiGatWayClient.getBasePathMappings(new GetBasePathMappingsRequest().withDomainName(getDomainName()));
				domainNameMappingsList.addAll(dnr.getItems());
				position = dnr.getPosition();
			} while (position != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < domainNameMappingsList.size(); i++) {
			ArrayList<Object> domainNameMapping = new ArrayList<Object>();
			String apiID = domainNameMappingsList.get(i).getRestApiId();
			RestApi apiGateway = getAPIgateway(getAccount(), apiID);
			domainNameMapping.add(apiGateway.getName());
			domainNameMapping.add(domainNameMappingsList.get(i).getStage());
			domainNameMapping.add(domainNameMappingsList.get(i).getBasePath());
			domainNameMapping.add(!apiGateway.getDisableExecuteApiEndpoint());
			domainNameMappings.add(domainNameMapping);
		}
		return domainNameMappings;
	}

	private RestApi getAPIgateway(AWSAccount account, String id) {
		RestApi apiGateway = new RestApi();
		AmazonApiGateway apiGatWayClient;
		List<RestApi> apiGateWaysList = new ArrayList<>();
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
					.withRegion(account.getAccontRegionObject().getName()).build();
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
			if (apiGateWaysList.get(i).getId().equals(id)) {
				apiGateway = apiGateWaysList.get(i);
			}
		}
		return apiGateway;
	}
}
