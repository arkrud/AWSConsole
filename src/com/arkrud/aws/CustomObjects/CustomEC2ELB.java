package com.arkrud.aws.CustomObjects;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.BackendServerDescription;
import com.amazonaws.services.elasticloadbalancing.model.DescribeInstanceHealthRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeInstanceHealthResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancerPoliciesRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancerPoliciesResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancerPolicyTypesResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeTagsRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeTagsResult;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.InstanceState;
import com.amazonaws.services.elasticloadbalancing.model.ListenerDescription;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.Policies;
import com.amazonaws.services.elasticloadbalancing.model.PolicyDescription;
import com.amazonaws.services.elasticloadbalancing.model.PolicyTypeDescription;
import com.amazonaws.services.elasticloadbalancing.model.SourceSecurityGroup;
import com.amazonaws.services.elasticloadbalancing.model.TagDescription;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.CustomProgressBar;
import com.arkrud.UI.LinkLikeButton;
import com.arkrud.UI.OverviewPanel;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.StaticFactories.EC2Common;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class CustomEC2ELB extends LoadBalancerDescription implements CustomAWSObject, PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private AWSAccount account;
	private LoadBalancerDescription loadBalancerDescription;
	private String[] elbsTableColumnHeaders = { "ELB Name", "DNS Name", "VPC ID", "Availability Zones", "Type", "Created At", "Port Configuration", "Instance Count", "Health Check" };
	private String[] elbInstancesTableColumnHeaders = { "Instance ID", "Instance Name", "Availability Zone", "Status" };
	private String[] elbSubnetsTableColumnHeaders = { "Availability Zone", "Subnet ID", "Subnet CIDR", "Instance Count", "Helthy?" };
	private String[] elbListenersTableColumnHeaders = { "Load Balancer Protocol", "Load Balancer Port", "Instance Protocol", "Instance Port", "SSL Certificate" };
	private String[] securityGroupsTableColumnHeaders = { "Group Name", "GroupID", "Owner", "VPC ID", "Description", "Tags", "Ingress", "Egress" };
	private JLabel[] elbOverviewHeaderLabels = { new JLabel("ELB Name: "), new JLabel("DNS Name: "), new JLabel("Scheme: "), new JLabel("Instances: "), new JLabel("Subnets: "), new JLabel("Availability Zones: "), new JLabel("Creation time: "),
			new JLabel("VPC ID: ") };
	private JLabel[] elbHealthCheckHeaderLabels = { new JLabel("Ping Target:"), new JLabel("Timeout:"), new JLabel("Interval:"), new JLabel("Unhealthy threshold:"), new JLabel("Healthy threshold:") };
	private String objectNickName = "ELB";

	public CustomEC2ELB() {
		super();
	}

	public CustomEC2ELB(AWSAccount account, String elbName, boolean filtered, String appFilter) {
		List<CustomEC2ELB> object = retriveEC2ELBs(account, filtered, appFilter);
		Iterator<CustomEC2ELB> iterator = object.iterator();
		while (iterator.hasNext()) {
			CustomEC2ELB customEC2ELB = iterator.next();
			if (customEC2ELB.getLoadBalancerName().equals(elbName)) {
				this.loadBalancerDescription = customEC2ELB;
				this.account = account;
			}
		}
	}

	public CustomEC2ELB(LoadBalancerDescription loadBalancerDescription) {
		this.loadBalancerDescription = loadBalancerDescription;
	}

	@Override
	public String[] defineNodeTreeDropDown() {
		String[] menus = { objectNickName + " Properties" };
		return menus;
	}

	@Override
	public String[] defineTableColumnHeaders() {
		return elbsTableColumnHeaders;
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

	public Hashtable<CustomEC2ELB, PolicyDescription> getAllELBsSLListenerPolicies(AWSAccount account) {
		Hashtable<CustomEC2ELB, PolicyDescription> descriptins = new Hashtable<CustomEC2ELB, PolicyDescription>();
		AmazonElasticLoadBalancing client = new AmazonElasticLoadBalancingClient(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		List<CustomEC2ELB> customELBs = retriveEC2ELBs(account, false, null);
		Iterator<CustomEC2ELB> accountsELBsIterator = customELBs.iterator();
		while (accountsELBsIterator.hasNext()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			CustomEC2ELB elb = accountsELBsIterator.next();
			DescribeLoadBalancerPoliciesRequest request = new DescribeLoadBalancerPoliciesRequest().withLoadBalancerName(elb.getLoadBalancerName());
			client.setRegion(account.getAccontRegionObject());
			DescribeLoadBalancerPoliciesResult result = client.describeLoadBalancerPolicies(request);
			Collection<PolicyDescription> policyDescriptions = result.getPolicyDescriptions();
			Iterator<PolicyDescription> policyDescriptionsIterator = policyDescriptions.iterator();
			while (policyDescriptionsIterator.hasNext()) {
				descriptins.put(elb, policyDescriptionsIterator.next());
			}
		}
		return descriptins;
	}

	@Override
	public ImageIcon getAssociatedContainerImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("elbs");
	}

	@Override
	public ImageIcon getAssociatedImage() {
		return UtilMethodsFactory.populateInterfaceImages().get("elb");
	}

	@Override
	public List<String> getAvailabilityZones() {
		return loadBalancerDescription.getAvailabilityZones();
	}

	public AWSAccount getAwsAccount() {
		return account;
	}

	@Override
	public ArrayList<Object> getAWSDetailesPaneData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(getLoadBalancerName());
		summaryData.add(getDNSName());
		summaryData.add(getScheme());
		summaryData.add(getInstances());
		//summaryData.add(getSubnets());
		List<CustomAWSSubnet> subnets = new ArrayList<CustomAWSSubnet>();
		for (int i = 0; i < getSubnets().size(); i++) {
			subnets.add(new CustomAWSSubnet(getAccount(),getSubnets().get(i)));
        }
		summaryData.add(subnets);
		summaryData.add(getAvailabilityZones());
		summaryData.add(getCreatedTime());
		summaryData.add(getVPCId());
		return summaryData;
	}

	@Override
	public ArrayList<Object> getAWSObjectSummaryData() {
		ArrayList<Object> summaryData = new ArrayList<Object>();
		summaryData.add(this);
		summaryData.add(getDNSName());
		summaryData.add(getVPCId());
		summaryData.add(getAZList());
		summaryData.add(getScheme());
		summaryData.add(getCreatedTime());
		summaryData.add(getELBPortConfiguration());
		summaryData.add(getInstances().size());
		summaryData.add(getHealthCheck().getTarget());
		return summaryData;
	}

	@Override
	public ArrayList<ArrayList<Object>> getAWSObjectTagsData() {
		return getELBTagsData();
	}

	private String getAZList() {
		Iterator<String> availabilityZones = getAvailabilityZones().iterator();
		String availabiklityZonesString = "";
		while (availabilityZones.hasNext()) {
			availabiklityZonesString = availabiklityZonesString + availabilityZones.next() + " , ";
		}
		return availabiklityZonesString = availabiklityZonesString.substring(0, availabiklityZonesString.length() - 3);
	}

	@Override
	public List<BackendServerDescription> getBackendServerDescriptions() {
		return loadBalancerDescription.getBackendServerDescriptions();
	}

	@Override
	public String getCanonicalHostedZoneName() {
		return loadBalancerDescription.getCanonicalHostedZoneName();
	}

	@Override
	public String getCanonicalHostedZoneNameID() {
		return loadBalancerDescription.getCanonicalHostedZoneNameID();
	}

	@Override
	public Date getCreatedTime() {
		return loadBalancerDescription.getCreatedTime();
	}

	public ArrayList<PolicyDescription> getDefaultELBPolicies(AWSAccount account) {
		ArrayList<PolicyDescription> elbPolices = new ArrayList<PolicyDescription>();
		AmazonElasticLoadBalancing client = new AmazonElasticLoadBalancingClient(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		client.setRegion(account.getAccontRegionObject());
		DescribeLoadBalancerPoliciesResult result = client.describeLoadBalancerPolicies();
		Iterator<PolicyDescription> policyDescriptions = result.getPolicyDescriptions().iterator();
		while (policyDescriptions.hasNext()) {
			elbPolices.add(policyDescriptions.next());
		}
		return elbPolices;
	}

	@Override
	public String getDNSName() {
		return loadBalancerDescription.getDNSName();
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

	public ArrayList<Object> getELBHelthCheckData() {
		ArrayList<Object> elbHealthCheckData = new ArrayList<Object>();
		HealthCheck healthCheck = getHealthCheck();
		elbHealthCheckData.add(healthCheck.getTarget());
		elbHealthCheckData.add(healthCheck.getTimeout());
		elbHealthCheckData.add(healthCheck.getInterval());
		elbHealthCheckData.add(healthCheck.getUnhealthyThreshold());
		elbHealthCheckData.add(healthCheck.getHealthyThreshold());
		return elbHealthCheckData;
	}

	private int getELBInstanceCountInSubnet(String subnetID) {
		int instancesCount = 0;
		Iterator<com.amazonaws.services.elasticloadbalancing.model.Instance> idsIterator = getInstances().iterator();
		while (idsIterator.hasNext()) {
			DescribeInstancesResult describeInstancesResult = null;
			try {
				AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
				DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
				String id = idsIterator.next().getInstanceId();
				client.setRegion(account.getAccontRegionObject());
				describeInstancesResult = client.describeInstances(describeInstancesRequest.withInstanceIds(id));
				if (describeInstancesResult.getReservations().get(0).getInstances().get(0).getSubnetId().equals(subnetID)) {
					instancesCount++;
				}
			} catch (AmazonServiceException e) {
				e.printStackTrace();
			} catch (AmazonClientException e) {
				e.printStackTrace();
			}
		}
		return instancesCount;
	}

	public ArrayList<ArrayList<Object>> getELBInstancesData() {
		ArrayList<ArrayList<Object>> elbInstancesData = new ArrayList<ArrayList<Object>>();
		getInstances();
		Iterator<com.amazonaws.services.elasticloadbalancing.model.Instance> idsIterator = getInstances().iterator();
		List<String> elbInstancesServiceStates = getELBInstancesServiceState();
		ArrayList<Object> elbInstancedata = null;
		int x = 0;
		while (idsIterator.hasNext()) {
			DescribeInstancesResult describeInstancesResult = null;
			try {
				AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
				DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
				String id = idsIterator.next().getInstanceId();
				client.setRegion(account.getAccontRegionObject());
				describeInstancesResult = client.describeInstances(describeInstancesRequest.withInstanceIds(id));
				elbInstancedata = new ArrayList<Object>();
				LinkLikeButton linkLikeIDButton = new LinkLikeButton(id);
				linkLikeIDButton.setAccount(account);
				elbInstancedata.add(linkLikeIDButton);
				Iterator<Tag> tagsIterator = describeInstancesResult.getReservations().get(0).getInstances().get(0).getTags().iterator();
				boolean hasName = false;
				while (tagsIterator.hasNext()) {
					Tag tag = tagsIterator.next();
					if (tag.getKey().equals("Name")) {
						hasName = true;
						elbInstancedata.add(tag.getValue());
					}
				}
				if (!hasName) {
					elbInstancedata.add("Not named");
				}
				elbInstancedata.add(describeInstancesResult.getReservations().get(0).getInstances().get(0).getPlacement().getAvailabilityZone());
				elbInstancedata.add(elbInstancesServiceStates.get(x));
			} catch (AmazonServiceException e) {
				e.printStackTrace();
			} catch (AmazonClientException e) {
				e.printStackTrace();
			}
			elbInstancesData.add(elbInstancedata);
			x++;
		}
		return elbInstancesData;
	}

	private List<String> getELBInstancesServiceState() {
		List<String> elbInstancesInServiceState = new ArrayList<String>();
		AmazonElasticLoadBalancingClient elbClient = new AmazonElasticLoadBalancingClient(AwsCommon.getAWSCredentials(getAwsAccount().getAccountAlias()));
		elbClient.setRegion(getAwsAccount().getAccontRegionObject());
		DescribeInstanceHealthRequest stateRequest = new DescribeInstanceHealthRequest().withLoadBalancerName(getLoadBalancerName());
		DescribeInstanceHealthResult stateResult = elbClient.describeInstanceHealth(stateRequest);
		List<InstanceState> instanceStates = stateResult.getInstanceStates();
		Iterator<InstanceState> it = instanceStates.iterator();
		while (it.hasNext()) {
			elbInstancesInServiceState.add(it.next().getState());
		}
		return elbInstancesInServiceState;
	}

	public ArrayList<ArrayList<Object>> getELBListenersData() {
		ArrayList<ArrayList<Object>> elbListenersData = new ArrayList<ArrayList<Object>>();
		Iterator<ListenerDescription> elbListenersIterator = getListenerDescriptions().iterator();
		while (elbListenersIterator.hasNext()) {
			ListenerDescription listenerDescription = elbListenersIterator.next();
			ArrayList<Object> listenerData = new ArrayList<Object>();
			listenerData.add(listenerDescription.getListener().getInstanceProtocol());
			listenerData.add(listenerDescription.getListener().getLoadBalancerPort());
			listenerData.add(listenerDescription.getListener().getProtocol());
			listenerData.add(listenerDescription.getListener().getInstancePort());
			String cert = listenerDescription.getListener().getSSLCertificateId();
			if (cert == null) {
				cert = " - ";
			} else {
			}
			listenerData.add(cert);
			elbListenersData.add(listenerData);
		}
		return elbListenersData;
	}

	public ArrayList<String> getELBPolicyTypes(AWSAccount account) {
		ArrayList<String> elbPolicesTypes = new ArrayList<String>();
		AmazonElasticLoadBalancing client = new AmazonElasticLoadBalancingClient(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		client.setRegion(account.getAccontRegionObject());
		DescribeLoadBalancerPolicyTypesResult result = client.describeLoadBalancerPolicyTypes();
		Iterator<PolicyTypeDescription> policyTypeDescriptions = result.getPolicyTypeDescriptions().iterator();
		while (policyTypeDescriptions.hasNext()) {
			elbPolicesTypes.add(policyTypeDescriptions.next().getPolicyTypeName());
		}
		return elbPolicesTypes;
	}

	private String getELBPortConfiguration() {
		if (getListenerDescriptions().size() > 0) {
			String listenerDescriptionString = "";
			Iterator<ListenerDescription> listenerDescriptions = getListenerDescriptions().iterator();
			while (listenerDescriptions.hasNext()) {
				ListenerDescription listenerDescription = listenerDescriptions.next();
				listenerDescriptionString = listenerDescriptionString + listenerDescription.getListener().getLoadBalancerPort() + " (" + listenerDescription.getListener().getProtocol() + ") forwarding to "
						+ listenerDescription.getListener().getInstancePort() + " (" + listenerDescription.getListener().getInstanceProtocol() + ") , ";
			}
			listenerDescriptionString = listenerDescriptionString.substring(0, listenerDescriptionString.length() - 3);
			return (listenerDescriptionString);
		} else {
			return "";
		}
	}

	public ArrayList<ArrayList<Object>> getELBSecurityGroupsData() {
		List<String> elbSecurityGroups = new ArrayList<String>();
		AmazonElasticLoadBalancingClient elbClient = new AmazonElasticLoadBalancingClient(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		Region region = null;
		if (account.getAccountRegion().contains("us-east-1")) {
			region = Region.getRegion(Regions.US_EAST_1);
		} else if (account.getAccountRegion().contains("us-west-2")) {
			region = Region.getRegion(Regions.US_WEST_2);
		}
		elbClient.setRegion(region);
		DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest().withLoadBalancerNames(getLoadBalancerName());
		DescribeLoadBalancersResult lbs = elbClient.describeLoadBalancers(request);
		List<LoadBalancerDescription> descriptions = lbs.getLoadBalancerDescriptions();
		for (LoadBalancerDescription loadBalancerDescription : descriptions) {
			elbSecurityGroups = loadBalancerDescription.getSecurityGroups();
		}
		ArrayList<ArrayList<Object>> elbSecurityGroupsData = new ArrayList<ArrayList<Object>>();
		for (String groupID : elbSecurityGroups) {
			ArrayList<Object> groupData = new ArrayList<Object>();
			CustomEC2SecurityGroup customEC2SecurityGroup = new CustomEC2SecurityGroup(getAwsAccount(), groupID, false, null);
			groupData.add(customEC2SecurityGroup.getGroupName());
			groupData.add(customEC2SecurityGroup.getGroupId());
			groupData.add(customEC2SecurityGroup.getOwnerId());
			if (customEC2SecurityGroup.getVpcId() == null) {
				groupData.add("Not in VPC");
			} else {
				groupData.add(customEC2SecurityGroup.getVpcId());
			}
			groupData.add(customEC2SecurityGroup.getDescription());
			groupData.add("Tags");
			groupData.add("Ingress");
			groupData.add("Egress");
			elbSecurityGroupsData.add(groupData);
		}
		return elbSecurityGroupsData;
	}

	public ArrayList<ArrayList<Object>> getELBSubnetsData() {
		ArrayList<ArrayList<Object>> elbSubnetsData = new ArrayList<ArrayList<Object>>();
		Iterator<String> elbSubnetsIDsIterator = getSubnets().iterator();
		while (elbSubnetsIDsIterator.hasNext()) {
			String subnetID = elbSubnetsIDsIterator.next();
			DescribeSubnetsRequest describeSubnetsRequest = new DescribeSubnetsRequest().withSubnetIds(subnetID);
			AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			client.setRegion(account.getAccontRegionObject());
			DescribeSubnetsResult res = client.describeSubnets(describeSubnetsRequest);
			ArrayList<Object> elbSubnetData = null;
			Iterator<Subnet> si = res.getSubnets().iterator();
			while (si.hasNext()) {
				Subnet subnet = si.next();
				elbSubnetData = new ArrayList<Object>();
				elbSubnetData.add(subnet.getAvailabilityZone());
				elbSubnetData.add(subnet.getSubnetId());
				elbSubnetData.add(subnet.getCidrBlock());
				int instanceCount = getELBInstanceCountInSubnet(subnet.getSubnetId());
				elbSubnetData.add(instanceCount);
				if (instanceCount < 1) {
					elbSubnetData.add("No (Availability Zone contains no healthy instances)");
				} else {
					elbSubnetData.add("Yes");
				}
			}
			elbSubnetsData.add(elbSubnetData);
		}
		return elbSubnetsData;
	}

	private ArrayList<ArrayList<Object>> getELBTagsData() {
		ArrayList<ArrayList<Object>> elbTagsData = new ArrayList<ArrayList<Object>>();
		AmazonElasticLoadBalancing client = new AmazonElasticLoadBalancingClient(AwsCommon.getAWSCredentials(getAwsAccount().getAccountAlias()));
		DescribeTagsRequest describeTagsRequest = new DescribeTagsRequest().withLoadBalancerNames(getLoadBalancerName());
		client.setRegion(account.getAccontRegionObject());
		DescribeTagsResult response = client.describeTags(describeTagsRequest);
		Iterator<TagDescription> tagsDercriptionIterator = response.getTagDescriptions().iterator();
		while (tagsDercriptionIterator.hasNext()) {
			TagDescription descr = tagsDercriptionIterator.next();
			Iterator<com.amazonaws.services.elasticloadbalancing.model.Tag> tagsIterator = descr.getTags().iterator();
			while (tagsIterator.hasNext()) {
				ArrayList<Object> elbTagData = new ArrayList<Object>();
				com.amazonaws.services.elasticloadbalancing.model.Tag tag = tagsIterator.next();
				elbTagData.add(tag.getKey());
				elbTagData.add(tag.getValue());
				elbTagsData.add(elbTagData);
			}
		}
		return elbTagsData;
	}

	@Override
	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		return retriveEC2ELBs(account, true, appFilter);
	}

	@Override
	public HealthCheck getHealthCheck() {
		return loadBalancerDescription.getHealthCheck();
	}

	@Override
	public List<Instance> getInstances() {
		return loadBalancerDescription.getInstances();
	}

	@Override
	public List<Integer> getkeyEvents() {
		List<Integer> events = new ArrayList<Integer>();
		events.add(KeyEvent.VK_0);
		events.add(KeyEvent.VK_1);
		events.add(KeyEvent.VK_2);
		events.add(KeyEvent.VK_3);
		events.add(KeyEvent.VK_4);
		events.add(KeyEvent.VK_5);
		return events;
	}

	@Override
	public List<ListenerDescription> getListenerDescriptions() {
		return loadBalancerDescription.getListenerDescriptions();
	}

	@Override
	public String getListTabHeaders(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "General", objectNickName + "HealthCheck" };
		String[] listPaneHeaders = { "General", "HealthCheck" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneHeaders);
	}

	@Override
	public String getListTabToolTips(String tableIdentifier) {
		String[] listPansIdentifiers = { objectNickName + "General", objectNickName + "HealthCheck" };
		String[] listPaneToolTips = { "General ELB Properties", "ELB Health Check" };
		return UtilMethodsFactory.getListTabsData(tableIdentifier, listPansIdentifiers, listPaneToolTips);
	}

	@Override
	public String getLoadBalancerName() {
		return loadBalancerDescription.getLoadBalancerName();
	}

	@Override
	public String getObjectAWSID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectName() {
		return getLoadBalancerName();
	}

	@Override
	public Policies getPolicies() {
		return loadBalancerDescription.getPolicies();
	}

	@Override
	public LinkedHashMap<String[][], String[][][]> getPropertiesPaneTableParams() {
		LinkedHashMap<String[][], String[][][]> map = new LinkedHashMap<String[][], String[][][]>();
		String[][] dataFlags0 = { { "ELBInstances", "ELBNetworking" } };
		String[][][] columnHeaders0 = { { elbInstancesTableColumnHeaders, elbSubnetsTableColumnHeaders } };
		map.put(dataFlags0, columnHeaders0);
		String[][] dataFlags1 = { { "ELBListeners" } };
		String[][][] columnHeaders1 = { { elbListenersTableColumnHeaders } };
		map.put(dataFlags1, columnHeaders1);
		String[][] dataFlags2 = { { "ELBSecurityGroups" } };
		String[][][] columnHeaders2 = { { securityGroupsTableColumnHeaders } };
		map.put(dataFlags2, columnHeaders2);
		String[][] dataFlags3 = { { "Tags" } };
		String[][][] columnHeaders3 = { { UtilMethodsFactory.tagsTableColumnHeaders } };
		map.put(dataFlags3, columnHeaders3);
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getpropertiesPaneTabs() {
		LinkedHashMap<String, String> propertiesPaneTabs = new LinkedHashMap<String, String>();
		propertiesPaneTabs.put(objectNickName + "General", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "Instances", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "HealthCheck", "ListInfoPane");
		propertiesPaneTabs.put(objectNickName + "Listeners", "TableInfoPane");
		propertiesPaneTabs.put(objectNickName + "SecurityGroups", "TableInfoPane");
		propertiesPaneTabs.put("Tags", "TableInfoPane");
		return propertiesPaneTabs;
	}

	@Override
	public String getpropertiesPaneTitle() {
		return objectNickName + " Properties for " + getLoadBalancerName() + " under " + getAccount().getAccountAlias() + " account";
	}

	@Override
	public ArrayList<Integer> getPropertyPanelsFieldsCount() {
		ArrayList<Integer> propertyPanelsFieldsCount = new ArrayList<Integer>();
		propertyPanelsFieldsCount.add(elbOverviewHeaderLabels.length);
		propertyPanelsFieldsCount.add(elbHealthCheckHeaderLabels.length);
		return propertyPanelsFieldsCount;
	}

	@Override
	public String getScheme() {
		return loadBalancerDescription.getScheme();
	}

	@Override
	public List<String> getSecurityGroups() {
		return loadBalancerDescription.getSecurityGroups();
	}

	@Override
	public SourceSecurityGroup getSourceSecurityGroup() {
		return loadBalancerDescription.getSourceSecurityGroup();
	}

	@Override
	public List<String> getSubnets() {
		return loadBalancerDescription.getSubnets();
	}

	@Override
	public String getTableTabHeaders(String tableIdentifier) {
		String[][] tableIdentifiers = { { "ELBInstances" }, { "ELBNetworking" }, { "ELBListeners" }, { "ELBSecurityGroups" }, { "Tags" } };
		String[][] tablePaneHeaders = { { "Instances" }, { "Networking" }, { "Listeners" }, { "Security Groups" }, { "Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneHeaders);
	}

	@Override
	public String getTableTabToolTips(String tableIdentifier) {
		String[][] tableIdentifiers = { { "ELBInstances" }, { "ELBNetworking" }, { "ELBListeners" }, { "ELBSecurityGroups" }, { "Tags" } };
		String[][] tablePaneToolTips = { { objectNickName + " Instances" }, { objectNickName + " Networking" }, { objectNickName + " Listeners" }, { objectNickName + " Security Groups" }, { objectNickName + " Tags" } };
		return UtilMethodsFactory.getTableTabsData(tableIdentifier, tableIdentifiers, tablePaneToolTips);
	}

	@Override
	public String getTreeNodeLeafText() {
		return getLoadBalancerName();
	}

	@Override
	public String getVPCId() {
		return loadBalancerDescription.getVPCId();
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
			final CustomProgressBar progFrame = new CustomProgressBar(true, false, "Retrieving ELB Info");
			progFrame.getPb().setIndeterminate(true);
			SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					UtilMethodsFactory.showFrame(node.getUserObject(), dash.getJScrollableDesktopPane());
					return null;
				};

				// this is called when the SwingWorker's doInBackground finishes
				@Override
				protected void done() {
					progFrame.getPb().setIndeterminate(false);
					progFrame.setVisible(false); // hide my progress bar JFrame
				};
			};
			w.addPropertyChangeListener(this);
			w.execute();
			progFrame.setVisible(true);
		}
	}

	@Override
	public void populateAWSObjectPrpperties(OverviewPanel overviewPanel, CustomAWSObject object, String paneName) {
		if (paneName.equals(objectNickName + "General")) {
			overviewPanel.getDetailesData(object, object.getAccount(), elbOverviewHeaderLabels, paneName);
		} else if (paneName.equals(objectNickName + "HealthCheck")) {
			overviewPanel.getDetailesData(object, object.getAccount(), elbHealthCheckHeaderLabels, paneName);
		}
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

	private ArrayList<CustomEC2ELB> retriveEC2ELBs(AWSAccount account, boolean filtered, String appFilter) {
		ArrayList<CustomEC2ELB> elbs = new ArrayList<CustomEC2ELB>();
		@SuppressWarnings("deprecation")
		AmazonElasticLoadBalancingClient elbClient = new AmazonElasticLoadBalancingClient(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		Region region = null;
		if (account.getAccountRegion().contains("us-east-1")) {
			region = Region.getRegion(Regions.US_EAST_1);
		} else if (account.getAccountRegion().contains("us-west-2")) {
			region = Region.getRegion(Regions.US_WEST_2);
		}
		elbClient.setRegion(account.getAccontRegionObject());
		
		DescribeLoadBalancersResult lbr = elbClient.describeLoadBalancers();
		Iterator<LoadBalancerDescription> it = lbr.getLoadBalancerDescriptions().iterator();
		CustomEC2ELB customEC2ELB = null;
		while (it.hasNext()) {
			LoadBalancerDescription loadBalancerDescription = it.next();
			customEC2ELB = new CustomEC2ELB(loadBalancerDescription);
			if (appFilter != null) {
				if (filtered) {
					if (loadBalancerDescription.getLoadBalancerName().matches(appFilter)) {
						elbs.add(customEC2ELB);
					}
				} else {
					elbs.add(customEC2ELB);
				}
			} else {
				if (filtered) {
					if (loadBalancerDescription.getLoadBalancerName().matches(UtilMethodsFactory.getMatchString(account))) {
						elbs.add(customEC2ELB);
					}
				} else {
					elbs.add(customEC2ELB);
				}
			}
			customEC2ELB.setAccount(account);
		}
		return elbs;
	}

	@Override
	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	public void setAwsAccount(AWSAccount account) {
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
	}
}
