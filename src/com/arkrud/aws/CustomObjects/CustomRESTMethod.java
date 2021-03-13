package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.services.apigateway.model.Integration;
import com.amazonaws.services.apigateway.model.Method;
import com.amazonaws.services.apigateway.model.MethodResponse;
import com.amazonaws.services.apigateway.model.Resource;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomRESTMethod extends Method implements CustomAWSObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Method method;
	private String objectNickName = "RESTMethod";
	private AWSAccount account;
	private JLabel[] methodSettingsHeaderLabels = { new JLabel("REST Method: "), new JLabel("Operation Name: "), new JLabel("Request Validator ID: "), new JLabel("Authorization ID: "), new JLabel("Authorization Type: ") };

	public CustomRESTMethod() {
		super();
	}

	public CustomRESTMethod(Method method) {
		this.method = method;
	}

	@Override
	public Boolean getApiKeyRequired() {
		return method.getApiKeyRequired();
	}

	@Override
	public List<String> getAuthorizationScopes() {
		return method.getAuthorizationScopes();
	}

	@Override
	public String getAuthorizationType() {
		return method.getAuthorizationType();
	}

	@Override
	public String getAuthorizerId() {
		return method.getAuthorizerId();
	}

	@Override
	public String getHttpMethod() {
		return method.getHttpMethod();
	}

	@Override
	public Integration getMethodIntegration() {
		return method.getMethodIntegration();
	}

	@Override
	public Map<String, MethodResponse> getMethodResponses() {
		return method.getMethodResponses();
	}

	@Override
	public String getOperationName() {
		return method.getOperationName();
	}

	@Override
	public Map<String, String> getRequestModels() {
		return method.getRequestModels();
	}

	@Override
	public Map<String, Boolean> getRequestParameters() {
		return method.getRequestParameters();
	}

	@Override
	public String getRequestValidatorId() {
		return method.getRequestValidatorId();
	}

	@Override
	public Boolean isApiKeyRequired() {
		return method.isApiKeyRequired();
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
		return account;
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
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getHttpMethod());
		summaryData.add(getOperationName());
		summaryData.add(getRequestValidatorId());
		summaryData.add(getApiKeyRequired());
		summaryData.add(getAuthorizationType());
		summaryData.add(getAuthorizerId());
		return summaryData;
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
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		return events;
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "General" };
		String[] listPaneHeaders = { "Settings" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "General" };
		String[] listPaneHeaders = { "Method General Settings" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getObjectAWSID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectName() {
		return getHttpMethod();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "General", "ListInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Settings for " + getObjectName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(methodSettingsHeaderLabels.length);
		return propertyPanelsFieldsCount;
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
	public String getTreeNodeLeafText() {
		// TODO Auto-generated method stub
		return null;
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
		overviewPanel.getDetailesData(object, object.getAccount(), methodSettingsHeaderLabels, paneName);
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
}
