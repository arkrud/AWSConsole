package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
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
import com.amazonaws.services.ec2.model.CreateVolumePermission;
import com.amazonaws.services.ec2.model.DeleteSnapshotRequest;
import com.amazonaws.services.ec2.model.DescribeSnapshotAttributeRequest;
import com.amazonaws.services.ec2.model.DescribeSnapshotAttributeResult;
import com.amazonaws.services.ec2.model.DescribeSnapshotsRequest;
import com.amazonaws.services.ec2.model.Snapshot;
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

public class CustomEC2SnapShot extends Snapshot implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private String objectNickName = "Snapshot";
	private String action = "Delete";
	private Snapshot snapshot;
	private AWSAccount account;
	String[] snapShotsTableColumnHeaders = { "SnapShot Name", "SnapShot ID", "Size", "Decription", "Status", "Started", "Progress", "Volume", "Owner", "Encrypted", "KMS Key ID", "KMS Key Aliases" };
	private JLabel[] ec2SnapshotDetailesHeaderLabels = { new JLabel("Snapshot ID:"), new JLabel("Progress:"), new JLabel("Status:"), new JLabel("Capacity:"), new JLabel("Volume:"), new JLabel("Encrypted:"), new JLabel("Started:"),
			new JLabel("Owner:"), new JLabel("Description:") };

	public CustomEC2SnapShot() {
		super();
	}

	public CustomEC2SnapShot(Snapshot snapshot) {
		this.snapshot = snapshot;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return snapShotsTableColumnHeaders;
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
		return UtilMethodsFactory.populateInterfaceImages().get("snapshot-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("snapshot");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getSnapshotId());
		summaryData.add(getProgress());
		summaryData.add(getState());
		summaryData.add(getVolumeSize());
		summaryData.add(getVolumeId());
		summaryData.add(isEncrypted());
		summaryData.add(getStartTime());
		summaryData.add(getOwnerId());
		summaryData.add(getDescription());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getSnapshotId());
		summaryData.add(getVolumeSize());
		summaryData.add(getDescription());
		summaryData.add(getState());
		summaryData.add(getStartTime());
		summaryData.add(getProgress());
		summaryData.add(getVolumeId());
		summaryData.add(getOwnerId());
		summaryData.add(isEncrypted());
		summaryData.add(getKmsKeyId());
		summaryData.add(getDataEncryptionKeyId());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return UtilMethodsFactory.getAWSObjectTagsData(getTags().iterator());
	}

	@Override
	public String getDataEncryptionKeyId() {
		String keyId = "";
		if (snapshot.getDataEncryptionKeyId() == null) {
			keyId = "-";
		} else {
			keyId = snapshot.getDataEncryptionKeyId();
		}
		return keyId;
	}

	@Override
	public String getDescription() {
		return snapshot.getDescription();
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
	public Boolean getEncrypted() {
		return snapshot.getEncrypted();
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveSnapShots(account, appFilter);
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
	public String getKmsKeyId() {
		String keyId = "";
		if (snapshot.getKmsKeyId() == null) {
			keyId = "-";
		} else {
			keyId = snapshot.getKmsKeyId();
		}
		return keyId;
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
	public String getObjectAWSID() {
		return getSnapshotId();
	}

	@Override
	public String getObjectName() {
		return UtilMethodsFactory.getEC2ObjectFilterTag(getTags(), "Name");
	}

	@Override
	public String getOwnerAlias() {
		return snapshot.getOwnerAlias();
	}

	@Override
	public String getOwnerId() {
		return snapshot.getOwnerId();
	}

	@Override
	public String getProgress() {
		return snapshot.getProgress();
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
		return objectNickName + " Properties for " + getObjectName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(ec2SnapshotDetailesHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getSnapshotId() {
		return snapshot.getSnapshotId();
	}

	private ArrayList<String> getSnapShotLaunchPermissions() {
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(getAccount().getAccountAlias()));
		DescribeSnapshotAttributeRequest snapShotAttrRequest = new DescribeSnapshotAttributeRequest().withSnapshotId(getSnapshotId()).withAttribute("createVolumePermission");
		DescribeSnapshotAttributeResult snapShotAttrResult = client.describeSnapshotAttribute(snapShotAttrRequest);
		List<CreateVolumePermission> createVolumePermissions = snapShotAttrResult.getCreateVolumePermissions();
		ArrayList<String> snapshotPermissions = new ArrayList<String>();
		Iterator<CreateVolumePermission> iip = createVolumePermissions.iterator();
		while (iip.hasNext()) {
			snapshotPermissions.add(iip.next().getUserId());
		}
		return snapshotPermissions;
	}

	@Override
	public Date getStartTime() {
		return snapshot.getStartTime();
	}

	@Override
	public String getState() {
		return snapshot.getState();
	}

	@Override
	public String getStateMessage() {
		return snapshot.getStateMessage();
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
		return snapshot.getTags();
	}

	@Override
	public String getTreeNodeLeafText() {
		return getObjectName();
	}

	@Override
	public String getVolumeId() {
		return snapshot.getVolumeId();
	}

	@Override
	public Integer getVolumeSize() {
		return snapshot.getVolumeSize();
	}

	@Override
	public Boolean isEncrypted() {
		return snapshot.isEncrypted();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(action)) {
			UtilMethodsFactory.removeAction(table, action, objectNickName);
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
			UtilMethodsFactory.removeAction(account, object, node, tree, dash, action, objectNickName);
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals("Permissions")) {
			overviewPanel.launchPermissionsData(getSnapShotLaunchPermissions(), object.getAccount());
		} else if (paneName.equals(objectNickName + " Details")) {
			overviewPanel.getDetailesData(object, object.getAccount(), ec2SnapshotDetailesHeaderLabels, paneName);
		}
	}

	@Override
	public void remove() {
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(getAccount().getAccountAlias()));
		DeleteSnapshotRequest request = new DeleteSnapshotRequest().withSnapshotId(getSnapshotId());
		try {
			client.deleteSnapshot(request);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, objectNickName + getSnapshotId() + " is in use", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private ArrayList<CustomEC2SnapShot> retriveSnapShots(AWSAccount account, String appFilter) {
		ArrayList<CustomEC2SnapShot> customEC2SnapShots = new ArrayList<CustomEC2SnapShot>();
		DescribeSnapshotsRequest describeSnapshotsRequest = new DescribeSnapshotsRequest();
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		List<Snapshot> snapShots = client.describeSnapshots(describeSnapshotsRequest).getSnapshots();
		Iterator<Snapshot> snapShotsIterator = snapShots.iterator();
		CustomEC2SnapShot customEC2SnapShot = null;
		while (snapShotsIterator.hasNext()) {
			Snapshot snapshot = snapShotsIterator.next();
			 customEC2SnapShot = new CustomEC2SnapShot(snapshot);
			if (appFilter != null) {
				if (customEC2SnapShot.getObjectName().matches(appFilter)) {

					customEC2SnapShots.add(customEC2SnapShot);
				}
			} else {
				if (customEC2SnapShot.getObjectName().matches(UtilMethodsFactory.getMatchString(account))) {

					customEC2SnapShots.add(customEC2SnapShot);
				}
			}
		}
		return customEC2SnapShots;
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
}
