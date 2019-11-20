package com.arkrud.aws.CustomObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.services.route53.model.AliasTarget;
import com.amazonaws.services.route53.model.GeoLocation;
import com.amazonaws.services.route53.model.RRType;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.aws.AWSAccount;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomResourceRecordSet extends ResourceRecordSet implements CustomAWSObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public CustomResourceRecordSet() {
		// TODO Auto-generated constructor stub
	}

	public CustomResourceRecordSet(String name, RRType type) {
		super(name, type);
		// TODO Auto-generated constructor stub
	}

	public CustomResourceRecordSet(String name, String type) {
		super(name, type);
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
		return super.getName();
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		// TODO Auto-generated method stub
	}


}
