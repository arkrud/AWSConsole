package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.BlockDeviceMapping;
import com.amazonaws.services.ec2.model.DeregisterImageRequest;
import com.amazonaws.services.ec2.model.DescribeImageAttributeRequest;
import com.amazonaws.services.ec2.model.DescribeImageAttributeResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.LaunchPermission;
import com.amazonaws.services.ec2.model.ProductCode;
import com.amazonaws.services.ec2.model.Tag;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.StaticFactories.EC2Common;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

@SuppressWarnings("serial")
public class CustomEC2AMI extends Image implements CustomAWSObject {
	private String objectNickName = "AMI";
	private String action = "deregister";
	private Image ami;
	private AWSAccount account;
	private JLabel[] ec2AMIDetailesHeaderLabels = { new JLabel("AMI ID:"), new JLabel("AMIName:"), new JLabel("Owner:"), new JLabel("Source:"), new JLabel("Status:"), new JLabel("State reason:"), new JLabel("Creation Date:"), new JLabel("Platform:"),
			new JLabel("Architecture:"), new JLabel("Image Type:"), new JLabel("Virtualization type:"), new JLabel("Description:"), new JLabel("Root Device Name:"), new JLabel("Root Device Type:"), new JLabel("RAM Disk ID:"),
			new JLabel("Kernel ID:"), new JLabel("Products Codes:"), new JLabel("Block Devices:") };
	String[] amiTableColumnHeaders = { "AMI Name", "AMI ID", "Creation Date", "Owner", "Visibility", "Status", "Architecture", "Platform", "Root Device Name", "Root Device", "Block Devices", "Size", "Image Type", "Virtualization" };

	public CustomEC2AMI() {
	}

	public CustomEC2AMI(AWSAccount account, String amiID) {
		DescribeImagesRequest request = new DescribeImagesRequest();
		request.withImageIds(amiID);
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		Collection<Image> images = client.describeImages(request).getImages();
		Iterator<Image> imagesIterator = images.iterator();
		while (imagesIterator.hasNext()) {
			this.ami = imagesIterator.next();
		}
	}

