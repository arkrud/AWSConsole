package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.DescribeParametersRequest;
import com.amazonaws.services.simplesystemsmanagement.model.DescribeParametersResult;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterHistoryRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterHistoryResult;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.ListTagsForResourceRequest;
import com.amazonaws.services.simplesystemsmanagement.model.ListTagsForResourceResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import com.amazonaws.services.simplesystemsmanagement.model.ParameterInlinePolicy;
import com.amazonaws.services.simplesystemsmanagement.model.ParameterMetadata;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomParameterMetadata extends ParameterMetadata implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private ParameterMetadata parameterMetadata;
	private AWSAccount account;
	private String objectNickName = "Parameter";
	private String[] parametersTableColumnHeaders = { "Name", "Type", "Value", "Data Type", "Last Modified" };
	private JLabel[] parametersOverviewHeaderLabels = { new JLabel("Name: "), new JLabel("Type: "), new JLabel("Value: "), new JLabel("Data Type: "), new JLabel("Last Modifie: "), new JLabel("Tier: "), new JLabel("Version: "),
			new JLabel("Description: ") };
	private String[] historyTableColumnHeaders = { "Version", "Value", "Tier", "Last modified date", "Last modified user" };

	public CustomParameterMetadata() {
		super();
	}

	public CustomParameterMetadata(ParameterMetadata parameterMetadata) {
		this.parameterMetadata = parameterMetadata;
	}

	@Override
	public String getAllowedPattern() {
		return parameterMetadata.getAllowedPattern();
	}

	@Override
	public String getDataType() {
		return parameterMetadata.getDataType();
	}

	@Override
	public String getDescription() {
		return parameterMetadata.getDescription();
	}

	@Override
	public String getKeyId() {
		return parameterMetadata.getKeyId();
	}

	@Override
	public Date getLastModifiedDate() {
		return parameterMetadata.getLastModifiedDate();
	}

	@Override
	public String getLastModifiedUser() {
		return parameterMetadata.getLastModifiedUser();
	}

	@Override
	public String getName() {
		return parameterMetadata.getName();
	}

	@Override
	public List<ParameterInlinePolicy> getPolicies() {
		return parameterMetadata.getPolicies();
	}

	@Override
	public String getTier() {
		return parameterMetadata.getTier();
	}

	@Override
	public String getType() {
		return parameterMetadata.getType();
	}

	@Override
	public Long getVersion() {
		return parameterMetadata.getVersion();
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
		String[] menus = { objectNickName + " Detailes" };
		return menus;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("parameterstore");
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
		summaryData.add(retriveParameter(getName()).getValue());
		summaryData.add(getDataType());
		summaryData.add(getLastModifiedDate());
		summaryData.add(getTier());
		summaryData.add(getVersion());
		summaryData.add(getDescription());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getName());
		summaryData.add(getType());
		summaryData.add(retriveParameter(getName()).getValue());
		summaryData.add(getDataType());
		summaryData.add(getLastModifiedDate());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		ArrayList<ArrayList<Object>> tagsData = new ArrayList<ArrayList<Object>>();
		AWSSimpleSystemsManagement ssmc = AWSSimpleSystemsManagementClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		ListTagsForResourceResult result = ssmc.listTagsForResource(new ListTagsForResourceRequest().withResourceType("Parameter").withResourceId(getName()));
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
		return retriveParameters(account, true, appFilter);
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
	public String getObjectAWSID() {
		return getName();
	}

	@Override
	public String getObjectName() {
		return getName();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { "ParameterHistory" } };
		String[][][] columnHeaders0 = { { historyTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		String[][] dataFlags1 = { { "Tags" } };
		String[][][] columnHeaders1 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags1, columnHeaders1);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "Detailes", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "History", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Detailes for " + getName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(parametersOverviewHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "ParameterHistory" }, { "Tags" } };
		String[][] tablePaneHeaders = { { "History" }, { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "ParameterHistory" }, { "Tags" } };
		String[][] tablePaneHeaders = { { objectNickName + " History" }, { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTreeNodeLeafText() {
		return getName();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(objectNickName + " Detailes")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
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
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		overviewPanel.getDetailesData(object, object.getAccount(), parametersOverviewHeaderLabels, paneName);
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

	public ArrayList<ArrayList<Object>> getParameterHistoryData() {
		ArrayList<ArrayList<Object>> parameterHistoryData = new ArrayList<ArrayList<Object>>();
		AWSSimpleSystemsManagement ssmc = AWSSimpleSystemsManagementClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		GetParameterHistoryResult result = ssmc.getParameterHistory(new GetParameterHistoryRequest().withName(getName()));
		for (int i = 0; i < result.getParameters().size(); i++) {
			ArrayList<Object> historyData = new ArrayList<Object>();
			historyData.add(result.getParameters().get(i).getVersion());
			historyData.add(result.getParameters().get(i).getValue());
			historyData.add(result.getParameters().get(i).getTier());
			historyData.add(result.getParameters().get(i).getLastModifiedDate());
			historyData.add(result.getParameters().get(i).getLastModifiedUser());
			parameterHistoryData.add(historyData);
		}
		return parameterHistoryData;
	}

	private ArrayList<CustomParameterMetadata> retriveParameters(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomParameterMetadata> cpm = new ArrayList<CustomParameterMetadata>();
		AWSSimpleSystemsManagement ssmc = AWSSimpleSystemsManagementClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		List<ParameterMetadata> parametersMetadata = new ArrayList<ParameterMetadata>();
		try {
			String nextToken = null;
			DescribeParametersResult result;
			do {
				result = ssmc.describeParameters(new DescribeParametersRequest().withNextToken(nextToken));
				parametersMetadata.addAll(result.getParameters());
				nextToken = result.getNextToken();
			} while (nextToken != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < parametersMetadata.size(); i++) {
			if (appFilter != null) {
				if (filtered) {
					if (parametersMetadata.get(i).getName().matches(appFilter)) {
						cpm.add(new CustomParameterMetadata(parametersMetadata.get(i)));
					}
				} else {
					cpm.add(new CustomParameterMetadata(parametersMetadata.get(i)));
				}
			} else {
				if (filtered) {
					if (parametersMetadata.get(i).getName().matches(UtilMethodsFactory.getMatchString(account))) {
						cpm.add(new CustomParameterMetadata(parametersMetadata.get(i)));
					}
				} else {
					cpm.add(new CustomParameterMetadata(parametersMetadata.get(i)));
				}
			}
		}
		return cpm;
	}

	private Parameter retriveParameter(String name) {
		AWSSimpleSystemsManagement ssmc = AWSSimpleSystemsManagementClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		return ssmc.getParameter(new GetParameterRequest().withName(name)).getParameter();
	}
}
