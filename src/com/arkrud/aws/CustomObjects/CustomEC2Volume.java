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

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DeleteVolumeRequest;
import com.amazonaws.services.ec2.model.DeleteVolumeResult;
import com.amazonaws.services.ec2.model.DescribeVolumesRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.VolumeAttachment;
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

public class CustomEC2Volume extends Volume implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private Volume volume;
	private AWSAccount account;
	private String objectNickName = "Volume";
	private String action = "Delete";
	private JLabel[] ec2VolumeDetailesLabels = { new JLabel("Name"), new JLabel("ID"), new JLabel("Size"), new JLabel("Type"), new JLabel("IOPS"), new JLabel("Snapshot"), new JLabel("Created"), new JLabel("Availability Zone"), new JLabel("State"),
			new JLabel("Attachment Information"), new JLabel("Encrypted"), new JLabel("KSM Key ID") };
	private String[] ec2VolumeColumnHeaders = { "Volume Name", "Volume ID", "Size", "Type", "IOPS", "Snapshot", "Creation Time", "Availability Zone", "State", "Attachments", "Encrypted", "KSM Key" };

	public CustomEC2Volume() {
	}

	public CustomEC2Volume(AWSAccount account, String volumeId) {
		DescribeVolumesRequest volumesRequest = new DescribeVolumesRequest().withVolumeIds(volumeId);
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		DescribeVolumesResult volumesResult = client.describeVolumes(volumesRequest);
		this.volume = volumesResult.getVolumes().get(0);
		this.account = account;
	}

	public CustomEC2Volume(Volume volume) {
		this.volume = volume;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return ec2VolumeColumnHeaders;
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

	private void deleteVolume() {
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(getAccount().getAccountAlias()));
		DeleteVolumeRequest request = new DeleteVolumeRequest().withVolumeId(getVolumeId());
		DeleteVolumeResult response = client.deleteVolume(request);
		response.getSdkResponseMetadata().getRequestId();
	}

	private void deleteVolume(AWSAccount account, CustomEC2Volume customEC2Volume, DefaultMutableTreeNode node, JTree tree, Dashboard dash) {
		UtilMethodsFactory.removeAction(account, customEC2Volume, node, tree, dash, action, "AMI");
	}

	private void deleteVolume(CustomTable table) {
		UtilMethodsFactory.removeAction(table, action, objectNickName);
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("volume-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("volume");
	}

	private String getAttachmentIDs() {
		List<String> ids = new ArrayList<String>();
		Iterator<VolumeAttachment> itr = volume.getAttachments().iterator();
		while (itr.hasNext()) {
			ids.add(itr.next().getInstanceId());
		}
		return ids.stream().collect(Collectors.joining(", "));
	}

	@Override
	public List<VolumeAttachment> getAttachments() {
		return volume.getAttachments();
	}

	@Override
	public String getAvailabilityZone() {
		return volume.getAvailabilityZone();
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getNameTag());
		summaryData.add(getVolumeId());
		summaryData.add(getSize());
		summaryData.add(getVolumeType());
		summaryData.add(getIops());
		summaryData.add(getSnapshotId());
		summaryData.add(getCreateTime());
		summaryData.add(getAvailabilityZone());
		summaryData.add(getState());
		summaryData.add(getAttachments());
		summaryData.add(getEncrypted());
		summaryData.add(getKmsKeyId());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getVolumeId());
		summaryData.add(getSize());
		summaryData.add(getVolumeType());
		summaryData.add(getIops());
		summaryData.add(getSnapshotId());
		summaryData.add(getCreateTime());
		summaryData.add(getAvailabilityZone());
		summaryData.add(getState());
		summaryData.add(getAttachmentIDs());
		summaryData.add(getEncrypted());
		summaryData.add(getKmsKeyId());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return UtilMethodsFactory.getAWSObjectTagsData(getTags().iterator());
	}

	@Override
	public Date getCreateTime() {
		return volume.getCreateTime();
	}

	@Override
	public Boolean getEncrypted() {
		return volume.getEncrypted();
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return getVolumes(account, appFilter);
	}

	@Override
	public Integer getIops() {
		return volume.getIops();
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		return events;
	}

	@Override
	public String getKmsKeyId() {
		if (volume.getKmsKeyId() == null) {
			return "";
		} else {
			return volume.getKmsKeyId();
		}
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

	private String getNameTag() {
		String volumeName = "";
		Iterator<Tag> tags = getTags().iterator();
		while (tags.hasNext()) {
			Tag tag = tags.next();
			if (tag.getKey().startsWith("Name")) {
				volumeName = tag.getValue();
			}
		}
		return volumeName;
	}

	@Override
	public String getObjectAWSID() {
		return volume.getSnapshotId();
	}

	@Override
	public String getObjectName() {
		return getNameTag();
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
		LinkedHashMap<String, String> summaryDataPaneTabs = new LinkedHashMap<String, String>();
		summaryDataPaneTabs.put(objectNickName + " Details", "ListInfoPane");
		summaryDataPaneTabs.put("Tags", "TableInfoPane");
		return summaryDataPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + getVolumeId() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public Integer getSize() {
		return volume.getSize();
	}

	@Override
	public String getSnapshotId() {
		return volume.getSnapshotId();
	}

	@Override
	public String getState() {
		return volume.getState();
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
		String[][] tablePaneToolTips = { { "Volume Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public List<Tag> getTags() {
		return volume.getTags();
	}

	@Override
	public String getTreeNodeLeafText() {
		return getNameTag();
	}

	@Override
	public String getVolumeId() {
		return volume.getVolumeId();
	}

	private ArrayList<CustomEC2Volume> getVolumes(AWSAccount account, String appFilter) {
		ArrayList<CustomEC2Volume> customEC2Volumes = new ArrayList<CustomEC2Volume>();
		DescribeVolumesRequest volumesRequest = new DescribeVolumesRequest();
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		DescribeVolumesResult volumesResult = client.describeVolumes(volumesRequest);
		Iterator<Volume> volumesIterator = volumesResult.getVolumes().iterator();
		while (volumesIterator.hasNext()) {
			Volume volume = volumesIterator.next();
			if (appFilter != null) {
				if (UtilMethodsFactory.getEC2ObjectFilterTag(volume.getTags(), "Name").matches(appFilter)) {
					customEC2Volumes.add(new CustomEC2Volume(volume));
				}
			} else {
				if (UtilMethodsFactory.getEC2ObjectFilterTag(volume.getTags(), "Name").matches(UtilMethodsFactory.getMatchString(account))) {
					customEC2Volumes.add(new CustomEC2Volume(volume));
				}
			}
		}
		return customEC2Volumes;
	}

	@Override
	public String getVolumeType() {
		return volume.getVolumeType();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(action)) {
			deleteVolume(table);
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
			deleteVolume(account, (CustomEC2Volume) object, node, tree, dash);
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals(objectNickName + " Details")) {
			overviewPanel.getDetailesData(object, object.getAccount(), ec2VolumeDetailesLabels, paneName);
		}
	}

	@Override
	public void remove() {
		deleteVolume();
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
		propertyPanelsFieldsCount.add(ec2VolumeDetailesLabels.length);
		return propertyPanelsFieldsCount;
	}
}
