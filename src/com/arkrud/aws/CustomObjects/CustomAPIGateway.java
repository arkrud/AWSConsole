package com.arkrud.aws.CustomObjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
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
import com.amazonaws.services.apigateway.model.Resource;
import com.amazonaws.services.apigateway.model.RestApi;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.OverviewPanel;
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

	public CustomAPIGateway() {
		super();
	}
	
	public CustomAPIGateway(RestApi restApi) {
		this.restApi = restApi;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getApiKeySource() {
		// TODO Auto-generated method stub
		return super.getApiKeySource();
	}

	@Override
	public List<String> getBinaryMediaTypes() {
		// TODO Auto-generated method stub
		return super.getBinaryMediaTypes();
	}

	@Override
	public Date getCreatedDate() {
		// TODO Auto-generated method stub
		return super.getCreatedDate();
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return super.getDescription();
	}

	@Override
	public EndpointConfiguration getEndpointConfiguration() {
		// TODO Auto-generated method stub
		return super.getEndpointConfiguration();
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return super.getId();
	}

	@Override
	public Integer getMinimumCompressionSize() {
		// TODO Auto-generated method stub
		return super.getMinimumCompressionSize();
	}

	@Override
	public String getName() {
		return restApi.getName();
	}

	@Override
	public String getPolicy() {
		// TODO Auto-generated method stub
		return super.getPolicy();
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return super.getVersion();
	}

	@Override
	public List<String> getWarnings() {
		// TODO Auto-generated method stub
		return super.getWarnings();
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
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
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveAPIGateways(account, true, appFilter);
	}

	@Override
	public List<Integer> getkeyEvents() {
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
	public String getObjectAWSID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectName() {
		return getName();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		// TODO Auto-generated method stub
		return null;
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
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		// TODO Auto-generated method stub
		return null;
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
	public String getTreeNodeLeafText() {
		return getName();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		// TODO Auto-generated method stub
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		// TODO Auto-generated method stub
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
	}

	
	private ArrayList<CustomAPIGateway> retriveAPIGateways(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomAPIGateway> capig = new ArrayList<CustomAPIGateway>();
		List<RestApi> apiGateWaysList = new ArrayList<>();
		AmazonApiGateway apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName()).build();
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName()).build();
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
	
	
	public static List<RestApi> fetchApiGateways(AWSAccount account) {
		List<RestApi> apiGateWaysList = new ArrayList<>();
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName()).build();
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
			System.out.println(apiGateWaysList.get(i).getId());		}		return apiGateWaysList;
	}
	
	public static List<DomainName> fetchApiCustomDomains(AWSAccount account) {
		List<DomainName> domainNamesList = new ArrayList<>();
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName()).build();
			GetDomainNamesResult  dnr;
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
			System.out.println(domainNamesList.get(i).getRegionalDomainName());
		}
		
		return domainNamesList;
	}
	
	public static List<String> getAPIGatewayIntegration(AWSAccount account, String restApiId) {
		List<String> integrationPropertioes = new ArrayList<>();
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName()).build();
			GetIntegrationResult  dnr;
					dnr = apiGatWayClient.getIntegration(new GetIntegrationRequest().withRestApiId(restApiId).withResourceId("gw81gp").withHttpMethod("POST"));
					integrationPropertioes.add(dnr.getType());
					integrationPropertioes.add(dnr.getUri());
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < integrationPropertioes.size(); i++) {
			System.out.println(integrationPropertioes.get(i));
		}
		return integrationPropertioes;
	}
	
	public static List<Resource> getAPIGatewayResources(AWSAccount account, String restApiId) {
		List<Resource> resources = new ArrayList<>();
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName()).build();
			GetResourcesResult  rsr;
			
			String position = null;
			
			do {
				rsr = apiGatWayClient.getResources(new GetResourcesRequest().withRestApiId(restApiId));
				resources.addAll(rsr.getItems());
				position = rsr.getPosition();
			} while (position != null);
			
			
			
		} catch (Exception e) {
			System.out.println(e);
		}
		//String someString = "elephant";
		//long count = someString.chars().filter(ch -> ch == 'e').count();
		
		for (int i = 0; i < resources.size(); i++) {
			System.out.println(resources.get(i).getPath());
		}
		return resources;
	}
	
}
