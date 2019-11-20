package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.autoscaling.model.EnabledMetric;
import com.amazonaws.services.autoscaling.model.Instance;
import com.amazonaws.services.autoscaling.model.LaunchTemplateSpecification;
import com.amazonaws.services.autoscaling.model.SuspendedProcess;
import com.amazonaws.services.autoscaling.model.TagDescription;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomEC2Asg extends AutoScalingGroup implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private AWSAccount account;
	private String objectNickName = "AutoScaling Group";
	private String action = "Delete";
	private AutoScalingGroup autoScalingGroup;
	private JLabel[] ec2AsgDetailesHeaderLabels = { new JLabel("Launch Configuration"), new JLabel("Service-Linked Role"), new JLabel("Load Balancers:"), new JLabel("Target Groups:"), new JLabel("Desired:"), new JLabel("Min:"), new JLabel("Max:"),
			new JLabel("Health Check Type:"), new JLabel("Health Check Grace period:"), new JLabel("Termination Policies:"), new JLabel("Creation Time:"), new JLabel("Availability Zone(s):"), new JLabel("Subnets:"), new JLabel("Default Cooldown:") };

	public CustomEC2Asg(AutoScalingGroup autoScalingGroup) {
		this.autoScalingGroup = autoScalingGroup;
	}

	public CustomEC2Asg() {
		super();
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	public String getLoadBalancerNamesString() {
		return autoScalingGroup.getLoadBalancerNames().stream().collect(Collectors.joining(", "));
	}

	public String getTerminationPoliciesNames() {
		return autoScalingGroup.getTerminationPolicies().stream().collect(Collectors.joining(", "));
	}

	public String getAvailabilityZonesIDs() {
		return autoScalingGroup.getAvailabilityZones().stream().collect(Collectors.joining(", "));
	}

	public String getInstanceIDs() {
		List<String> ids = new ArrayList<String>();
		Iterator<Instance> itr = autoScalingGroup.getInstances().iterator();
		while (itr.hasNext()) {
			ids.add(itr.next().getInstanceId());
		}
		return ids.stream().collect(Collectors.joining(", "));
	}

	@Override
	public String getAutoScalingGroupARN() {
		return autoScalingGroup.getAutoScalingGroupARN();
	}

	@Override
	public String getAutoScalingGroupName() {
		return autoScalingGroup.getAutoScalingGroupName();
	}

	@Override
	public List<String> getAvailabilityZones() {
		return autoScalingGroup.getAvailabilityZones();
	}

	@Override
	public Date getCreatedTime() {
		return autoScalingGroup.getCreatedTime();
	}

	@Override
	public Integer getDefaultCooldown() {
		return autoScalingGroup.getDefaultCooldown();
	}

	@Override
	public Integer getDesiredCapacity() {
		return autoScalingGroup.getDesiredCapacity();
	}

	@Override
	public List<EnabledMetric> getEnabledMetrics() {
		return autoScalingGroup.getEnabledMetrics();
	}

	@Override
	public Integer getHealthCheckGracePeriod() {
		return autoScalingGroup.getHealthCheckGracePeriod();
	}

	@Override
	public String getHealthCheckType() {
		return autoScalingGroup.getHealthCheckType();
	}

	@Override
	public List<Instance> getInstances() {
		return autoScalingGroup.getInstances();
	}

	@Override
	public String getLaunchConfigurationName() {
		return autoScalingGroup.getLaunchConfigurationName();
	}

	@Override
	public List<String> getLoadBalancerNames() {
		return autoScalingGroup.getLoadBalancerNames();
	}

	private List<CustomEC2ELB> getLoadBalancers() {
		List<CustomEC2ELB> elbs = new ArrayList<CustomEC2ELB>();
		Iterator<String> elbNamesIteartor = getLoadBalancerNames().iterator();
		while (elbNamesIteartor.hasNext()) {
			String elbName = elbNamesIteartor.next();
			elbs.add(new CustomEC2ELB(getAccount(), elbName, false, null));
		}
		return elbs;
	}

	@Override
	public Integer getMaxSize() {
		return autoScalingGroup.getMaxSize();
	}

	@Override
	public Integer getMinSize() {
		return autoScalingGroup.getMinSize();
	}

	@Override
	public Boolean getNewInstancesProtectedFromScaleIn() {
		return autoScalingGroup.getNewInstancesProtectedFromScaleIn();
	}

	@Override
	public String getPlacementGroup() {
		return autoScalingGroup.getPlacementGroup();
	}

	@Override
	public String getStatus() {
		return autoScalingGroup.getStatus();
	}

	@Override
	public List<SuspendedProcess> getSuspendedProcesses() {
		return autoScalingGroup.getSuspendedProcesses();
	}

	@Override
	public List<TagDescription> getTags() {
		return autoScalingGroup.getTags();
	}

	@Override
	public List<String> getTargetGroupARNs() {
		List<String> tg = new ArrayList<String>();
		if (autoScalingGroup.getTargetGroupARNs().size() == 0) {
			tg.add("");
			return tg;
		} else {
			return autoScalingGroup.getTargetGroupARNs();
		}
	}

	@Override
	public List<String> getTerminationPolicies() {
		return autoScalingGroup.getTerminationPolicies();
	}

	@Override
	public String getVPCZoneIdentifier() {
		return autoScalingGroup.getVPCZoneIdentifier().replace(",", ", ");
	}

	@Override
	public Boolean isNewInstancesProtectedFromScaleIn() {
		return autoScalingGroup.isNewInstancesProtectedFromScaleIn();
	}

	@Override
	public String getServiceLinkedRoleARN() {
		return autoScalingGroup.getServiceLinkedRoleARN();
	}

	@Override
	public LaunchTemplateSpecification getLaunchTemplate() {
		return autoScalingGroup.getLaunchTemplate();
	}

	@Override
	public String getObjectName() {
		return getAutoScalingGroupName();
	}

	@Override
	public String[] defineTableColumnHeaders() {
		String[] ec2AsgColumnHeaders = { "ASG Name", "Launch Configuration", "Online Instances", "Desired", "Min", "Max", "Availability Zones", "Load Balancers", "Default Cooldown", "Health Check Type", "Health Check Grace Period",
				"Termination Polices", "Subnets", "Creation Time", "Instance Protection" };
		return ec2AsgColumnHeaders;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName + "(s)" };
		return menus;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName + "(s)" };
		return menus;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties", UtilMethodsFactory.upperCaseFirst(action) + " " + objectNickName };
		return menus;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getInstanceIDs());
		summaryData.add(getLaunchConfigurationName());
		summaryData.add(getDesiredCapacity());
		summaryData.add(getMinSize());
		summaryData.add(getMaxSize());
		summaryData.add(getAvailabilityZonesIDs());
		summaryData.add(getLoadBalancerNamesString());
		summaryData.add(getDefaultCooldown());
		summaryData.add(getHealthCheckType());
		summaryData.add(getHealthCheckGracePeriod());
		summaryData.add(getTerminationPoliciesNames());
		summaryData.add(getVPCZoneIdentifier());
		summaryData.add(getCreatedTime());
		summaryData.add(getNewInstancesProtectedFromScaleIn());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getLaunchConfigurationName());
		summaryData.add(getServiceLinkedRoleARN());
		summaryData.add(getLoadBalancers());
		summaryData.add(getTargetGroupARNs());
		summaryData.add(getDesiredCapacity());
		summaryData.add(getMinSize());
		summaryData.add(getMaxSize());
		summaryData.add(getHealthCheckType());
		summaryData.add(getHealthCheckGracePeriod());
		summaryData.add(getTerminationPolicies());
		summaryData.add(getCreatedTime());
		summaryData.add(getAvailabilityZones());
		summaryData.add(Arrays.stream(getVPCZoneIdentifier().split(",")).collect(Collectors.toList()));
		summaryData.add(getDefaultCooldown());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return UtilMethodsFactory.getAWSObjectTagsDEscriptionData(getTags().iterator());
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("asg");
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("asg-big");
	}

	@Override
	public String getTreeNodeLeafText() {
		return getAutoScalingGroupName();
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveASGs(account, appFilter);
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
		return objectNickName + " Properties for " + getAutoScalingGroupName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public String getObjectAWSID() {
		return getAutoScalingGroupName();
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
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		return events;
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		account = ((CustomTreeContainer) parentNode.getUserObject()).getAccount();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " PROPERTIES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		} else if (actionString.equals(action.toUpperCase() + " " + objectNickName.toUpperCase())) {
		}
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains("Delete")) {
		} else if (actionString.contains(objectNickName + " Properties")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
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
	public String getListTabHeaders(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + " Details" };
		String[] tablePaneToolTips = { "Detailes" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + " Details" };
		String[] tablePaneToolTips = { objectNickName + " Properties" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals(objectNickName + " Details")) {
			overviewPanel.getDetailesData(object, object.getAccount(), ec2AsgDetailesHeaderLabels, paneName);
		}
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		CustomTableViewInternalFrame theFrame = new CustomTableViewInternalFrame(getpropertiesPaneTitle(), UtilMethodsFactory.generateEC2ObjectPropertiesPane(customAWSObject, jScrollableDesktopPan));
		UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(getpropertiesPaneTitle(), jScrollableDesktopPan, theFrame);
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

	private ArrayList<CustomEC2Asg> retriveASGs(AWSAccount account, String appFilter) {
		ArrayList<CustomEC2Asg> customEC2ASGs = new ArrayList<CustomEC2Asg>();
		@SuppressWarnings("deprecation")
		AmazonAutoScalingClient amazonAutoScalingClient = new AmazonAutoScalingClient(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		amazonAutoScalingClient.setRegion(account.getAccontRegionObject());
		DescribeAutoScalingGroupsRequest request = new DescribeAutoScalingGroupsRequest();
		DescribeAutoScalingGroupsResult result = amazonAutoScalingClient.describeAutoScalingGroups(request);
		List<AutoScalingGroup> asgs = result.getAutoScalingGroups();
		Iterator<AutoScalingGroup> asgsIterator = asgs.iterator();
		while (asgsIterator.hasNext()) {
			AutoScalingGroup asg = asgsIterator.next();
			if (appFilter != null) {
				if (asg.getAutoScalingGroupName().matches(appFilter)) {
					CustomEC2Asg customAsg = new CustomEC2Asg(asg);
					customEC2ASGs.add(customAsg);
				}
			} else {
				if (asg.getAutoScalingGroupName().matches(UtilMethodsFactory.getMatchString(account))) {
					CustomEC2Asg customAsg = new CustomEC2Asg(asg);
					customEC2ASGs.add(customAsg);
				}
			}
		}
		return customEC2ASGs;
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
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(ec2AsgDetailesHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}
}
