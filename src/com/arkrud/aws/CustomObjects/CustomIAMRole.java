package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementAsyncClientBuilder;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.GetRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetRolePolicyResult;
import com.amazonaws.services.identitymanagement.model.GetRoleRequest;
import com.amazonaws.services.identitymanagement.model.GetRoleResult;
import com.amazonaws.services.identitymanagement.model.ListRolePoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListRolePoliciesResult;
import com.amazonaws.services.identitymanagement.model.Role;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomIAMRole extends Role implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	public static List<String> getIAMRolePolices(String accountAlias, String roleName) {
		ListRolePoliciesRequest listRolePoliciesRequest = new ListRolePoliciesRequest().withRoleName(roleName);
		AmazonIdentityManagementClient aimClient = new AmazonIdentityManagementClient(AwsCommon.getAWSCredentials(accountAlias));
		ListRolePoliciesResult response = aimClient.listRolePolicies(listRolePoliciesRequest);
		return response.getPolicyNames().stream().collect(Collectors.toList());
	}
	public static String getIAMRolePolicyDocument(String policyName, String roleName, String accountAlias) {
		GetRolePolicyRequest getRolePolicyRequest = new GetRolePolicyRequest().withRoleName(roleName).withPolicyName(policyName);
		AmazonIdentityManagementClient aimClient = new AmazonIdentityManagementClient(AwsCommon.getAWSCredentials(accountAlias));
		GetRolePolicyResult result = aimClient.getRolePolicy(getRolePolicyRequest);
		return result.getPolicyDocument();
	}
	private AWSAccount account;
	private Role role;
	private String objectNickName = "IAM Role";
	private String action = "Delete";


	private String[] iamRoleColumnHeaders = { "Role Name", "Create Date", "Role ID", "Arn", "Path" };

	private JLabel[] iamRoleDetailesLabels = { new JLabel("Name"), new JLabel("Create Date"), new JLabel("ID"), new JLabel("ARN") , new JLabel("Path") };

	public CustomIAMRole() {
		super();
	}

	public CustomIAMRole(Role role) {
		super();
		this.role = role;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		// TODO Auto-generated method stub
		return null;
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
	public String getArn() {
		return role.getArn();
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("role-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("role");
	}

	@Override
	public String getAssumeRolePolicyDocument() {
		return role.getAssumeRolePolicyDocument();
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getRoleName());
		summaryData.add(getCreateDate());
		summaryData.add(getRoleId());
		summaryData.add(getArn());
		summaryData.add(getPath());
		return summaryData;
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
	public Date getCreateDate() {
		return role.getCreateDate();
	}

	@Override
	public String getDocumentTabHeaders(String paneIdentifier) {
		String[] documentPaneIdentifiers = { "AssumeRolePolicyDocument", "RolePolicyDocument" };
		String[] documentPaneIPaneHeaders = { "Assume Role Policy Document", "Role Policy Document" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneIPaneHeaders);
	}

	@Override
	public String getDocumentTabToolTips(String paneIdentifier) {
		String[] documentPaneIdentifiers = { "AssumeRolePolicyDocument", "RolePolicyDocument" };
		String[] documentPaneToolTips = { "Assume Role Policy Document", "Role Policy Document" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneToolTips);
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(CustomIAMInstanceProfile customIAMInstanceProfile) {
		return getIAMRoles(customIAMInstanceProfile);
	}

	public ArrayList<CustomIAMRole> getIAMRoles(CustomIAMInstanceProfile customIAMInstanceProfile) {
		ArrayList<CustomIAMRole> roles = new ArrayList<CustomIAMRole>();
		Iterator<Role> rolesIterator = customIAMInstanceProfile.getRoles().iterator();
		while (rolesIterator.hasNext()) {
			Role role = rolesIterator.next();
			CustomIAMRole customIAMRole = new CustomIAMRole(role);
			customIAMRole.setAccount(customIAMInstanceProfile.getAccount());
			if (role.getRoleName().matches(UtilMethodsFactory.getMatchString(account))) {
				roles.add(customIAMRole);
			}
		}
		return roles;
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
	public String getListTabHeaders(String listIdentifier) {
		String[] listPaneIdentifiers = { objectNickName + " Details" };
		String[] listPaneIPaneHeaders = { "Detailes" };
		return UtilMethodsFactory.getListTabsData(listIdentifier, listPaneIdentifiers, listPaneIPaneHeaders);
	}

	@Override
	public String getListTabToolTips(String listIdentifier) {
		String[] listPaneIdentifiers = { objectNickName + " Details" };
		String[] listPaneToolTips = { objectNickName + " Detailes" };
		return UtilMethodsFactory.getListTabsData(listIdentifier, listPaneIdentifiers, listPaneToolTips);
	}

	@Override
	public String getObjectAWSID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectName() {
		return getRoleName();
	}

	@Override
	public String getPath() {
		return role.getPath();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> summaryDataPaneTabs = new LinkedHashMap<String, String>();
		summaryDataPaneTabs.put(objectNickName + " Details", "ListInfoPane");
		summaryDataPaneTabs.put("AssumeRolePolicyDocument", "DocumentInfoPane");
		summaryDataPaneTabs.put("RolePolicyDocument", "DocumentInfoPane");
		return summaryDataPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + getObjectName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(iamRoleDetailesLabels.length);
		return propertyPanelsFieldsCount;
	}

	public Role getRole() {
		return role;
	}

	@Override
	public String getRoleId() {
		return role.getRoleId();
	}

	@Override
	public String getRoleName() {
		return role.getRoleName();
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
		return getRoleName();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		// TODO Auto-generated method stub
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		account = ((CustomIAMInstanceProfile) parentNode.getUserObject()).getAccount();
		if (actionString.equals(objectNickName.toUpperCase() + " PROPERTIES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		} else if (actionString.equals(action.toUpperCase() + " " + objectNickName.toUpperCase())) {
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals(objectNickName + " Details")) {
			overviewPanel.getDetailesData(object, object.getAccount(), UtilMethodsFactory.convertStringArrayToLabels(iamRoleColumnHeaders), paneName);
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

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		CustomTableViewInternalFrame theFrame = new CustomTableViewInternalFrame(getpropertiesPaneTitle(), UtilMethodsFactory.generateEC2ObjectPropertiesPane(customAWSObject, jScrollableDesktopPan));
		UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(getpropertiesPaneTitle(), jScrollableDesktopPan, theFrame);
	}
	
	
	
	
}