	public CustomEC2AMI(Image ami) {
		this.ami = ami;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return amiTableColumnHeaders;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName + "(s)" };
		return menus;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName + "(s)", objectNickName + " Properties" };
		return menus;
	}

	private void deregister() {
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(getAccount().getAccountAlias()));
		DeregisterImageRequest request = new DeregisterImageRequest().withImageId(getImageId());
		client.deregisterImage(request);
	}

	private void deregisterAMI(AWSAccount account, CustomEC2AMI customEC2AMI, DefaultMutableTreeNode node, JTree tree, Dashboard dash) {
		UtilMethodsFactory.removeAction(account, customEC2AMI, node, tree, dash, action, objectNickName);
	}

	private void deregisterAMI(CustomTable table) {
		UtilMethodsFactory.removeAction(table, action, objectNickName);
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public String getArchitecture() {
		return ami.getArchitecture();
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("ami-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("ami");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getImageId());
		summaryData.add(getName());
		summaryData.add(getOwnerId());
		summaryData.add(getImageLocation());
		summaryData.add(getState());
		summaryData.add(getStateReasonMessage());
		summaryData.add(getCreationDate());
		summaryData.add(getPlatform());
		summaryData.add(getArchitecture());
		summaryData.add(getImageType());
		summaryData.add(getVirtualizationType());
		summaryData.add(getDescription());
		summaryData.add(getRootDeviceName());
		summaryData.add(getRootDeviceType());
		summaryData.add(getRamdiskId());
		summaryData.add(getKernelId());
		summaryData.add(getProductCodesString());
		summaryData.add(getBlockDeviceMappingsString());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getImageId());
		summaryData.add(getCreationDate());
		summaryData.add(getOwnerId());
		summaryData.add(getPublicStatus());
		summaryData.add(getState());
		summaryData.add(getArchitecture());
		summaryData.add(getPlatform());
		summaryData.add(getRootDeviceName());
		summaryData.add(getRootDeviceType());
		summaryData.add(getBlockDeviceMappingsString());
		summaryData.add(getBlockDeviceMappingsString().split(":")[2]);
		summaryData.add(getImageType());
		summaryData.add(getVirtualizationType());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return UtilMethodsFactory.getAWSObjectTagsData(getTags().iterator());
	}

	private String getBlockDeviceMappingsString() {
		List<BlockDeviceMapping> blockDevices = ami.getBlockDeviceMappings();
		Iterator<BlockDeviceMapping> ibdm = blockDevices.iterator();
		String bdmsString = "";
		while (ibdm.hasNext()) {
			BlockDeviceMapping bdm = ibdm.next();
			if (bdm.getVirtualName() == null) {
				String encriptedState = "";
				if (bdm.getEbs().getEncrypted()) {
					encriptedState = "Encripted";
				} else {
					encriptedState = "Unencripted";
				}
				String ebsInfo = bdm.getEbs().getSnapshotId() + ":" + encriptedState + ":" + String.valueOf(bdm.getEbs().getVolumeSize()) + "GiB:" + bdm.getEbs().getVolumeType();
				bdmsString = bdmsString + bdm.getDeviceName() + "=" + ebsInfo + ",";
			} else {
				bdmsString = bdmsString + bdm.getDeviceName() + "=" + bdm.getVirtualName() + ",";
			}
		}
		bdmsString = bdmsString.substring(0, bdmsString.length() - 1);
		return bdmsString;
	}

	@Override
	public String getCreationDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		Date date = null;
		try {
			date = formatter.parse(ami.getCreationDate());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		return formatter1.format(date);
	}

	@Override
	public String getDescription() {
		return ami.getDescription();
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
		return retriveEC2AMIs(account, appFilter);
	}

	@Override
	public String getImageId() {
		return ami.getImageId();
	}

	@Override
	public String getImageLocation() {
		return ami.getImageLocation();
	}

	@Override
	public String getImageType() {
		return ami.getImageType();
	}

	@Override
	public String getKernelId() {
		return ami.getKernelId();
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		events.add(KeyEvent.VK_2);
		return events;
	}

	private ArrayList<String> getLaunchPermissions() {
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		DescribeImageAttributeRequest imageAttrRequest = new DescribeImageAttributeRequest().withImageId(ami.getImageId()).withAttribute("launchPermission");
		DescribeImageAttributeResult imageAttrResult = client.describeImageAttribute(imageAttrRequest);
		List<LaunchPermission> imagePermissions = imageAttrResult.getImageAttribute().getLaunchPermissions();
		ArrayList<String> launchPermissions = new ArrayList<String>();
		Iterator<LaunchPermission> iip = imagePermissions.iterator();
		while (iip.hasNext()) {
			launchPermissions.add(iip.next().getUserId());
		}
		return launchPermissions;
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + " Details", "Permissions" };
		String[] tablePaneToolTips = { "Properties", "Permissions" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + " Details", "Permissions" };
		String[] tablePaneToolTips = { objectNickName + " Properties", objectNickName + " Permissions" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public String getName() {
		return ami.getName();
	}

	@Override
	public String getObjectAWSID() {
		String id = "";
		try {
			id = ami.getImageId();
		} catch (Exception e1) {
			id = objectNickName + " Deregistered";
		}
		return id;
	}

	@Override
	public String getObjectName() {
		return getName();
	}

	@Override
	public String getOwnerId() {
		return ami.getOwnerId();
	}

	@Override
	public String getPlatform() {
		if (ami.getPlatform() == null) {
			return "Other Linux";
		} else {
			return ami.getPlatform();
		}
	}

	private String getProductCodesString() {
		List<ProductCode> productCodes = ami.getProductCodes();
		Iterator<ProductCode> ipc = productCodes.iterator();
		String ipcString = "";
		while (ipc.hasNext()) {
			ProductCode pc = ipc.next();
			ipcString = ipcString + pc.getProductCodeId() + ":" + pc.getProductCodeType();
		}
		return ipcString;
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags = { { "Tags" } };
		String[][][] columnHeaders = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags, columnHeaders);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + " Details", "ListInfoPane");
		propertiesPaneTabs.put("Permissions", "ListInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + getName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(ec2AMIDetailesHeaderLabels.length);
		propertyPanelsFieldsCount.add(1);
		return propertyPanelsFieldsCount;
	}

	private String getPublicStatus() {
		if (ami.getPublic()) {
			return "Public";
		} else {
			return "Private";
		}
	}

	@Override
	public String getRamdiskId() {
		if (ami.getRamdiskId() == null) {
			return "-";
		} else {
			return ami.getRamdiskId();
		}
	}

	@Override
	public String getRootDeviceName() {
		return ami.getRootDeviceName();
	}

	@Override
	public String getRootDeviceType() {
		return ami.getRootDeviceType();
	}

	@Override
	public String getState() {
		return ami.getState();
	}

	private String getStateReasonMessage() {
		if (ami.getStateReason() == null) {
			return "-";
		} else {
			return ami.getStateReason().getMessage();
		}
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "Tags" } };
		String[][] tablePaneToolTips = { { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "Tags" } };
		String[][] tablePaneToolTips = { { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public List<Tag> getTags() {
		return ami.getTags();
	}

	@Override
	public String getTreeNodeLeafText() {
		return getName();
	}

	@Override
	public String getVirtualizationType() {
		return ami.getVirtualizationType();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(action)) {
			deregisterAMI(table);
		} else if (actionString.contains(objectNickName + " Properties")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName + " PROPERTIES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		} else if (actionString.equals(action.toUpperCase() + " " + objectNickName)) {
			deregisterAMI(account, (CustomEC2AMI) object, node, tree, dash);
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals("Permissions")) {
			overviewPanel.launchPermissionsData(getLaunchPermissions(), object.getAccount());
		} else if (paneName.equals(objectNickName + " Details")) {
			overviewPanel.getDetailesData(object, object.getAccount(), ec2AMIDetailesHeaderLabels, paneName);
		}
	}

	@Override
	public void remove() {
		deregister();
	}

	private ArrayList<CustomEC2AMI> retriveEC2AMIs(AWSAccount account, String appFilter) {
		ArrayList<CustomEC2AMI> amis = new ArrayList<CustomEC2AMI>();
		DescribeImagesRequest request = new DescribeImagesRequest();
		request.withOwners("self");
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		Collection<Image> images = client.describeImages(request).getImages();
		Iterator<Image> imagesIterator = images.iterator();
		CustomEC2AMI customEC2AMI = null;
		while (imagesIterator.hasNext()) {
			Image image = imagesIterator.next();
			if (appFilter != null) {
				customEC2AMI = new CustomEC2AMI(image);
				if (customEC2AMI.getName().matches(appFilter)) {

					amis.add(customEC2AMI);
				}
			} else {
				customEC2AMI = new CustomEC2AMI(image);
				if (customEC2AMI.getName().matches(UtilMethodsFactory.getMatchString(account))) {

					amis.add(customEC2AMI);
				}
			}
		}
		return amis;
	}

	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		AWSAccount activeAccount;
		CustomTableViewInternalFrame theFrame = null;
		if (account.getAccountAlias().equals("AspenDev")) {
			activeAccount = account;
		} else {
			activeAccount = new AWSAccount();
			activeAccount.setAccountAlias("AspenDev");
			((CustomEC2AMI) customAWSObject).setAccount(activeAccount);
		}
		try {
			theFrame = new CustomTableViewInternalFrame(getpropertiesPaneTitle(), UtilMethodsFactory.generateEC2ObjectPropertiesPane(customAWSObject, jScrollableDesktopPan));
			UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(getpropertiesPaneTitle(), jScrollableDesktopPan, theFrame);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(theFrame, "This " + objectNickName + " is Alredy Deregistered", objectNickName + " Gone", JOptionPane.WARNING_MESSAGE);
		}
	}
}
