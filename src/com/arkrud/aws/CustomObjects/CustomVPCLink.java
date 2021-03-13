package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.GetVpcLinksRequest;
import com.amazonaws.services.apigateway.model.GetVpcLinksResult;
import com.amazonaws.services.apigateway.model.VpcLink;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomVPCLink extends VpcLink implements CustomAWSObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String objectNickName = "VPCLink";
	private VpcLink vpcLink;
	private AWSAccount account;
	private String[] vpcLinksTableColumnHeaders = { "Name", "ID", "Target ARN", "Description", "Status" };
	private JLabel[] vpcLinkOverviewHeaderLabels = { new JLabel("Name: "), new JLabel("ID: "), new JLabel("Target ARN: "), new JLabel("Description: "), new JLabel("Status "), new JLabel("Status Message") };

	public CustomVPCLink() {
		super();
	}

	public CustomVPCLink(VpcLink vpcLink) {
		this.vpcLink = vpcLink;
	}

	@Override
	public String getDescription() {
		return vpcLink.getDescription();
	}

	@Override
	public String getId() {
		return vpcLink.getId();
	}

	@Override
	public String getName() {
		return vpcLink.getName();
	}

	@Override
	public String getStatus() {
		return vpcLink.getStatus();
	}

	@Override
	public String getStatusMessage() {
		return vpcLink.getStatusMessage();
	}

	@Override
	public Map<String, String> getTags() {
		return vpcLink.getTags();
	}

	@Override
	public List<String> getTargetArns() {
		return vpcLink.getTargetArns();
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Settings" };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return vpcLinksTableColumnHeaders;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { objectNickName + " Settings" };
		return menus;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { objectNickName + " Settings" };
		return menus;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("VPCLinks");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("VPCLink");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getName());
		summaryData.add(getId());
		summaryData.add(getTargetArns().get(0).split("/")[2]);
		summaryData.add(getDescription());
		summaryData.add(getStatus());
		summaryData.add(getStatusMessage());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getName());
		summaryData.add(getId());
		summaryData.add(getTargetArns().get(0).split("/")[2]);
		summaryData.add(getDescription());
		summaryData.add(getStatus());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		ArrayList<ArrayList<Object>> tagsData = new ArrayList<ArrayList<Object>>();
		Map<String, String> tags = getTags();
		if (tags == null) {
			ArrayList<Object> tagData = new ArrayList<Object>();
			tagData.add("-");
			tagData.add("-");
			tagsData.add(tagData);
		} else {
			Iterator<Map.Entry<String, String>> itr = tags.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, String> entry = itr.next();
				ArrayList<Object> tagData = new ArrayList<Object>();
				tagData.add(entry.getKey());
				tagData.add(entry.getValue());
				tagsData.add(tagData);
			}
		}
		return tagsData;
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
		return retriveVPCLinks(account, true, appFilter);
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
		String[] listPansIdentifiers = { objectNickName + "Settings" };
		String[] listPaneHeaders = { "Settings" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "Settings" };
		String[] listPaneHeaders = { "VPC Link Settings" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getObjectAWSID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectName() {
		return getName();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { "Tags" } };
		String[][][] columnHeaders0 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "Settings", "ListInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Settings for " + getName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(vpcLinkOverviewHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "Tags" } };
		String[][] tablePaneHeaders = { { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "Tags" } };
		String[][] tablePaneHeaders = { { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTreeNodeLeafText() {
		return getName();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(objectNickName + " Settings")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " SETTINGS")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		overviewPanel.getDetailesData(object, object.getAccount(), vpcLinkOverviewHeaderLabels, paneName);
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
		CustomTableViewInternalFrame theFrame = null;
		try {
			theFrame = new CustomTableViewInternalFrame(getpropertiesPaneTitle(), UtilMethodsFactory.generateEC2ObjectPropertiesPane(customAWSObject, jScrollableDesktopPan));
			UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(getpropertiesPaneTitle(), jScrollableDesktopPan, theFrame);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(theFrame, "This " + objectNickName + " is Alredy Deleted", objectNickName + " Gone", JOptionPane.WARNING_MESSAGE);
		}
	}

	private static ArrayList<CustomVPCLink> retriveVPCLinks(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomVPCLink> vpcLinks = new ArrayList<CustomVPCLink>();
		List<VpcLink> vplLinksList = new ArrayList<>();
		AmazonApiGateway apiGatWayClient;
		try {
			apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
					.withRegion(account.getAccontRegionObject().getName()).build();
			GetVpcLinksResult vlr;
			String position = null;
			do {
				vlr = apiGatWayClient.getVpcLinks(new GetVpcLinksRequest().withPosition(position));
				vplLinksList.addAll(vlr.getItems());
				position = vlr.getPosition();
			} while (position != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < vplLinksList.size(); i++) {
			if (appFilter != null) {
				if (filtered) {
					if (vplLinksList.get(i).getName().matches(appFilter)) {
						vpcLinks.add(new CustomVPCLink(vplLinksList.get(i)));
					}
				} else {
					vpcLinks.add(new CustomVPCLink(vplLinksList.get(i)));
				}
			} else {
				if (filtered) {
					if (vplLinksList.get(i).getName().matches(UtilMethodsFactory.getMatchString(account))) {
						vpcLinks.add(new CustomVPCLink(vplLinksList.get(i)));
					}
				} else {
					vpcLinks.add(new CustomVPCLink(vplLinksList.get(i)));
				}
			}
		}
		return vpcLinks;
	}
}
