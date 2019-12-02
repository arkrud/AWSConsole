package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsForMetricRequest;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsForMetricResult;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.DimensionFilter;
import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.DescribeAddressesRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesResult;
import com.amazonaws.services.ec2.model.DescribeClassicLinkInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeClassicLinkInstancesResult;
import com.amazonaws.services.ec2.model.DescribeInstanceAttributeRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceAttributeResult;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.IamInstanceProfile;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceBlockDeviceMapping;
import com.amazonaws.services.ec2.model.InstanceNetworkInterface;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.InstanceStatusEvent;
import com.amazonaws.services.ec2.model.Monitoring;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.ProductCode;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StateReason;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.LinkLikeButton;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.StaticFactories.EC2Common;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomEC2Instance extends Instance implements CustomAWSObject {
	private static final long serialVersionUID = 1L;
	private AWSAccount account;
	private Instance instance;
	private String objectNickName = "Instance";
	private JLabel[] ec2InstanceGeneralLabels = { new JLabel("Instance ID: "), new JLabel("Instance state: "), new JLabel("Instance type: "), new JLabel("Private DNS: "), new JLabel("Private IPs: "), new JLabel("Public DNS: "),
			new JLabel("Public IP: "), new JLabel("Elastic IPs: "), new JLabel("VPC ID: "), new JLabel("Subnet ID: "), new JLabel("Availability zone: "), new JLabel("Launch time: ") };
	private JLabel[] ec2InstanceStorageLabels = { new JLabel("EBS-optimized: "), new JLabel("Root device type: "), new JLabel("Root device: "), new JLabel("Block devices volumes: "), new JLabel("AMI ID: "), new JLabel("RAM disk ID: "),
			new JLabel("AMI launch index: ") };
	private JLabel[] ec2InstanceSecurityLabels = { new JLabel("Security groups: "), new JLabel("IAM role: "), new JLabel("Key pair name: "), new JLabel("Owner: "), new JLabel("Reservation: "), new JLabel("Termination protection: "),
			new JLabel("State transition reason: ") };
	private JLabel[] ec2InstanceAdvancedLabels = { new JLabel("Secondary private IPs: "), new JLabel("Network interfaces: "), new JLabel("Source/dest. check: "), new JLabel("ClassicLink: "), new JLabel("Scheduled events: "),
			new JLabel("Lifecycle: "), new JLabel("Monitoring: "), new JLabel("Alarm status: "), new JLabel("Platform: "), new JLabel("Kernel ID: "), new JLabel("Placement group: "), new JLabel("Virtualization: "), new JLabel("Tenancy: "),
			new JLabel("Host ID: "), new JLabel("Affinity: ") };
	public String[] instancesTableColumnHeaders = { "Instance Name", "Instance ID", "Instance Type", "Availability Zone", "Instance State", "Alarm Status", "Launch Time" };

	public CustomEC2Instance(Instance instance) {
		this.instance = instance;
	}

	public CustomEC2Instance(AWSAccount account, String id) {
		this.instance = retriveOneEC2Instance(account, id);
		this.account = account;
	}

	public CustomEC2Instance(AWSAccount account, String id, boolean filtered, String appFilter) {
		List<CustomEC2Instance> object = retriveEC2Instances(account, filtered, appFilter);
		Iterator<CustomEC2Instance> iterator = object.iterator();
		while (iterator.hasNext()) {
			CustomEC2Instance instance = iterator.next();
			if (instance.getInstanceId().equals(id)) {
				this.instance = instance;
				this.account = account;
			}
		}
	}

	public CustomEC2Instance() {
		super();
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties" };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return instancesTableColumnHeaders;
	}

	@Override
	public String[] defineTableMultipleSelectionDropDown() {
		String[] menus = { "Select single " + objectNickName };
		return menus;
	}

	@Override
	public String[] defineTableSingleSelectionDropDown() {
		String[] menus = { objectNickName + " Properties" };
		return menus;
	}

	@Override
	public AWSAccount getAccount() {
		return account;
	}

	@Override
	public Integer getAmiLaunchIndex() {
		return instance.getAmiLaunchIndex();
	}

	@Override
	public String getArchitecture() {
		return instance.getArchitecture();
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("instances");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("instance");
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		Subnet subnet = EC2Common.retriveVPCSubnets(account, getSubnetId());
		String subnetID = "";
		if (subnet != null) {
			subnetID = getSubnetId();
		} else {
			subnetID = " - ";
		}
		String vpcID = "none";
		if (getVpcId() != null) {
			vpcID = getVpcId();
		} else {
			vpcID = "none";
		}
		summaryData.add(getInstanceId());
		summaryData.add(getState().getName());
		summaryData.add(getInstanceType());
		summaryData.add(getPrivateDnsName());
		summaryData.add(getPrivateIpAddress());
		summaryData.add(getPublicDnsName());
		summaryData.add(getPublicIpAddress());
		summaryData.add(getElasicAddressess(account, getInstanceId()).get(0));
		summaryData.add(vpcID);
		summaryData.add(subnetID);
		summaryData.add(getPlacement().getAvailabilityZone());
		summaryData.add(getLaunchTime());
		return summaryData;
	}

	public ArrayList<Object> getInstanceStoragePaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		String ramDiskId = getRamdiskId();
		if (ramDiskId == null) {
			ramDiskId = "-";
		}
		summaryData.add(getEbsOptimized());
		summaryData.add(getRootDeviceType());
		summaryData.add(getRootDeviceName());
		summaryData.add(getBlockDeviceMappings());
		AWSAccount activeAccount;
		if (account.getAccountAlias().equals("AspenDev")) {
			activeAccount = account;
		} else {
			activeAccount = new AWSAccount();
			activeAccount.setAccountAlias("AspenDev");
		}
		summaryData.add(new CustomEC2AMI(activeAccount, getImageId()));
		summaryData.add(ramDiskId);
		summaryData.add(getAmiLaunchIndex());
		return summaryData;
	}

	public ArrayList<Object> getInstanceSecurityPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		String stateTransitionReason = getStateTransitionReason();
		if (stateTransitionReason == null || stateTransitionReason.length() == 0) {
			stateTransitionReason = "-";
		}
		summaryData.add(getSecurityGroups());
		summaryData.add(getIamInstanceProfile().getArn());
		summaryData.add(getKeyName());
		summaryData.add(retriveEC2InstanceReservation(account, getInstanceId()).getOwnerId());
		summaryData.add(retriveEC2InstanceReservation(account, getInstanceId()).getReservationId());
		summaryData.add(findTerminationProtection(account, getInstanceId()));
		summaryData.add(stateTransitionReason);
		return summaryData;
	}

	public ArrayList<Object> getInstanceAdvancedPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		String groupName = getPlacement().getGroupName();
		if (groupName.length() == 0) {
			groupName = "-";
		}
		String lifeCycle = getInstanceLifecycle();
		if (lifeCycle == null) {
			lifeCycle = "-";
		}
		String platform = getPlatform();
		if (platform == null) {
			platform = "-";
		}
		String kernelId = getKernelId();
		if (kernelId == null) {
			kernelId = "-";
		}
		String hostId = getPlacement().getHostId();
		if (hostId == null) {
			hostId = "-";
		}
		summaryData.add(getSecondaryPublicIPs(account, getInstanceId()));
		summaryData.add(getNetworkInterfaces());
		summaryData.add(getSourceDestCheck());
		summaryData.add(isClassicLincUsed(account, getInstanceId()));
		summaryData.add(getInstanceScheduledEents(account, getInstanceId()));
		summaryData.add(lifeCycle);
		summaryData.add(getMonitoring().getState());
		//summaryData.add(getAlarmStateForInstance(getAccount(), getInstanceId()));
		summaryData.add(" - ");
		summaryData.add(platform);
		summaryData.add(kernelId);
		summaryData.add(groupName);
		summaryData.add(getVirtualizationType());
		summaryData.add(getPlacement().getTenancy());
		summaryData.add(hostId);
		summaryData.add(getPlacement().getAvailabilityZone());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		LinkLikeButton linkLikeIDButton = new LinkLikeButton(getInstanceId());
		linkLikeIDButton.setName("PanelLinkLikeButton");
		summaryData.add(linkLikeIDButton);
		summaryData.add(getInstanceType());
		summaryData.add(getPlacement().getAvailabilityZone());
		summaryData.add(getState().getName());
		summaryData.add(getAlarmStateForInstance(getAccount(), getInstanceId()));
		summaryData.add(getLaunchTime());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return UtilMethodsFactory.getAWSObjectTagsData(getTags().iterator());
	}

	@Override
	public List<InstanceBlockDeviceMapping> getBlockDeviceMappings() {
		return instance.getBlockDeviceMappings();
	}

	@Override
	public String getClientToken() {
		return instance.getClientToken();
	}

	@Override
	public Boolean getEbsOptimized() {
		return instance.getEbsOptimized();
	}

	@Override
	public Boolean getEnaSupport() {
		return instance.getEnaSupport();
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveEC2Instances(account, true, appFilter);
	}

	@Override
	public String getHypervisor() {
		return instance.getHypervisor();
	}

	@Override
	public IamInstanceProfile getIamInstanceProfile() {
		return instance.getIamInstanceProfile();
	}

	@Override
	public String getImageId() {
		return instance.getImageId();
	}

	public Instance getInstance() {
		return instance;
	}

	@Override
	public String getInstanceId() {
		return instance.getInstanceId();
	}

	@Override
	public String getInstanceLifecycle() {
		return instance.getInstanceLifecycle();
	}

	public String getInstanceName() {
		return UtilMethodsFactory.getEC2ObjectFilterTag(getTags(), "Name");
	}

	@Override
	public String getInstanceType() {
		return instance.getInstanceType();
	}

	@Override
	public String getKernelId() {
		return instance.getKernelId();
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
	public String getKeyName() {
		return instance.getKeyName();
	}

	@Override
	public Date getLaunchTime() {
		return instance.getLaunchTime();
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + "General", objectNickName + "Storage", objectNickName + "Security", objectNickName + "Advanced" };
		String[] tablePaneToolTips = { "General", "Storage", "Security", "Advanced" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] tableIdentifiers = { objectNickName + "General", objectNickName + "Storage", objectNickName + "Security", objectNickName + "Advanced" };
		String[] tablePaneToolTips = { "General Instance Properties", "Instance Storage Properties", "Instance Security Properties", "Instance Advanced Properties" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public Monitoring getMonitoring() {
		return instance.getMonitoring();
	}

	@Override
	public List<InstanceNetworkInterface> getNetworkInterfaces() {
		return instance.getNetworkInterfaces();
	}

	@Override
	public String getObjectAWSID() {
		String id = "";
		try {
			id = instance.getInstanceId();
		} catch (Exception e1) {
			id = objectNickName + " Terminated";
		}
		return id;
	}

	@Override
	public String getObjectName() {
		return getInstanceName();
	}

	@Override
	public Placement getPlacement() {
		return instance.getPlacement();
	}

	@Override
	public String getPlatform() {
		return instance.getPlatform();
	}

	@Override
	public String getPrivateDnsName() {
		return instance.getPrivateDnsName();
	}

	@Override
	public String getPrivateIpAddress() {
		return instance.getPrivateIpAddress();
	}

	@Override
	public List<ProductCode> getProductCodes() {
		return instance.getProductCodes();
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
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "General", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "Storage", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "Security", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "Advanced", "ListInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + UtilMethodsFactory.getEC2ObjectFilterTag(getTags(), "Name") + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public String getPublicDnsName() {
		return instance.getPublicDnsName();
	}

	@Override
	public String getPublicIpAddress() {
		if (instance.getPublicIpAddress() == null) {
			return "-";
		} else {
			return instance.getPublicIpAddress();
		}
	}

	@Override
	public String getRamdiskId() {
		return instance.getRamdiskId();
	}

	@Override
	public String getRootDeviceName() {
		return instance.getRootDeviceName();
	}

	@Override
	public String getRootDeviceType() {
		return instance.getRootDeviceType();
	}

	@Override
	public List<GroupIdentifier> getSecurityGroups() {
		return instance.getSecurityGroups();
	}

	@Override
	public Boolean getSourceDestCheck() {
		return instance.getSourceDestCheck();
	}

	@Override
	public String getSpotInstanceRequestId() {
		return instance.getSpotInstanceRequestId();
	}

	@Override
	public String getSriovNetSupport() {
		return instance.getSriovNetSupport();
	}

	@Override
	public InstanceState getState() {
		return instance.getState();
	}

	@Override
	public StateReason getStateReason() {
		return instance.getStateReason();
	}

	@Override
	public String getStateTransitionReason() {
		return instance.getStateTransitionReason();
	}

	@Override
	public String getSubnetId() {
		return instance.getSubnetId();
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
	public List<Tag> getTags() {
		return instance.getTags();
	}

	@Override
	public String getTreeNodeLeafText() {
		return getInstanceName();
	}

	@Override
	public String getVirtualizationType() {
		return instance.getVirtualizationType();
	}

	@Override
	public String getVpcId() {
		return instance.getVpcId();
	}

	@Override
	public Boolean isEbsOptimized() {
		return instance.isEbsOptimized();
	}

	@Override
	public Boolean isEnaSupport() {
		return instance.isEnaSupport();
	}

	@Override
	public Boolean isSourceDestCheck() {
		return instance.isSourceDestCheck();
	}

	@Override
	public void performTableActions(CustomAWSObject object, JScrollableDesktopPane jScrollableDesktopPan, CustomTable table, String actionString) {
		if (actionString.contains(objectNickName + " Properties")) {
			UtilMethodsFactory.showFrame(object, jScrollableDesktopPan);
		}
	}

	@Override
	public void performTreeActions(CustomAWSObject object, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String actionString) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
		setAccount(((CustomTreeContainer) parentNode.getUserObject()).getAccount());
		if (actionString.equals(objectNickName.toUpperCase() + " PROPERTIES")) {
			UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals(objectNickName + "Storage")) {
			overviewPanel.getDetailesData(object, object.getAccount(), ec2InstanceStorageLabels, paneName);
		} else if (paneName.equals(objectNickName + "Security")) {
			overviewPanel.getDetailesData(object, object.getAccount(), ec2InstanceSecurityLabels, paneName);
		} else if (paneName.equals(objectNickName + "Advanced")) {
			overviewPanel.getDetailesData(object, object.getAccount(), ec2InstanceAdvancedLabels, paneName);
		} else if (paneName.equals(objectNickName + "General")) {
			overviewPanel.getDetailesData(object, object.getAccount(), ec2InstanceGeneralLabels, paneName);
		}
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	@Override
	public void showDetailesFrame(AWSAccount account, CustomAWSObject customAWSObject, JScrollableDesktopPane jScrollableDesktopPan) {
		CustomTableViewInternalFrame theFrame = null;
		try {
			try {
				theFrame = new CustomTableViewInternalFrame(getpropertiesPaneTitle(), UtilMethodsFactory.generateEC2ObjectPropertiesPane(customAWSObject, jScrollableDesktopPan));
				UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(getpropertiesPaneTitle(), jScrollableDesktopPan, theFrame);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(theFrame, "This " + objectNickName + " is Alredy Terminated", objectNickName + " Gone", JOptionPane.WARNING_MESSAGE);
		}
	}

	private ArrayList<String> getElasicAddressess(AWSAccount account, String instanceID) {
		ArrayList<String> elasticAddresses = new ArrayList<String>();
		Filter f = new Filter().withName("instance-id").withValues(instanceID);
		DescribeAddressesRequest request = new DescribeAddressesRequest().withFilters(f);
		try {
			DescribeAddressesResult result = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias())).describeAddresses(request);
			List<Address> addresses = result.getAddresses();
			if (addresses != null & !addresses.isEmpty()) {
				Iterator<Address> it = addresses.iterator();
				while (it.hasNext()) {
					elasticAddresses.add(it.next().getPublicIp());
				}
			} else {
				elasticAddresses.add(" - ");
			}
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		return elasticAddresses;
	}

	private String getAlarmStateForInstance(AWSAccount account, String instanceId) {
		String alarmStatValue = "No Alarms Set";
		AmazonCloudWatchClient amazonCloudWatchClient = new AmazonCloudWatchClient(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		ListMetricsRequest listMetricsRequest = new ListMetricsRequest();
		Collection<DimensionFilter> dimentions = new ArrayList<DimensionFilter>();
		dimentions.add(new DimensionFilter().withName("InstanceId").withValue(instanceId));
		listMetricsRequest.setDimensions(dimentions);
		ListMetricsResult lmres = amazonCloudWatchClient.listMetrics(listMetricsRequest);
		Iterator<Metric> im = lmres.getMetrics().iterator();
		while (im.hasNext()) {
			Metric me = im.next();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String metricName = me.getMetricName();
			DescribeAlarmsForMetricRequest describeAlarmsForMetricRequest = new DescribeAlarmsForMetricRequest();
			Dimension dimension = new Dimension();
			dimension.setName("InstanceId");
			dimension.setValue(instanceId);
			describeAlarmsForMetricRequest.withDimensions(dimension);
			describeAlarmsForMetricRequest.withNamespace("AWS/EC2");
			describeAlarmsForMetricRequest.withMetricName(metricName);
			DescribeAlarmsForMetricResult res = amazonCloudWatchClient.describeAlarmsForMetric(describeAlarmsForMetricRequest);
			Iterator<MetricAlarm> mai = res.getMetricAlarms().iterator();
			while (mai.hasNext()) {
				MetricAlarm alarm = mai.next();
				if (alarm.getStateValue().contains("ALARM")) {
					alarmStatValue = "ALARM";
					break;
				} else {
					alarmStatValue = "OK";
				}
			}
		}
		return alarmStatValue;
	}

	private ArrayList<CustomEC2Instance> retriveEC2Instances(AWSAccount account, boolean filtered, String appFilter) {
		System.out.println("!!!!!!!!!!!!!");
		ArrayList<CustomEC2Instance> instances = new ArrayList<CustomEC2Instance>();
		DescribeInstancesResult describeInstancesResult = null;
		try {
			AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			client.setRegion(account.getAccontRegionObject());
			describeInstancesResult = client.describeInstances();
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		Iterator<Reservation> ec2Reservations = describeInstancesResult.getReservations().iterator();
		while (ec2Reservations.hasNext()) {
			CustomEC2Instance customInstance = null;
			Reservation reservation = ec2Reservations.next();
			Iterator<Instance> instanceIterator = reservation.getInstances().iterator();
			while (instanceIterator.hasNext()) {
				Instance instance = instanceIterator.next();
				System.out.println("5: " + instance.getInstanceId());
				customInstance = new CustomEC2Instance(instance);
				if (appFilter != null) {
					if (filtered) {
						if (UtilMethodsFactory.getEC2ObjectFilterTag(instance.getTags(), "Name").matches(appFilter)) {
							customInstance.setAccount(account);
							instances.add(customInstance);
							System.out.println("1: " + instance.getInstanceId());
						}
					} else {
						customInstance.setAccount(account);
						instances.add(customInstance);
						System.out.println("2: " + instance.getInstanceId());
					}
				} else {
					if (filtered) {
						if (UtilMethodsFactory.getEC2ObjectFilterTag(instance.getTags(), "Name").matches(UtilMethodsFactory.getMatchString(account))) {
							customInstance.setAccount(account);
							instances.add(customInstance);
							System.out.println("3: " + instance.getInstanceId());
						}
					} else {
						customInstance.setAccount(account);
						instances.add(customInstance);
						System.out.println("4: " + instance.getInstanceId());
					}
				}
			}
		}
		return instances;
	}

	public static CustomEC2Instance retriveOneEC2Instance(AWSAccount account,  String id) {
		DescribeInstancesResult describeInstancesResult = null;
		DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(id);

		try {
			AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			client.setRegion(account.getAccontRegionObject());
			describeInstancesResult = client.describeInstances(request);
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		CustomEC2Instance customInstance = null;
		Iterator<Reservation> ec2Reservations = describeInstancesResult.getReservations().iterator();
		while (ec2Reservations.hasNext()) {

			Reservation reservation = ec2Reservations.next();
			Iterator<Instance> instanceIterator = reservation.getInstances().iterator();
			while (instanceIterator.hasNext()) {
				Instance instance = instanceIterator.next();
				customInstance = new CustomEC2Instance(instance);
				System.out.println("5: " + instance.getInstanceId());
		}
		}
		return customInstance;
	}

	public static CustomEC2Instance retriveEC2Instance(AWSAccount account,  String id) {
		DescribeInstancesResult describeInstancesResult = null;
		DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(id);

		try {
			AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			client.setRegion(account.getAccontRegionObject());
			describeInstancesResult = client.describeInstances(request);
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		CustomEC2Instance customInstance = null;
		Iterator<Reservation> ec2Reservations = describeInstancesResult.getReservations().iterator();
		while (ec2Reservations.hasNext()) {

			Reservation reservation = ec2Reservations.next();
			Iterator<Instance> instanceIterator = reservation.getInstances().iterator();
			while (instanceIterator.hasNext()) {
				Instance instance = instanceIterator.next();
				System.out.println(instance.getInstanceId());
				
				customInstance = new CustomEC2Instance(instance);

		}
		}
		return customInstance;
	}

	private Reservation retriveEC2InstanceReservation(AWSAccount account, String instanceID) {
		DescribeInstancesResult describeInstancesResult = null;
		try {
			AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			client.setRegion(account.getAccontRegionObject());
			DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
			describeInstancesResult = client.describeInstances(describeInstancesRequest.withInstanceIds(instanceID));
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		System.out.println("1: ");
		return describeInstancesResult.getReservations().get(0);
	}

	private Boolean findTerminationProtection(AWSAccount account, String instanceId) {
		DescribeInstanceAttributeRequest req = new DescribeInstanceAttributeRequest();
		req.setAttribute("disableApiTermination");
		req.setInstanceId(instanceId);
		DescribeInstanceAttributeResult res = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias())).describeInstanceAttribute(req);
		return res.getInstanceAttribute().getDisableApiTermination();
	}

	private String getSecondaryPublicIPs(AWSAccount account, String instanceId) {
		String secondaryIPs = "";
		int counter = 0;
		DescribeAddressesRequest describeAddressesRequest = new DescribeAddressesRequest();
		Filter f = new Filter().withName("instance-id").withValues(instanceId);
		Collection<Filter> filters = new ArrayList<Filter>();
		filters.add(f);
		describeAddressesRequest.setFilters(filters);
		DescribeAddressesResult res = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias())).describeAddresses(describeAddressesRequest);
		Iterator<Address> ai = res.getAddresses().iterator();
		while (ai.hasNext()) {
			String address = ai.next().getPrivateIpAddress();
			if (counter < 1) {
			} else {
				secondaryIPs = secondaryIPs + address + ",";
			}
			counter++;
		}
		if (secondaryIPs.length() > 0) {
			secondaryIPs = secondaryIPs.substring(0, secondaryIPs.length() - 1);
		} else {
			secondaryIPs = " - ";
		}
		return secondaryIPs;
	}

	private boolean isClassicLincUsed(AWSAccount account, String instanceId) {
		boolean hasClassicLink = false;
		DescribeClassicLinkInstancesRequest describeClassicLinkInstancesRequest = new DescribeClassicLinkInstancesRequest();
		Filter f = new Filter().withName("instance-id").withValues(instanceId);
		Collection<Filter> filters = new ArrayList<Filter>();
		filters.add(f);
		describeClassicLinkInstancesRequest.setFilters(filters);
		DescribeClassicLinkInstancesResult res = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias())).describeClassicLinkInstances(describeClassicLinkInstancesRequest);
		if (res.getInstances().size() < 1) {
			hasClassicLink = false;
		} else {
			hasClassicLink = true;
		}
		return hasClassicLink;
	}

	private String getInstanceScheduledEents(AWSAccount account, String instanceId) {
		DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest();
		Collection<String> ids = new ArrayList<String>();
		ids.add(instanceId);
		describeInstanceStatusRequest.setInstanceIds(ids);
		DescribeInstanceStatusResult res = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias())).describeInstanceStatus(describeInstanceStatusRequest);
		String instanseScheduledEvents = "";
		Iterator<InstanceStatus> iis = res.getInstanceStatuses().iterator();
		while (iis.hasNext()) {
			Iterator<InstanceStatusEvent> ise = iis.next().getEvents().iterator();
			while (ise.hasNext()) {
				instanseScheduledEvents = instanseScheduledEvents + ise.next().getDescription() + ",";
			}
		}
		if (instanseScheduledEvents.length() > 0) {
			instanseScheduledEvents = instanseScheduledEvents.substring(0, instanseScheduledEvents.length() - 1);
		} else {
			instanseScheduledEvents = " - ";
		}
		return instanseScheduledEvents;
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
		propertyPanelsFieldsCount.add(ec2InstanceGeneralLabels.length);
		propertyPanelsFieldsCount.add(ec2InstanceStorageLabels.length);
		propertyPanelsFieldsCount.add(ec2InstanceSecurityLabels.length);
		propertyPanelsFieldsCount.add(ec2InstanceAdvancedLabels.length);
		return propertyPanelsFieldsCount;
	}
}
