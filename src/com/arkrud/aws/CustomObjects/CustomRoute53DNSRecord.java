package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53Client;
import com.amazonaws.services.route53.model.AliasTarget;
import com.amazonaws.services.route53.model.GeoLocation;
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResult;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomRoute53DNSRecord extends ResourceRecordSet implements CustomAWSObject {
	private static final long serialVersionUID = 1L;

	private static ArrayList<ResourceRecordSet> getResourceRecordSets(AWSAccount account, String zoneID, String appFilter) {
		AmazonRoute53 route53 = new AmazonRoute53Client(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		ArrayList<ResourceRecordSet> resourceRecordSets = new ArrayList<ResourceRecordSet>();
		ListResourceRecordSetsRequest request = new ListResourceRecordSetsRequest().withHostedZoneId(zoneID);
		while (true) {
			ListResourceRecordSetsResult result = route53.listResourceRecordSets(request);
			List<ResourceRecordSet> recordList = result.getResourceRecordSets();
			for (ResourceRecordSet record : recordList) {
				if (record.getName().matches(appFilter)) {
					resourceRecordSets.add(record);
				}
			}
			if (!result.isTruncated()) {
				break;
			}
		}
		return resourceRecordSets;
	}

	private static ArrayList<CustomRoute53DNSRecord> getResourceRecordSets(CustomRoute53Zone customRoute53Zone, String appFilter) {
		AmazonRoute53 route53 = new AmazonRoute53Client(AwsCommon.getAWSCredentials(customRoute53Zone.getAccount().getAccountAlias()));
		ArrayList<CustomRoute53DNSRecord> resourceRecordSets = new ArrayList<CustomRoute53DNSRecord>();
		ListResourceRecordSetsRequest request = new ListResourceRecordSetsRequest().withHostedZoneId(customRoute53Zone.getId());
		while (true) {
			ListResourceRecordSetsResult result = route53.listResourceRecordSets(request);
			List<ResourceRecordSet> recordList = result.getResourceRecordSets();
			for (ResourceRecordSet record : recordList) {
				if (record.getName().matches(appFilter)) {
					resourceRecordSets.add(new CustomRoute53DNSRecord(record));
				}
			}
			if (!result.isTruncated()) {
				break;
			}
			request.setStartRecordName(result.getNextRecordName());
		}
		return resourceRecordSets;
	}

	private ResourceRecordSet resourceRecordSet;
	private String zoneID;
	private AWSAccount account;
	private String action = "Delete";
	private String objectNickName = "DNSRecordSet";
	private String[] recordSetColumnHeaders = { "Record Set Name", "Type", "Value", "TTL" };
	private JLabel[] recordSetDetailesLabels = { new JLabel("Record Set Name"), new JLabel("Name"),new JLabel("Type"), new JLabel("Value"), new JLabel("TTL") };
	private JLabel[] recordSetAdvancedLabels = { new JLabel("Evaluate target Health"), new JLabel("Helth Check ID"), new JLabel("Region"), new JLabel("Weight"), new JLabel("Geolocation"), new JLabel("Multivalue Answer"), new JLabel("Set ID"), new JLabel("Failover"), new JLabel("Traffic Policy Instance Id")  };

	public CustomRoute53DNSRecord() {
	}

	public CustomRoute53DNSRecord(ResourceRecordSet resourceRecordSet) {
		super();
		this.resourceRecordSet = resourceRecordSet;
	}

	public CustomRoute53DNSRecord(String zoneID) {
		super();
		this.zoneID = zoneID;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return recordSetColumnHeaders;
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
	public AliasTarget getAliasTarget() {
		return resourceRecordSet.getAliasTarget();
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("dnsrecordset-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("dnsrecordset");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(StringUtils.substring(getName(),0, -1));
		summaryData.add(getType());
		if (getResourceRecords().size() > 0 ) {
			summaryData.add(getResourceRecords().get(0).getValue());
			
		} else {
			if (getAliasTarget() != null) {
				summaryData.add("ALIAS " + getAliasTarget().getDNSName());
			} else {
				summaryData.add(" - ");
			}
			
		}
		summaryData.add(getTTL());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getType());
		if (getResourceRecords().size() > 0 ) {
			summaryData.add(getResourceRecords().get(0).getValue());
			
		} else {
			if (getAliasTarget() != null) {
				summaryData.add("ALIAS " + getAliasTarget().getDNSName());
			} else {
				summaryData.add(" - ");
			}
			
		}
		summaryData.add(getTTL());
		return summaryData;
	}
	
	public ArrayList<Object> getRecordSetAdvancedPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		if (getAliasTarget() != null) {
			summaryData.add(getAliasTarget().getEvaluateTargetHealth());
		} else {
			summaryData.add(" - ");
		}
		
		summaryData.add(getHealthCheckId());
		summaryData.add(getRegion());
		summaryData.add(getWeight());
		summaryData.add(getGeoLocation());
		summaryData.add(getMultiValueAnswer());
		summaryData.add(getSetIdentifier());
		summaryData.add(getFailover());
		summaryData.add(getTrafficPolicyInstanceId());
		
		
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return null;
	}

	@Override
	public String getDocumentTabHeaders(String paneIdentifier) {
		return null;
	}

	@Override
	public String getDocumentTabToolTips(String paneIdentifier) {
		return null;
	}

	@Override
	public String getFailover() {
		return resourceRecordSet.getFailover();
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return getResourceRecordSets(account, zoneID, appFilter);
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(CustomRoute53Zone customRoute53Zone, String appFilter) {
		return getResourceRecordSets(customRoute53Zone, appFilter);
	}

	@Override
	public GeoLocation getGeoLocation() {
		return resourceRecordSet.getGeoLocation();
	}

	@Override
	public String getHealthCheckId() {
		return resourceRecordSet.getHealthCheckId();
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		return events;
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + " Details", objectNickName + " Advanced" };
		String[] tablePaneHeaders = { "Detailes", "Advanced" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + " Details", objectNickName + " Advanced" };
		String[] tablePaneToolTips = { objectNickName + " Properties", objectNickName + " Routing & Failover" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public Boolean getMultiValueAnswer() {
		return resourceRecordSet.getMultiValueAnswer();
	}

	@Override
	public String getName() {
		return resourceRecordSet.getName();
	}

	@Override
	public String getObjectAWSID() {
		return resourceRecordSet.getName();
	}

	@Override
	public String getObjectName() {
		return resourceRecordSet.getName();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		return null;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> summaryDataPaneTabs = new LinkedHashMap<String, String>();
		summaryDataPaneTabs.put(objectNickName + " Details", "ListInfoPane");
		summaryDataPaneTabs.put(objectNickName + " Advanced", "ListInfoPane");
		return summaryDataPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + getName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(recordSetDetailesLabels.length);
		propertyPanelsFieldsCount.add(recordSetAdvancedLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getRegion() {
		return resourceRecordSet.getRegion();
	}

	@Override
	public List<ResourceRecord> getResourceRecords() {
		return resourceRecordSet.getResourceRecords();
	}

	@Override
	public String getSetIdentifier() {
		return resourceRecordSet.getSetIdentifier();
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		return null;
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		return null;
	}

	@Override
	public String getTrafficPolicyInstanceId() {
		return resourceRecordSet.getTrafficPolicyInstanceId();
	}

	@Override
	public String getTreeNodeLeafText() {
		return getName();
	}

	@Override
	public Long getTTL() {
		return resourceRecordSet.getTTL();
	}

	@Override
	public String getType() {
		return resourceRecordSet.getType();
	}

	@Override
	public Long getWeight() {
		return resourceRecordSet.getWeight();
	}

	@Override
	public Boolean isMultiValueAnswer() {
		return resourceRecordSet.isMultiValueAnswer();
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
		setAccount(((CustomRoute53Zone) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " PROPERTIES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		} else if (actionString.equals(action.toUpperCase() + " " + objectNickName.toUpperCase())) {
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals(objectNickName + " Details")) {
			overviewPanel.getDetailesData(object, object.getAccount(), recordSetDetailesLabels, paneName);
		} else if (paneName.equals(objectNickName + " Advanced")) {
			overviewPanel.getDetailesData(object, object.getAccount(), recordSetAdvancedLabels, paneName);
		}
	}

	@Override
	public void remove() {
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
