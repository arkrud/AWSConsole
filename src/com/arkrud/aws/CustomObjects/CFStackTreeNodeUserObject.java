package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.DeleteStackRequest;
import com.amazonaws.services.cloudformation.model.DescribeStackEventsRequest;
import com.amazonaws.services.cloudformation.model.DescribeStackResourcesRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.GetTemplateRequest;
import com.amazonaws.services.cloudformation.model.Output;
import com.amazonaws.services.cloudformation.model.Parameter;
import com.amazonaws.services.cloudformation.model.RollbackConfiguration;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackEvent;
import com.amazonaws.services.cloudformation.model.StackResource;
import com.amazonaws.services.cloudformation.model.Tag;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CFStackTreeNodeUserObject extends Stack implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private Stack stack;
	private AWSAccount account;
	private String objectNickName = "Stack";
	private String action = "Delete";
	public static String[] cloudFormationTableColumnHeaders = { "Stack Name", "Created Time", "Updated Time", "Status", "Description" };
	private JLabel[] cfStackOverviewHeaderLabels = { new JLabel("Stack Name:"), new JLabel("Stack ID:"), new JLabel("Status:"), new JLabel("Status reason:"), new JLabel("Description") };
	private static String[] stackResourcesTableColumnHeaders = { "Type", "Logical ID", "Physical ID", "Description", "Status", "Status Reason" };
	private static String[] stackOutputsTableColumnHeaders = { "Key", "Value", "Description" };
	private static String[] stackEventsTableColumnHeaders = { "Time", "Status", "Type", "Logical ID", "Physical ID", "Status reason" };

	public CFStackTreeNodeUserObject() {
		super();
	}

	public CFStackTreeNodeUserObject(Stack stack) {
		this.stack = stack;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return cloudFormationTableColumnHeaders;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName + "(s)" };
		return menus;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName + "(s)" };
		return menus;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("cf_stacks");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("cf_stack");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getStackName());
		summaryData.add(getStackId());
		summaryData.add(getStackStatus());
		summaryData.add(getStackStatusReason());
		summaryData.add(getDescription());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		SimpleDateFormat form = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
		summaryData.add(form.format(getCreationTime()));
		if (getLastUpdatedTime() == null) {
			summaryData.add("");
		} else {
			summaryData.add(form.format(getLastUpdatedTime()));
		}
		summaryData.add(getStackStatus());
		summaryData.add(getDescription());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		ArrayList<ArrayList<Object>> tagsData = new ArrayList<ArrayList<Object>>();
		Iterator<Tag> tagsIterator = getTags().iterator();
		while (tagsIterator.hasNext()) {
			Tag tag = tagsIterator.next();
			ArrayList<Object> tagData = new ArrayList<Object>();
			tagData.add(tag.getKey());
			tagData.add(tag.getValue());
			tagsData.add(tagData);
		}
		return tagsData;
	}

	@Override
	public List<String> getCapabilities() {
		return stack.getCapabilities();
	}

	@Override
	public String getChangeSetId() {
		return stack.getChangeSetId();
	}

	@Override
	public Date getCreationTime() {
		return stack.getCreationTime();
	}

	@Override
	public Date getDeletionTime() {
		return stack.getDeletionTime();
	}

	@Override
	public String getDescription() {
		return stack.getDescription();
	}

	@Override
	public Boolean getDisableRollback() {
		return stack.getDisableRollback();
	}

	@Override
	public String getDocumentTabHeaders(String paneIdentifier) {
		String[] documentPaneIdentifiers = { objectNickName + "Template" };
		String[] documentPaneIPaneHeaders = { "Template" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneIPaneHeaders);
	}

	@Override
	public String getDocumentTabToolTips(String paneIdentifier) {
		String[] documentPaneIdentifiers = { objectNickName + "Template" };
		String[] documentPaneToolTips = { "Cloud Formation Stack Template" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneToolTips);
	}

	@Override
	public Boolean getEnableTerminationProtection() {
		return stack.getEnableTerminationProtection();
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return getAccountStacks(account, appFilter);
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
		return events;
	}

	@Override
	public Date getLastUpdatedTime() {
		return stack.getLastUpdatedTime();
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
		String[] listPaneToolTips = { "General Stack Properties" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneToolTips);
	}

	@Override
	public List<String> getNotificationARNs() {
		return stack.getNotificationARNs();
	}

	@Override
	public String getObjectAWSID() {
		return getStackId();
	}

	@Override
	public String getObjectName() {
		return getStackName();
	}

	@Override
	public List<Output> getOutputs() {
		return stack.getOutputs();
	}

	@Override
	public List<Parameter> getParameters() {
		return stack.getParameters();
	}

	@Override
	public String getParentId() {
		return stack.getParentId();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { objectNickName + "Resources" } };
		String[][][] columnHeaders0 = { { stackResourcesTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		String[][] dataFlags1 = { { objectNickName + "Outputs" } };
		String[][][] columnHeaders1 = { { stackOutputsTableColumnHeaders } };
		map.put(dataFlags1, columnHeaders1);
		String[][] dataFlags2 = { { objectNickName + "Events" } };
		String[][][] columnHeaders2 = { { stackEventsTableColumnHeaders } };
		map.put(dataFlags2, columnHeaders2);
		String[][] dataFlags3 = { { objectNickName + "Parameters" } };
		String[][][] columnHeaders3 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags3, columnHeaders3);
		String[][] dataFlags4 = { { "Tags" } };
		String[][][] columnHeaders4 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags4, columnHeaders4);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "General", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "Resources", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "Outputs", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "Events", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "Parameters", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "Template", "DocumentInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + getStackName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public String getRoleARN() {
		return stack.getRoleARN();
	}

	@Override
	public RollbackConfiguration getRollbackConfiguration() {
		return stack.getRollbackConfiguration();
	}

	@Override
	public String getRootId() {
		return stack.getRootId();
	}

	public Stack getStack() {
		return stack;
	}

	@Override
	public String getStackId() {
		return stack.getStackId();
	}

	@Override
	public String getStackName() {
		return stack.getStackName();
	}

	@Override
	public String getStackStatus() {
		return stack.getStackStatus();
	}

	@Override
	public String getStackStatusReason() {
		return stack.getStackStatusReason();
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { objectNickName + "Resources" }, { objectNickName + "Outputs" }, { objectNickName + "Events" }, { objectNickName + "Parameters" }, { "Tags" } };
		String[][] tablePaneHeaders = { { "Resources" }, { "Outputs" }, { "Events" }, { "Parameters" }, { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { objectNickName + "Resources" }, { objectNickName + "Outputs" }, { objectNickName + "Events" }, { objectNickName + "Parameters" }, { "Tags" } };
		String[][] tablePaneToolTips = { { objectNickName + "Resources" }, { objectNickName + "Outputs" }, { objectNickName + "Events" }, { objectNickName + "Parameters" }, { objectNickName + "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public List<Tag> getTags() {
		return stack.getTags();
	}

	@Override
	public Integer getTimeoutInMinutes() {
		return stack.getTimeoutInMinutes();
	}

	@Override
	public String getTreeNodeLeafText() {
		return getStackName();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(action)) {
			deleteStack(table);
		} else if (actionString.contains(objectNickName + " Properties")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " PROPERTIES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		} else if (actionString.equals(action.toUpperCase() + " " + objectNickName.toUpperCase())) {
			deleteStack(account, (CFStackTreeNodeUserObject) object, node, tree, dash);
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals(objectNickName + "General")) {
			overviewPanel.getDetailesData(object, object.getAccount(), cfStackOverviewHeaderLabels, paneName);
		}
	}

	@Override
	public void remove() {
		deleteStack();
	}

	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	public void setStack(Stack stack) {
		this.stack = stack;
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
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(cfStackOverviewHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	private ArrayList<CFStackTreeNodeUserObject> getAccountStacks(AWSAccount account, String appFilter) {
		AWSCredentials credentials = AwsCommon.getAWSCredentials(account.getAccountAlias());
		AmazonCloudFormation stackbuilder = new AmazonCloudFormationClient(credentials);
		Region region = account.getAccontRegionObject();
		stackbuilder.setRegion(region);
		ArrayList<CFStackTreeNodeUserObject> accountStackList = new ArrayList<CFStackTreeNodeUserObject>();
		DescribeStacksRequest request = new DescribeStacksRequest();
		do {
			DescribeStacksResult result = stackbuilder.describeStacks(request);
			for (Stack stack : result.getStacks()) {
				if (appFilter != null) {
					if (stack.getStackName().matches(appFilter)) {
						accountStackList.add(new CFStackTreeNodeUserObject(stack));
					}
				} else {
					if (stack.getStackName().matches(UtilMethodsFactory.getMatchString(account))) {
						accountStackList.add(new CFStackTreeNodeUserObject(stack));
					}
				}
			}
			request.setNextToken(result.getNextToken());
		} while (request.getNextToken() != null);
		return accountStackList;
	}

	private void deleteStack(CustomTable table) {
		UtilMethodsFactory.removeAction(table, action, objectNickName);
	}

	private void deleteStack(AWSAccount account, CFStackTreeNodeUserObject cfStackTreeNodeUserObject, DefaultMutableTreeNode node, JTree tree, Dashboard dash) {
		UtilMethodsFactory.removeAction(account, cfStackTreeNodeUserObject, node, tree, dash, action, "Stack");
	}

	private void deleteStack() {
		AWSCredentials credentials = AwsCommon.getAWSCredentials(getAccount().getAccountAlias());
		AmazonCloudFormation stackbuilder = new AmazonCloudFormationClient(credentials);
		DeleteStackRequest deleteRequest = new DeleteStackRequest();
		deleteRequest.setStackName(getStackName());
		stackbuilder.deleteStack(deleteRequest);
	}

	public ArrayList<ArrayList<Object>> getStacksResources() {
		ArrayList<ArrayList<Object>> accountStacksResourcesData = new ArrayList<ArrayList<Object>>();
		AWSCredentials credentials = AwsCommon.getAWSCredentials(getAccount().getAccountAlias());
		Region region = account.getAccontRegionObject();
		AmazonCloudFormation stackbuilder = new AmazonCloudFormationClient(credentials);
		stackbuilder.setRegion(region);
		DescribeStacksRequest request = new DescribeStacksRequest();
		do {
			DescribeStacksResult result = stackbuilder.describeStacks(request);
			for (Stack stack : result.getStacks()) {
				if (stack.getStackName().equals(getStackName())) {
					DescribeStackResourcesRequest stackResourceRequest = new DescribeStackResourcesRequest();
					stackResourceRequest.setStackName(stack.getStackName());
					for (StackResource resource : stackbuilder.describeStackResources(stackResourceRequest).getStackResources()) {
						ArrayList<Object> stackData = new ArrayList<Object>();
						stackData.add(resource.getResourceType());
						stackData.add(resource.getLogicalResourceId());
						stackData.add(resource.getPhysicalResourceId());
						if (resource.getDescription() == null) {
							stackData.add("");
						} else {
							stackData.add(resource.getDescription());
						}
						stackData.add(resource.getResourceStatus());
						if (resource.getResourceStatusReason() == null) {
							stackData.add("");
						} else {
							stackData.add(resource.getResourceStatusReason());
						}
						accountStacksResourcesData.add(stackData);
					}
					break;
				}
			}
			request.setNextToken(result.getNextToken());
		} while (request.getNextToken() != null);
		return accountStacksResourcesData;
	}

	public ArrayList<ArrayList<Object>> getStacksOutputs() {
		ArrayList<ArrayList<Object>> cfStackOutputsData = new ArrayList<ArrayList<Object>>();
		for (Output output : getOutputs()) {
			ArrayList<Object> stackOutputData = new ArrayList<Object>();
			stackOutputData.add(output.getOutputKey());
			stackOutputData.add(output.getOutputValue());
			if (output.getDescription() == null) {
				stackOutputData.add("");
			} else {
				stackOutputData.add(output.getDescription());
			}
			cfStackOutputsData.add(stackOutputData);
		}
		return cfStackOutputsData;
	}

	public ArrayList<ArrayList<Object>> getStacksParameters() {
		ArrayList<ArrayList<Object>> cfStackParamsData = new ArrayList<ArrayList<Object>>();
		for (Parameter param : getParameters()) {
			ArrayList<Object> stackParametersData = new ArrayList<Object>();
			stackParametersData.add(param.getParameterKey());
			stackParametersData.add(param.getParameterValue());
			cfStackParamsData.add(stackParametersData);
		}
		return cfStackParamsData;
	}

	@SuppressWarnings("deprecation")
	public ArrayList<ArrayList<Object>> getStacksEvents() {
		ArrayList<ArrayList<Object>> cfStackOutputsData = new ArrayList<ArrayList<Object>>();
		AWSCredentials credentials = AwsCommon.getAWSCredentials(getAccount().getAccountAlias());
		Region region = account.getAccontRegionObject();
		AmazonCloudFormation stackbuilder = new AmazonCloudFormationClient(credentials);
		stackbuilder.setRegion(region);
		DescribeStackEventsRequest stackEventsRequest = new DescribeStackEventsRequest().withStackName(getStackName());
		List<StackEvent> stackEvents = stackbuilder.describeStackEvents(stackEventsRequest).getStackEvents();
		for (StackEvent stackEvent : stackEvents) {
			ArrayList<Object> stackOutputData = new ArrayList<Object>();
			SimpleDateFormat form = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
			if (stackEvent.getTimestamp() == null) {
				stackOutputData.add("");
			} else {
				stackOutputData.add(form.format(stackEvent.getTimestamp()));
			}
			stackOutputData.add(stackEvent.getResourceStatus());
			stackOutputData.add(stackEvent.getResourceType());
			stackOutputData.add(stackEvent.getLogicalResourceId());
			stackOutputData.add(stackEvent.getPhysicalResourceId());
			if (stackEvent.getResourceStatusReason() == null) {
				stackOutputData.add("");
			} else {
				stackOutputData.add(stackEvent.getResourceStatusReason());
			}
			cfStackOutputsData.add(stackOutputData);
		}
		return cfStackOutputsData;
	}

	public static String getStacksTemplate(AWSAccount account, String stackName) {
		AWSCredentials credentials = AwsCommon.getAWSCredentials(account.getAccountAlias());
		Region region = account.getAccontRegionObject();
		AmazonCloudFormation stackbuilder = new AmazonCloudFormationClient(credentials);
		stackbuilder.setRegion(region);
		GetTemplateRequest templateRequest = new GetTemplateRequest();
		templateRequest.setStackName(stackName);
		String template = stackbuilder.getTemplate(templateRequest).getTemplateBody();
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine scriptEngine = manager.getEngineByName("JavaScript");
		scriptEngine.put("jsonString", template);
		try {
			scriptEngine.eval("result = JSON.stringify(JSON.parse(jsonString), null, 2)");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return template;
	}
}
