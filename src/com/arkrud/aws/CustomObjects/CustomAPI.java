package com.arkrud.aws.CustomObjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.apigatewayv2.AmazonApiGatewayV2;
import com.amazonaws.services.apigatewayv2.AmazonApiGatewayV2ClientBuilder;
import com.amazonaws.services.apigatewayv2.model.Api;
import com.amazonaws.services.apigatewayv2.model.Cors;
import com.amazonaws.services.apigatewayv2.model.GetApisRequest;
import com.amazonaws.services.apigatewayv2.model.GetApisResult;

public class CustomAPI extends Api implements CustomAWSObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Api api;
	private AWSAccount account;
	private String objectNickName = "API Gateway";
	private String[] tgTableColumnHeaders = { "Name", "Description", "ID", "Protocol", "Endpoint Type", "Cretaed" };

	public CustomAPI() {
		super();
	}

	@Override
	public String getApiEndpoint() {
		return api.getApiEndpoint();
	}

	@Override
	public Boolean getApiGatewayManaged() {
		return api.getApiGatewayManaged();
	}

	@Override
	public String getApiId() {
		return api.getApiId();
	}

	@Override
	public String getApiKeySelectionExpression() {
		return api.getApiKeySelectionExpression();
	}

	@Override
	public Cors getCorsConfiguration() {
		return api.getCorsConfiguration();
	}

	@Override
	public Date getCreatedDate() {
		return api.getCreatedDate();
	}

	@Override
	public String getDescription() {
		return api.getDescription();
	}

	@Override
	public Boolean getDisableExecuteApiEndpoint() {
		return api.getDisableExecuteApiEndpoint();
	}

	@Override
	public Boolean getDisableSchemaValidation() {
		return api.getDisableSchemaValidation();
	}

	@Override
	public List<String> getImportInfo() {
		return api.getImportInfo();
	}

	@Override
	public String getName() {
		return api.getName();
	}

	@Override
	public String getProtocolType() {
		return api.getProtocolType();
	}

	@Override
	public String getRouteSelectionExpression() {
		return api.getRouteSelectionExpression();
	}

	@Override
	public Map<String, String> getTags() {
		return api.getTags();
	}

	@Override
	public String getVersion() {
		return api.getVersion();
	}

	@Override
	public List<String> getWarnings() {
		return api.getWarnings();
	}

	@Override
	public Boolean isApiGatewayManaged() {
		return api.isApiGatewayManaged();
	}

	@Override
	public Boolean isDisableExecuteApiEndpoint() {
		return api.isDisableExecuteApiEndpoint();
	}

	@Override
	public Boolean isDisableSchemaValidation() {
		return api.isDisableSchemaValidation();
	}

	public CustomAPI(Api api) {
		this.api = api;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties" };
		return null;
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
		summaryData.add(getApiId());
		summaryData.add(getProtocolType());
		summaryData.add(getProtocolType());
		summaryData.add(getCreatedDate());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getName());
		summaryData.add(getDescription());
		summaryData.add(getApiId());
		summaryData.add(getProtocolType());
		summaryData.add(getProtocolType());
		summaryData.add(getCreatedDate());
		return summaryData;
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
		return retriveAPIs(account, true, appFilter);
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		// TODO Auto-generated method stub
	}

	private ArrayList<CustomAPI> retriveAPIs(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomAPI> capig = new ArrayList<CustomAPI>();
		List<Api> apiGateWaysList = new ArrayList<>();
		AmazonApiGatewayV2 apiGatWayClient = AmazonApiGatewayV2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		try {
			String nextToken = null;
			GetApisResult rslt;
			do {
				rslt = apiGatWayClient.getApis(new GetApisRequest().withNextToken(nextToken));
				apiGateWaysList.addAll(rslt.getItems());
				nextToken = rslt.getNextToken();
			} while (nextToken != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < apiGateWaysList.size(); i++) {
			if (appFilter != null) {
				if (filtered) {
					if (apiGateWaysList.get(i).getName().matches(appFilter)) {
						capig.add(new CustomAPI(apiGateWaysList.get(i)));
					}
				} else {
					capig.add(new CustomAPI(apiGateWaysList.get(i)));
				}
			} else {
				if (filtered) {
					if (apiGateWaysList.get(i).getName().matches(UtilMethodsFactory.getMatchString(account))) {
						capig.add(new CustomAPI(apiGateWaysList.get(i)));
					}
				} else {
					capig.add(new CustomAPI(apiGateWaysList.get(i)));
				}
			}
		}
		return capig;
	}
}
