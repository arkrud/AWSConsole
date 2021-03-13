package com.arkrud.aws.CustomObjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterHistoryRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterHistoryResult;
import com.amazonaws.services.simplesystemsmanagement.model.ListTagsForResourceRequest;
import com.amazonaws.services.simplesystemsmanagement.model.ListTagsForResourceResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomSMParameter extends Parameter implements CustomAWSObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Parameter parameter;
	private AWSAccount account;
	private String objectNickName = "Parameter";
	private String[] parametersTableColumnHeaders = { "Name", "Type", "Value", "Data Type", "Last Modified" };
	private JLabel[] apiOverviewHeaderLabels = { new JLabel("Name: "), new JLabel("Type: "), new JLabel("Value: "), new JLabel("Data Type: "), new JLabel("Last Modifie: "), new JLabel("Tier: "), new JLabel("Version: "), new JLabel("Description: ") };

	public CustomSMParameter() {
		super();
	}

	public CustomSMParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public String getARN() {
		return parameter.getARN();
	}

	@Override
	public String getDataType() {
		return parameter.getDataType();
	}

	@Override
	public Date getLastModifiedDate() {
		return parameter.getLastModifiedDate();
	}

	@Override
	public String getName() {
		return parameter.getName();
	}

	@Override
	public String getSelector() {
		return parameter.getSelector();
	}

	@Override
	public String getSourceResult() {
		return parameter.getSourceResult();
	}

	@Override
	public String getType() {
		return parameter.getType();
	}

	@Override
	public String getValue() {
		return parameter.getValue();
	}

	@Override
	public Long getVersion() {
		return parameter.getVersion();
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Detailes" };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return parametersTableColumnHeaders;
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
		return UtilMethodsFactory.populateInterfaceImages().get("parameters");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("parameter");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getName());
		summaryData.add(getType());
		summaryData.add(getValue());
		summaryData.add(getDataType());
		summaryData.add(getLastModifiedDate());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		ArrayList<ArrayList<Object>> tagsData = new ArrayList<ArrayList<Object>>();
		AWSSimpleSystemsManagement ssmc = AWSSimpleSystemsManagementClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		ListTagsForResourceResult result =  ssmc.listTagsForResource(new ListTagsForResourceRequest().withResourceType("Parameter").withResourceId(getName()));
		for (int i = 0; i < result.getTagList().size(); i++) {
			ArrayList<Object> tagData = new ArrayList<Object>();
			tagData.add(result.getTagList().get(i).getKey());
			tagData.add(result.getTagList().get(i).getValue());
			tagsData.add(tagData);
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
		// TODO Auto-generated method stub
		return null;
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
	
	public ArrayList<ArrayList<Object>> getParameterHistoryData() {
		ArrayList<ArrayList<Object>> parameterHistoryData = new ArrayList<ArrayList<Object>>();
		AWSSimpleSystemsManagement ssmc = AWSSimpleSystemsManagementClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		GetParameterHistoryResult result =  ssmc.getParameterHistory(new GetParameterHistoryRequest().withName(getName()));
		for (int i = 0; i < result.getParameters().size(); i++) {
			ArrayList<Object> historyData = new ArrayList<Object>();
			historyData.add(result.getParameters().get(i).getVersion());
			historyData.add(result.getParameters().get(i).getValue());
			historyData.add(result.getParameters().get(i).getTier());
			historyData.add(result.getParameters().get(i).getLastModifiedDate());
			historyData.add(result.getParameters().get(i).getLastModifiedUser());
			//historyData.add(result.getParameters().get(i).getKeyId());
			//historyData.add(result.getParameters().get(i).getLabels());
			parameterHistoryData.add(historyData);
		}
		return parameterHistoryData;
	}
	
	private ArrayList<Parameter> retriveParameters(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		AWSSimpleSystemsManagement ssmc = AWSSimpleSystemsManagementClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		return parameters;
	}
	
}
