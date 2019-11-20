package com.arkrud.aws.CustomObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.services.elasticloadbalancing.model.PolicyAttributeDescription;
import com.amazonaws.services.elasticloadbalancing.model.PolicyDescription;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomELBPolicyDescription extends PolicyDescription implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private CustomEC2ELB elb;
	private PolicyDescription description;

	public CustomELBPolicyDescription() {
		super();
	}

	public CustomELBPolicyDescription(PolicyDescription description, CustomEC2ELB elb) {
		this.description = description;
		this.elb = elb;
	}

	public CustomEC2ELB getElb() {
		return elb;
	}

	public void setElb(CustomEC2ELB elb) {
		this.elb = elb;
	}

	@Override
	public String getPolicyName() {
		return description.getPolicyName();
	}

	@Override
	public String getPolicyTypeName() {
		return description.getPolicyTypeName();
	}

	@Override
	public List<PolicyAttributeDescription> getPolicyAttributeDescriptions() {
		return description.getPolicyAttributeDescriptions();
	}

	@Override
	public String getObjectName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("elb-policy");
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTreeNodeLeafText() {
		return getPolicyName();
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
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
	public String getObjectAWSID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAccount(AWSAccount account) {
		// TODO Auto-generated method stub
	}

	@Override
	public AWSAccount getAccount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getkeyEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		// TODO Auto-generated method stub
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		// TODO Auto-generated method stub
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
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}
}
