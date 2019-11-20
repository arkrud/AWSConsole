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

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.autoscaling.model.BlockDeviceMapping;
import com.amazonaws.services.autoscaling.model.DescribeLaunchConfigurationsResult;
import com.amazonaws.services.autoscaling.model.InstanceMonitoring;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomEC2LC extends LaunchConfiguration implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private AWSAccount account;
	private LaunchConfiguration launchConfiguration;
	private String objectNickName = "Launch Configuration";
	private String action = "Copy";
	private String[] lcTableColumnHeaders = { "Name", "AMI ID", "Instance Type", "Block Device", "IAM Instance Profile", "Kernel ID", "Key Name", "Monitoring", "EBS Optimized", "ARN", "Security Groups", "RAM Disk ID", "Spot Price", "IP Address Type",
			"Creation Time" };

	public CustomEC2LC() {
	}

	public CustomEC2LC(LaunchConfiguration launchConfiguration) {
		this.launchConfiguration = launchConfiguration;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return lcTableColumnHeaders;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { "Select single LC" };
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
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("lc-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("lc");
	}

	@Override
	public Boolean getAssociatePublicIpAddress() {
		if (launchConfiguration.getAssociatePublicIpAddress() == null) {
			return false;
		} else {
			return launchConfiguration.getAssociatePublicIpAddress();
		}
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getLaunchConfigurationName());
		summaryData.add(new CustomEC2AMI(account, getImageId()));
		summaryData.add(getInstanceType());
		summaryData.add(getBlockDeviceMappings());
		summaryData.add(getIamInstanceProfile());
		summaryData.add(getKernelId());
		summaryData.add(getKeyName());
		summaryData.add(getInstanceMonitoring().getEnabled());
		summaryData.add(getEbsOptimized());
		summaryData.add(getLaunchConfigurationARN());
		summaryData.add(getSecurityGroups());
		summaryData.add(getRamdiskId());
		summaryData.add(getSpotPrice());
		summaryData.add(getAssociatePublicIpAddress());
		summaryData.add(getCreatedTime());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getImageId());
		summaryData.add(getInstanceType());
		summaryData.add(getBlockDeviceMappingsString());
		summaryData.add(getIamInstanceProfile());
		summaryData.add(getKernelId());
		summaryData.add(getKeyName());
		summaryData.add(getInstanceMonitoring().getEnabled());
		summaryData.add(getEbsOptimized());
		summaryData.add(getLaunchConfigurationARN());
		summaryData.add(getSecurityGroupsString());
		summaryData.add(getRamdiskId());
		summaryData.add(getSpotPrice());
		summaryData.add(getAssociatePublicIpAddress());
		summaryData.add(getCreatedTime());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BlockDeviceMapping> getBlockDeviceMappings() {
		return launchConfiguration.getBlockDeviceMappings();
	}

	private String getBlockDeviceMappingsString() {
		List<BlockDeviceMapping> blockDevices = launchConfiguration.getBlockDeviceMappings();
		Iterator<BlockDeviceMapping> ibdm = blockDevices.iterator();
		List<String> bdmNames = new ArrayList<>();
		while (ibdm.hasNext()) {
			bdmNames.add(ibdm.next().getDeviceName());
		}
		if (String.join(",", bdmNames).length() == 0) {
			return " - ";
		} else {
			return String.join(",", bdmNames);
		}
	}

	@Override
	public String getClassicLinkVPCId() {
		return launchConfiguration.getClassicLinkVPCId();
	}

	@Override
	public List<String> getClassicLinkVPCSecurityGroups() {
		return launchConfiguration.getClassicLinkVPCSecurityGroups();
	}

	@Override
	public Date getCreatedTime() {
		return launchConfiguration.getCreatedTime();
	}

	@Override
	public Boolean getEbsOptimized() {
		return launchConfiguration.getEbsOptimized();
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveEC2LCs(account, appFilter);
	}

	@Override
	public String getIamInstanceProfile() {
		return launchConfiguration.getIamInstanceProfile();
	}

	@Override
	public String getImageId() {
		return launchConfiguration.getImageId();
	}

	@Override
	public InstanceMonitoring getInstanceMonitoring() {
		return launchConfiguration.getInstanceMonitoring();
	}

	@Override
	public String getInstanceType() {
		return launchConfiguration.getInstanceType();
	}

	@Override
	public String getKernelId() {
		if (launchConfiguration.getKernelId().length() == 0) {
			return "-";
		} else {
			return launchConfiguration.getKernelId();
		}
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		return events;
	}

	@Override
	public String getKeyName() {
		return launchConfiguration.getKeyName();
	}

	@Override
	public String getLaunchConfigurationARN() {
		return launchConfiguration.getLaunchConfigurationARN();
	}

	@Override
	public String getLaunchConfigurationName() {
		return launchConfiguration.getLaunchConfigurationName();
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + " Details" };
		String[] tablePaneHeaders = { "Detailes" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + " Details" };
		String[] tablePaneToolTips = { objectNickName + " Properties" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public String getObjectAWSID() {
		return getLaunchConfigurationName();
	}

	@Override
	public String getObjectName() {
		return getLaunchConfigurationName();
	}

	@Override
	public String getPlacementTenancy() {
		return launchConfiguration.getPlacementTenancy();
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
		summaryDataPaneTabs.put("LCUserData", "DocumentInfoPane");
		return summaryDataPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + getObjectName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public String getRamdiskId() {
		if (launchConfiguration.getRamdiskId().length() == 0) {
			return "-";
		} else {
			return launchConfiguration.getRamdiskId();
		}
	}

	@Override
	public List<String> getSecurityGroups() {
		return launchConfiguration.getSecurityGroups();
	}

	private String getSecurityGroupsString() {
		return String.join(",", getSecurityGroups());
	}

	@Override
	public String getSpotPrice() {
		if (launchConfiguration.getSpotPrice() == null) {
			return "-";
		} else {
			return launchConfiguration.getSpotPrice();
		}
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
		return getLaunchConfigurationName();
	}

	@Override
	public String getUserData() {
		return launchConfiguration.getUserData();
	}

	@Override
	public Boolean isAssociatePublicIpAddress() {
		return launchConfiguration.isAssociatePublicIpAddress();
	}

	@Override
	public Boolean isEbsOptimized() {
		return launchConfiguration.isEbsOptimized();
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
			ArrayList<JLabel> lcTableColumnLabels = new ArrayList<JLabel>();
			String[] lcTableColumnNames = lcTableColumnHeaders;
			for (int i = 0; i < lcTableColumnNames.length; i++) {
				lcTableColumnLabels.add(new JLabel(lcTableColumnNames[i]));
			}
			overviewPanel.getDetailesData(object, object.getAccount(), lcTableColumnLabels.stream().toArray(JLabel[]::new), paneName);
		}
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

	private ArrayList<CustomEC2LC> retriveEC2LCs(AWSAccount account, String appFilter) {
		ArrayList<CustomEC2LC> customEC2LCs = new ArrayList<CustomEC2LC>();
		AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		AmazonAutoScaling as = AmazonAutoScalingClientBuilder.standard().withRegion(account.getAccountRegion()).withCredentials(provider).build();
		DescribeLaunchConfigurationsResult result = as.describeLaunchConfigurations();
		Iterator<LaunchConfiguration> lcIterator = result.getLaunchConfigurations().iterator();
		while (lcIterator.hasNext()) {
			LaunchConfiguration launchConfiguration = lcIterator.next();
			if (appFilter != null) {
				if (launchConfiguration.getLaunchConfigurationName().matches(appFilter)) {
					customEC2LCs.add(new CustomEC2LC(launchConfiguration));
				}
			} else {
				if (launchConfiguration.getLaunchConfigurationName().matches(UtilMethodsFactory.getMatchString(account))) {
					customEC2LCs.add(new CustomEC2LC(launchConfiguration));
				}
			}
		}
		return customEC2LCs;
	}

	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
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
		ArrayList<JLabel> lcTableColumnLabels = new ArrayList<JLabel>();
		String[] lcTableColumnNames = lcTableColumnHeaders;
		for (int i = 0; i < lcTableColumnNames.length; i++) {
			lcTableColumnLabels.add(new JLabel(lcTableColumnNames[i]));
		}
		propertyPanelsFieldsCount.add(lcTableColumnLabels.size());
		return propertyPanelsFieldsCount;
	}
}
