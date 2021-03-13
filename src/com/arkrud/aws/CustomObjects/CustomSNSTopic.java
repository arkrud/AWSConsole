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
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicResult;
import com.amazonaws.services.sns.model.ListTagsForResourceRequest;
import com.amazonaws.services.sns.model.ListTagsForResourceResult;
import com.amazonaws.services.sns.model.ListTopicsRequest;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.Topic;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomSNSTopic extends Topic implements CustomAWSObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Topic topic;
	private AWSAccount account;
	private String objectNickName = "Topic";
	private String[] snsTopicTableColumnHeaders = { "Name", "ARN", "Type", "Owner" };
	private String[] snsTopicSubscriptionsTableColumnHeaders = { "ID", "Endpoint", "Status", "Protocol","Owner" };
	private JLabel[] snsTopicOverviewHeaderLabels = { new JLabel("Name: "), new JLabel("ARN: "), new JLabel("Type: "), new JLabel("Owner: ") };

	public CustomSNSTopic() {
		super();
	}

	public CustomSNSTopic(Topic topic) {
		this.topic = topic;
	}

	@Override
	public String getTopicArn() {
		return topic.getTopicArn();
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Detailes" };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return snsTopicTableColumnHeaders;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { "Select single " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { objectNickName + " Detailes" };
		return menus;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("topic-big");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("topic");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getTopicArn().split(":")[5]);
		summaryData.add(getTopicArn());
		summaryData.add(getSNSTopicType());
		summaryData.add(getTopicArn().split(":")[4]);
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getTopicArn().split(":")[5]);
		summaryData.add(getTopicArn());
		summaryData.add(getSNSTopicType());
		summaryData.add(getTopicArn().split(":")[4]);
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		ArrayList<ArrayList<Object>> tagsData = new ArrayList<ArrayList<Object>>();
		AmazonSNS amazonSNSClient = AmazonSNSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		ListTagsForResourceResult result = amazonSNSClient.listTagsForResource(new ListTagsForResourceRequest().withResourceArn(getTopicArn()));
		for (int i = 0; i < result.getTags().size(); i++) {
			ArrayList<Object> tagData = new ArrayList<Object>();
			tagData.add(result.getTags().get(i).getKey());
			tagData.add(result.getTags().get(i).getValue());
			tagsData.add(tagData);
		}
		return tagsData;
	}

	@Override
	public String getDocumentTabHeaders(String paneIdentifier) {
		String[] documentPaneIdentifiers = { "SNSTopicAccessPolicy", "SNSTopicEffectiveDeliveryPolicy" };
		String[] documentPaneIPaneHeaders = { "SNS Topic Access Policy", "SNS Topic Effective Delivery Policy" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneIPaneHeaders);
	}

	@Override
	public String getDocumentTabToolTips(String paneIdentifier) {
		String[] documentPaneIdentifiers = { "SNSTopicAccessPolicy", "SNSTopicEffectiveDeliveryPolicy" };
		String[] documentPaneIPaneHeaders = { "SNS Topic Access Policy", "SNS Topic Effective Delivery Policy" };
		return UtilMethodsFactory.getListTabsData(paneIdentifier, documentPaneIdentifiers, documentPaneIPaneHeaders);
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveParameters(account, true, appFilter);
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		events.add(KeyEvent.VK_2);
		events.add(KeyEvent.VK_3);
		events.add(KeyEvent.VK_4);
		return events;
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "Detailes" };
		String[] listPaneHeaders = { "Detailes" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "Detailes" };
		String[] listPaneHeaders = { "Topic Detailes" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getObjectAWSID() {
		return getTopicArn().split(":")[5];
	}

	@Override
	public String getObjectName() {
		return getTopicArn().split(":")[5];
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { "TopicSubscriptions" } };
		String[][][] columnHeaders0 = { { snsTopicSubscriptionsTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		String[][] dataFlags1 = { { "Tags" } };
		String[][][] columnHeaders1 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags1, columnHeaders1);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "Detailes", "ListInfoPane");
		propertiesPaneTabs.put("SNSTopicAccessPolicy", "DocumentInfoPane");
		propertiesPaneTabs.put("SNSTopicEffectiveDeliveryPolicy", "DocumentInfoPane");
		propertiesPaneTabs.put("TopicSubscriptions", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Detailes for " + getTopicArn().split(":")[5] + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(snsTopicOverviewHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { {"TopicSubscriptions"}, { "Tags" } };
		String[][] tablePaneHeaders = { {"TopicSubscriptions"}, { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { {"TopicSubscriptions"}, { "Tags" } };
		String[][] tablePaneHeaders = { {objectNickName + " Subscriptions"}, { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTreeNodeLeafText() {
		return getObjectName();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(objectNickName + " Detailes")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " DETAILES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		overviewPanel.getDetailesData(object, object.getAccount(), snsTopicOverviewHeaderLabels, paneName);
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

	private Map<String, String> getSNSTopicAttributes() {
		return AmazonSNSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName()).build()
				.getTopicAttributes(getTopicArn()).getAttributes();
	}

	private String getSNSTopicType() {
		Iterator<Map.Entry<String, String>> itr = getSNSTopicAttributes().entrySet().iterator();
		String type = "";
		while (itr.hasNext()) {
			Map.Entry<String, String> entry = itr.next();
			if (entry.getKey().equals("FifoTopic") && entry.getValue().equals("true")) {
				type = "Fifo Topic";
				break;
			} else {
				type = "Standard";
			}
		}
		return type;
	}

	public String getSNSTopicAccessPolicy() {
		Iterator<Map.Entry<String, String>> itr = getSNSTopicAttributes().entrySet().iterator();
		String accessPolicy = "";
		while (itr.hasNext()) {
			Map.Entry<String, String> entry = itr.next();
			if (entry.getKey().equals("Policy")) {
				accessPolicy = entry.getValue();
				break;
			}
		}
		return accessPolicy;
	}
	
	public String getSNSTopicEffectiveDeliveryPolicy() {
		Iterator<Map.Entry<String, String>> itr = getSNSTopicAttributes().entrySet().iterator();
		String effectiveDeliveryPolicy = "";
		while (itr.hasNext()) {
			Map.Entry<String, String> entry = itr.next();
			if (entry.getKey().equals("EffectiveDeliveryPolicy")) {
				effectiveDeliveryPolicy = entry.getValue();
				break;
			}
		}
		return effectiveDeliveryPolicy;
	}
	
	public ArrayList<ArrayList<Object>> getSNSTopicSubscriptions() {
		ArrayList<ArrayList<Object>> snsTopicSubscriptions = new ArrayList<ArrayList<Object>>();
		AmazonSNS amazonSNSClient = AmazonSNSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		ListSubscriptionsByTopicResult result = amazonSNSClient.listSubscriptionsByTopic(new ListSubscriptionsByTopicRequest().withTopicArn(getTopicArn()));
		for (int i = 0; i < result.getSubscriptions().size(); i++) {
			ArrayList<Object> historyData = new ArrayList<Object>();
			historyData.add(result.getSubscriptions().get(i).getSubscriptionArn().split(":")[6]);
			historyData.add(result.getSubscriptions().get(i).getEndpoint());
			historyData.add(getSNSTopicSubscriptionsConfirnmationStatus(result.getSubscriptions().get(i).getSubscriptionArn()));
			historyData.add(result.getSubscriptions().get(i).getProtocol());
			historyData.add(result.getSubscriptions().get(i).getOwner());
			snsTopicSubscriptions.add(historyData);
		}
		return snsTopicSubscriptions;
	}
	
	private String getSNSTopicSubscriptionsConfirnmationStatus(String subscriptionARN) {
		Iterator<Map.Entry<String, String>> itr = getSNSTopicConfirmationsAttributes(subscriptionARN).entrySet().iterator();
		String status = "";
		while (itr.hasNext()) {
			Map.Entry<String, String> entry = itr.next();
			if (entry.getKey().equals("PendingConfirmation") && entry.getValue().equals("false")) {
				status = "Confirmed";
				break;
			} else {
				status = "Pending";
			}
		}
		return status;
	}

	private Map<String, String> getSNSTopicConfirmationsAttributes(String subscriptionARN) {
		return AmazonSNSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret()))).withRegion(account.getAccontRegionObject().getName()).build()
				.getSubscriptionAttributes(subscriptionARN).getAttributes();
	}
	
	
	private ArrayList<CustomSNSTopic> retriveParameters(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomSNSTopic> customSNSTopics = new ArrayList<CustomSNSTopic>();
		ArrayList<Topic> topics = new ArrayList<Topic>();
		AmazonSNS amazonSNSClient = AmazonSNSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account.getAccountKey(), account.getAccountSecret())))
				.withRegion(account.getAccontRegionObject().getName()).build();
		try {
			String nextToken = null;
			ListTopicsResult result;
			do {
				result = amazonSNSClient.listTopics(new ListTopicsRequest().withNextToken(nextToken));
				topics.addAll(result.getTopics());
				nextToken = result.getNextToken();
			} while (nextToken != null);
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < topics.size(); i++) {
			if (appFilter != null) {
				if (filtered) {
					if (topics.get(i).getTopicArn().matches(appFilter)) {
						customSNSTopics.add(new CustomSNSTopic(topics.get(i)));
					}
				} else {
					topics.add(new CustomSNSTopic(topics.get(i)));
				}
			} else {
				if (filtered) {
					if (topics.get(i).getTopicArn().matches(UtilMethodsFactory.getMatchString(account))) {
						topics.add(new CustomSNSTopic(topics.get(i)));
					}
				} else {
					topics.add(new CustomSNSTopic(topics.get(i)));
				}
			}
		}
		return customSNSTopics;
	}
}
