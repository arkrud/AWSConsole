package com.arkrud.aws.CustomObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.model.AliasTarget;
import com.amazonaws.services.route53.model.GeoLocation;
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResult;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import com.amazonaws.services.route53.AmazonRoute53Client;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.OverviewPanel;
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AliasTarget getAliasTarget() {
		// TODO Auto-generated method stub
		return super.getAliasTarget();
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("dnsrecordset");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		// TODO Auto-generated method stub
		return null;
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
	public String getFailover() {
		// TODO Auto-generated method stub
		return super.getFailover();
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
		// TODO Auto-generated method stub
		return super.getGeoLocation();
	}

	@Override
	public String getHealthCheckId() {
		// TODO Auto-generated method stub
		return super.getHealthCheckId();
	}

	@Override
	public List<Integer> getkeyEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getMultiValueAnswer() {
		// TODO Auto-generated method stub
		return super.getMultiValueAnswer();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return resourceRecordSet.getName();
	}

	@Override
	public String getObjectAWSID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getpropertiesPaneTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRegion() {
		// TODO Auto-generated method stub
		return super.getRegion();
	}

	@Override
	public List<ResourceRecord> getResourceRecords() {
		// TODO Auto-generated method stub
		return super.getResourceRecords();
	}

	@Override
	public String getSetIdentifier() {
		// TODO Auto-generated method stub
		return super.getSetIdentifier();
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
	public String getTrafficPolicyInstanceId() {
		// TODO Auto-generated method stub
		return super.getTrafficPolicyInstanceId();
	}

	@Override
	public String getTreeNodeLeafText() {
		return getName();
	}

	@Override
	public Long getTTL() {
		// TODO Auto-generated method stub
		return super.getTTL();
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return super.getType();
	}

	@Override
	public Long getWeight() {
		// TODO Auto-generated method stub
		return super.getWeight();
	}

	@Override
	public Boolean isMultiValueAnswer() {
		// TODO Auto-generated method stub
		return super.isMultiValueAnswer();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		// TODO Auto-generated method stub
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		// TODO Auto-generated method stub
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
	}
}
