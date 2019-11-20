package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53Client;
import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.HostedZoneConfig;
import com.amazonaws.services.route53.model.LinkedService;
import com.amazonaws.services.route53.model.ListHostedZonesRequest;
import com.amazonaws.services.route53.model.ListHostedZonesResult;
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResult;
import com.amazonaws.services.route53.model.ListTagsForResourceRequest;
import com.amazonaws.services.route53.model.ListTagsForResourceResult;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import com.amazonaws.services.route53.model.Tag;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomRoute53Zone extends HostedZone implements CustomAWSObject {
	private static final long serialVersionUID = 1L;

	public static ArrayList<CustomRoute53Zone> getRoute53Zones(AWSAccount account, String appFilter) {
		ListHostedZonesRequest listHostedZonesRequest = new ListHostedZonesRequest();
		AmazonRoute53 route53 = new AmazonRoute53Client(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		ListHostedZonesResult result = route53.listHostedZones(listHostedZonesRequest);
		List<HostedZone> hostedZones = result.getHostedZones();
		ArrayList<CustomRoute53Zone> customRoute53Zones = new ArrayList<CustomRoute53Zone>();
		/*GetHostedZoneRequest request = new GetHostedZoneRequest().withId("Z3M3LMPEXAMPLE");
		GetHostedZoneResult response = route53.getHostedZone(request);
		response.getDelegationSet();
		response.getVPCs().get(0).getVPCId();*/
		Iterator<HostedZone> hostedZonesIterator = hostedZones.iterator();
		CustomRoute53Zone customRoute53Zone = null;
		while (hostedZonesIterator.hasNext()) {
			HostedZone zone = hostedZonesIterator.next();
			printRecords( zone.getId(),  route53);
			customRoute53Zone = new CustomRoute53Zone(zone);
			customRoute53Zones.add(customRoute53Zone);
		}
		return customRoute53Zones;
	}

	private static void printRecords(String zoneID, AmazonRoute53 route53) {
		ListResourceRecordSetsRequest request = new ListResourceRecordSetsRequest().withHostedZoneId(zoneID);
		while (true) {
			ListResourceRecordSetsResult result = route53.listResourceRecordSets(request);
			List<ResourceRecordSet> recordList = result.getResourceRecordSets();
			for (ResourceRecordSet record : recordList) {
				System.out.println(record.getName());
			}
			if (!result.isTruncated()) {
				break;
			}
			request.setStartRecordName(result.getNextRecordName());
		}
	}

	private AWSAccount account;
	private HostedZone zone;
	private String objectNickName = "Hosted Zone";
	private String action = "Delete";
	private String[] hostedZoneColumnHeaders = { "Domain Name", "Type", "Record Set Count", "Comment", "ID" };
	private JLabel[] hostedZoneDetailesLabels = { new JLabel("Domain Name"), new JLabel("Type"), new JLabel("Record Set Count"), new JLabel("Comment"),
			new JLabel("ID") };

	public CustomRoute53Zone() {
		super();
	}

	public CustomRoute53Zone(HostedZone zone) {
		super();
		this.zone = zone;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return hostedZoneColumnHeaders;
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

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("dnszone-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("dnszone");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getName());
		if (getConfig().getPrivateZone()) {
			summaryData.add("Private");
		} else {
			summaryData.add("Publick");
		}
		summaryData.add(getResourceRecordSetCount());
		summaryData.add(getConfig().getComment());
		summaryData.add(getId());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getName());
		if (getConfig().getPrivateZone()) {
			summaryData.add("Private");
		} else {
			summaryData.add("Publick");
		}
		summaryData.add(getResourceRecordSetCount());
		summaryData.add(getConfig().getComment());
		summaryData.add(getId());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return UtilMethodsFactory.getAWSRoute53ObjectTagsData(getRoute53ZoneTags(account.getAWSAccountAlias(), getId()).iterator());
	}

	public List<Tag> getRoute53ZoneTags(String accountAlias, String zoneID) {
		ListTagsForResourceRequest listTagsForResourceRequest = new ListTagsForResourceRequest().withResourceId(zoneID).withResourceType("hostedzone");
		AmazonRoute53 route53 = new AmazonRoute53Client(AwsCommon.getAWSCredentials(accountAlias));
		ListTagsForResourceResult result = route53.listTagsForResource(listTagsForResourceRequest);
		List<Tag> hostedZoneTags = result.getResourceTagSet().getTags();
		return hostedZoneTags;
	}

	@Override
	public String getCallerReference() {
		return zone.getCallerReference();
	}

	@Override
	public HostedZoneConfig getConfig() {
		return zone.getConfig();
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
		return getRoute53Zones(account, appFilter);
	}

	@Override
	public String getId() {
		return zone.getId();
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		return events;
	}

	@Override
	public LinkedService getLinkedService() {
		return zone.getLinkedService();
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
		return zone.getName();
	}

	@Override
	public String getObjectAWSID() {
		return zone.getId();
	}

	@Override
	public String getObjectName() {
		return getName();
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
		propertyPanelsFieldsCount.add(hostedZoneDetailesLabels.length);
		propertyPanelsFieldsCount.add(1);
		return propertyPanelsFieldsCount;
	}

	@Override
	public Long getResourceRecordSetCount() {
		return zone.getResourceRecordSetCount();
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
	public String getTreeNodeLeafText() {
		return getName();
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
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " PROPERTIES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		} else if (actionString.equals(action.toUpperCase() + " " + objectNickName)) {
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals("Permissions")) {
		} else if (paneName.equals(objectNickName + " Details")) {
			overviewPanel.getDetailesData(object, object.getAccount(), hostedZoneDetailesLabels, paneName);
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
			theFrame = new CustomTableViewInternalFrame(getpropertiesPaneTitle(),
					UtilMethodsFactory.generateEC2ObjectPropertiesPane(customAWSObject, jScrollableDesktopPan));
			UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(getpropertiesPaneTitle(), jScrollableDesktopPan, theFrame);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(theFrame, "This " + objectNickName + " is Alredy Deregistered", objectNickName + " Gone",
					JOptionPane.WARNING_MESSAGE);
		}
	}
}
