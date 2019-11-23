package com.arkrud.aws.CustomObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.StaticFactories.EC2Common;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomEC2KeyPair extends KeyPairInfo implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private KeyPairInfo keyPairInfo;
	private AWSAccount account;
	private String[] keyPairsTableColumnHeaders = { "Key pair name", "Fingerprint" };
	private String action = "Import";
	private String objectNickName = "KeyPair";

	public CustomEC2KeyPair(KeyPairInfo keyPairInfo) {
		this.keyPairInfo = keyPairInfo;
	}

	public CustomEC2KeyPair() {
		super();
	}

	@Override
	public String getKeyFingerprint() {
		return keyPairInfo.getKeyFingerprint();
	}

	@Override
	public String getKeyName() {
		return keyPairInfo.getKeyName();
	}

	public KeyPairInfo getKeyPairInfo() {
		return keyPairInfo;
	}

	public void setKeyPairInfo(KeyPairInfo keyPairInfo) {
		this.keyPairInfo = keyPairInfo;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	@Override
	public String getObjectName() {
		return getKeyName();
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return keyPairsTableColumnHeaders;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { action + " " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { action + " " + objectNickName + "(s)" };
		return menus;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { action + " " + objectNickName };
		return menus;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getKeyFingerprint());
		return summaryData;
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
		return UtilMethodsFactory.populateInterfaceImages().get("keypair");
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("keypair-big");
	}

	@Override
	public String getTreeNodeLeafText() {
		return getKeyName();
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return getKeyPairs(account, appFilter);
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
	public List<Integer> getkeyEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(action.toUpperCase() + " " + objectNickName.toUpperCase())) {
			// UtilMethods.removeAction(account, object, node, tree, dash, action, objectNickName);
		}
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(action)) {
			// UtilMethods.removeAction(table, action, objectNickName);
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

	@SuppressWarnings("deprecation")
	private ArrayList<CustomEC2KeyPair> getKeyPairs(AWSAccount account, String appFilter) {
		ArrayList<CustomEC2KeyPair> keyPairs = new ArrayList<CustomEC2KeyPair>();
		DescribeKeyPairsRequest request = new DescribeKeyPairsRequest();
		DescribeKeyPairsResult result = null;
		try {
			AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			client.setEndpoint("ec2." + account.getAccountRegion() + ".amazonaws.com");
			result = client.describeKeyPairs(request);
			Iterator<KeyPairInfo> kpIterator = result.getKeyPairs().iterator();
			while (kpIterator.hasNext()) {
				KeyPairInfo keyPairInfo = kpIterator.next();
				if (appFilter != null) {
					if (keyPairInfo.getKeyName().matches(appFilter)) {
						keyPairs.add(new CustomEC2KeyPair(keyPairInfo));
					}
				} else {
					if (keyPairInfo.getKeyName().matches(UtilMethodsFactory.getMatchString(account))) {
						keyPairs.add(new CustomEC2KeyPair(keyPairInfo));
					}
				}
			}
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
		}
		return keyPairs;
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
	
	private void importKeyIntoApp () {
		
	}
	
}
