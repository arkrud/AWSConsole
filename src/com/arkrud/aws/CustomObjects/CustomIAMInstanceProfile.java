package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.InstanceProfile;
import com.amazonaws.services.identitymanagement.model.ListInstanceProfilesRequest;
import com.amazonaws.services.identitymanagement.model.Role;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomIAMInstanceProfile extends InstanceProfile implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private AWSAccount account;
	private InstanceProfile profile;
	private String[] iamInstanceprofilesColumnHeaders = { "Profile Name", "Crete Date", "Profile ID", "Arn", "Path" };
	private JLabel[] ec2VolumeDetailesLabels = { new JLabel("Name"), new JLabel("Created"), new JLabel("ID"), new JLabel("Path") };
	private String objectNickName = "Instance Profile";
	private String action = "Delete";

	public CustomIAMInstanceProfile() {
	}

	public CustomIAMInstanceProfile(InstanceProfile profile) {
		super();
		this.profile = profile;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return iamInstanceprofilesColumnHeaders;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { "Select single " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public String getArn() {
		return profile.getArn();
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("instance-profile-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("instance-profile");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getInstanceProfileName());
		summaryData.add(getCreateDate());
		summaryData.add(getInstanceProfileId());
		summaryData.add(getArn());
		summaryData.add(getPath());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getCreateDate());
		summaryData.add(getInstanceProfileId());
		summaryData.add(getArn());
		summaryData.add(getPath());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getCreateDate() {
		return profile.getCreateDate();
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return getIAMInstanceProfiles(account, appFilter);
	}

	public ArrayList<CustomIAMInstanceProfile> getIAMInstanceProfiles(AWSAccount account, String appFilter) {
		ListInstanceProfilesRequest listInstanceProfilesRequest = new ListInstanceProfilesRequest();
		ArrayList<CustomIAMInstanceProfile> customIAMInstanceProfiles = new ArrayList<CustomIAMInstanceProfile>();
		AmazonIdentityManagementClient aimClient = new AmazonIdentityManagementClient(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		Iterator<InstanceProfile> it = aimClient.listInstanceProfiles(listInstanceProfilesRequest).getInstanceProfiles().iterator();
		while (it.hasNext()) {
			InstanceProfile instanceProfile = it.next();
			if (appFilter != null) {
				if (instanceProfile.getInstanceProfileName().matches(appFilter)) {
					try {
						customIAMInstanceProfiles.add(new CustomIAMInstanceProfile(it.next()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
			} else {
				if (instanceProfile.getInstanceProfileName().matches(UtilMethodsFactory.getMatchString(account))) {
					customIAMInstanceProfiles.add(new CustomIAMInstanceProfile(it.next()));
				}
			}
		}
		return customIAMInstanceProfiles;
	}

	@Override
	public String getInstanceProfileId() {
		return profile.getInstanceProfileId();
	}

	@Override
	public String getInstanceProfileName() {
		return profile.getInstanceProfileName();
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
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
		return getInstanceProfileId();
	}

	@Override
	public String getObjectName() {
		return getInstanceProfileName();
	}

	@Override
	public String getPath() {
		return profile.getPath();
	}

	public InstanceProfile getProfile() {
		return profile;
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
		return summaryDataPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + getObjectName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public List<Role> getRoles() {
		return profile.getRoles();
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
		return getInstanceProfileName();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(action)) {
		} else if (actionString.contains(objectNickName + " Properties")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		account = ((CustomTreeContainer) parentNode.getUserObject()).getAccount();
		if (actionString.equals(objectNickName.toUpperCase() + " PROPERTIES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		} else if (actionString.equals(action.toUpperCase() + " " + objectNickName.toUpperCase())) {
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals(objectNickName + " Details")) {
			overviewPanel.getDetailesData(object, object.getAccount(), UtilMethodsFactory.convertStringArrayToLabels(iamInstanceprofilesColumnHeaders), paneName);
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

	public void setProfile(InstanceProfile profile) {
		this.profile = profile;
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		CustomTableViewInternalFrame theFrame = new CustomTableViewInternalFrame(getpropertiesPaneTitle(), UtilMethodsFactory.generateEC2ObjectPropertiesPane(customAWSObject, jScrollableDesktopPan));
		UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(getpropertiesPaneTitle(), jScrollableDesktopPan, theFrame);
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
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(ec2VolumeDetailesLabels.length);
		return propertyPanelsFieldsCount;
	}
}
