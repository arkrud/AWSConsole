package com.arkrud.aws.CustomObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.aws.AWSAccount;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public interface CustomAWSObject {
	public String[] defineNodeTreeDropDown();

	public String[] defineTableColumnHeaders();

	public String[] defineTableMultipleSelectionDropDown();

	public String[] defineTableSingleSelectionDropDown();

	public AWSAccount getAccount();

	public ImageIcon getAssociatedContainerImage();

	public ImageIcon getAssociatedImage();

	public ArrayList<Object> getAWSDetailesPaneData();

	public ArrayList<Object> getAWSObjectSummaryData();

	public ArrayList<ArrayList<Object>> getAWSObjectTagsData();

	public String getDocumentTabHeaders(String paneIdentifier);

	public String getDocumentTabToolTips(String paneIdentifier);

	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter);

	public default ArrayList<?> getFilteredAWSObjects(CustomIAMInstanceProfile customIAMInstanceProfile) {
		return null;
	}

	public List<Integer> getkeyEvents();

	public String getListTabHeaders(String tableIdentifier);

	public String getListTabToolTips(String tableIdentifier);

	public String getObjectAWSID();

	public String getObjectName();

	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams();

	public LinkedHashMap<String, String> getpropertiesPaneTabs();

	public String getpropertiesPaneTitle();

	public ArrayList<Integer> getPropertyPanelsFieldsCount();

	public String getTableTabHeaders(String tableIdentifier);

	public String getTableTabToolTips(String tableIdentifier);

	public String getTreeNodeLeafText();

	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString);

	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString);

	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName);

	public void remove();

	public void setAccount(AWSAccount account);

	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan);
}
