package com.arkrud.TreeInterface;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.amazonaws.services.elasticloadbalancing.model.PolicyDescription;
import com.amazonaws.services.s3.model.Bucket;
import com.arkrud.Shareware.SpringUtilities;
import com.arkrud.UI.CustomProgressBar;
import com.arkrud.UI.InterfaceFilter;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AWSService;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.CustomObjects.CFStackTreeNodeUserObject;
import com.arkrud.aws.CustomObjects.CustomEC2AMI;
import com.arkrud.aws.CustomObjects.CustomEC2Asg;
import com.arkrud.aws.CustomObjects.CustomEC2ELB;
import com.arkrud.aws.CustomObjects.CustomEC2ELBPolicyType;
import com.arkrud.aws.CustomObjects.CustomEC2ELBV2;
import com.arkrud.aws.CustomObjects.CustomEC2Instance;
import com.arkrud.aws.CustomObjects.CustomEC2KeyPair;
import com.arkrud.aws.CustomObjects.CustomEC2LC;
import com.arkrud.aws.CustomObjects.CustomEC2NetworkInterface;
import com.arkrud.aws.CustomObjects.CustomEC2SecurityGroup;
import com.arkrud.aws.CustomObjects.CustomEC2SnapShot;
import com.arkrud.aws.CustomObjects.CustomEC2TargetGroup;
import com.arkrud.aws.CustomObjects.CustomEC2Volume;
import com.arkrud.aws.CustomObjects.CustomELBPolicyDescription;
import com.arkrud.aws.CustomObjects.CustomIAMInstanceProfile;
import com.arkrud.aws.CustomObjects.CustomIAMRole;
import com.arkrud.aws.CustomObjects.CustomRegionObject;
import com.arkrud.aws.CustomObjects.CustomRoute53Zone;
import com.arkrud.aws.StaticFactories.S3Common;

/**
 * Class to build AWS Objects Tree (OT) and AWS Objects Configuration Tree (OCT).<br>
 * OT will organize AWS object accessible by user by Region, AWS Account, AWS Service, and AWS Object type. <br>
 * OT nodes provide drop down menus relevant to the actions allowed for object the node represents including action to refresh node children. <br>
 * Double click on container nodes will add associated object table control with data for filtered list of objects in multitable view area. <br>
 * OT objects are rendered with object and container specific icons and object and container names <br>
 * OCT provides the view of the same tree hierarchy as OT tree provides. <br>
 * But each object is presented as check box to activate or deactivate retrieval of AWS objects data based on accounts, services, and object types user desired.
 * <br>
 * The OCT also provides filter objects which can be used to filter retrieval of the AWS objects based on text pattern. <br>
 * After all selections in OCT tree are done the root "Select To Finish Configuration" node needs to be selected to apply the changes. <br>
 * After objects and containers are checked in OCT OT tree needs to be refreshed to reflect the configuration change and retrieve desired objects from AWS
 * cloud. <br>
 * Tree interface provides indeterministic progress bar while the AWS objects are retrieving which can take significant time for of large amount of them
 * requested. <br>
 */
public class CustomTree extends JPanel implements TreeWillExpandListener, TreeSelectionListener, PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private Class<?> treeContainerEC2Classes[] = { CustomEC2SecurityGroup.class, CustomEC2ELB.class, CustomEC2ELBV2.class, CustomEC2TargetGroup.class,
			CustomEC2KeyPair.class, CustomEC2ELBPolicyType.class, CustomEC2Instance.class, CustomEC2AMI.class, CustomEC2Volume.class, CustomEC2SnapShot.class,
			CustomEC2Asg.class, CustomEC2LC.class, CustomEC2NetworkInterface.class };
	private Class<?> treeContainerIAMClasses[] = { CustomIAMInstanceProfile.class };
	private Class<?> treeContainerRoute53Classes[] = { CustomRoute53Zone.class };
	public String ec2TreeContainerNames[] = { "Security Groups", "Load Balancers", "V2 Load Balancers", "Target Groups", "Key Pairs", "ELB Polices",
			"Instances", "AMIs", "Volumes", "Snapshots", "Autoscaling Groups", "Launch Configurations", "Network Interfaces" };
	public String iamTreeContainerNames[] = { "Instance Profiles" };
	public String cfTreeContainerNames[] = { "Stacks" };
	public String vpcTreeContainerNames[] = { "Subnets" };
	public String route53TreeContainerNames[] = { "Zones" };
	private DefaultMutableTreeNode top;
	private DefaultTreeModel treeModel;
	private JTree cloudTree;
	private Dashboard dash;
	private String treeType;
	private ArrayList<DefaultMutableTreeNode> regionNodes = new ArrayList<DefaultMutableTreeNode>();
	private String filter;

	/**
	 *
	 * Sole constructor of CustomTree object. <br>
	 * Defines the behavior of the tree interface and adds it to the JPanel<br>
	 *
	 *
	 * <ul>
	 * <li>Set tree top element to AwsCommon object
	 * <li>Initiate default JTree model
	 * <li>Add CustomTreeModelListener
	 * </ul>
	 * <p>
	 *
	 * @param dash
	 *            reference to the Dashboard object
	 * @param treeType
	 *            tree usage identifier (OT or OCT)
	 */
	public CustomTree(Dashboard dash, String treeType) {
		this.dash = dash;
		this.treeType = treeType;
		if (treeType.equals("config")) {
			top = new DefaultMutableTreeNode(new AwsCommon("Select To Finish Configuration"));
		} else {
			top = new DefaultMutableTreeNode(new AwsCommon(treeType));
		}
		treeModel = new DefaultTreeModel(top);
		treeModel.addTreeModelListener(new CustomTreeModelListener());
		cloudTree = new JTree(treeModel);
		cloudTree.addTreeWillExpandListener(this);
		cloudTree.setRowHeight(0);
		cloudTree.setToggleClickCount(0);
		try {
			createNodes(top);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cloudTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		cloudTree.addTreeSelectionListener(this);
		cloudTree.setShowsRootHandles(true);
		cloudTree.setBackground(Color.WHITE);
		ToolTipManager.sharedInstance().registerComponent(cloudTree);
		if (treeType.equals("config")) {
			cloudTree.setCellRenderer(new StateRenderer());
			cloudTree.setCellEditor(new StateEditor());
			cloudTree.setEditable(true);
		} else {
			cloudTree.setCellRenderer(new CustomTreeCellRenderer(UtilMethodsFactory.populateInterfaceImages()));
		}
		try {
			getTreePopUpMenu(cloudTree);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setLayout(new SpringLayout());
		setBackground(Color.WHITE);
		add(cloudTree);
		SpringUtilities.makeCompactGrid(this, 1, 1, 1, 1, 1, 1);
	}

	/**
	 * Add PopUp menu to cluster icons in the tree to initiate actions on them. <br>
	 */
	private void getTreePopUpMenu(JTree servicesTree) throws Exception {
		JPopupMenu popup = null;
		if (popup == null) {
			popup = new JPopupMenu();
			popup.setInvoker(servicesTree);
			// Instantiate Pop-up Menu handler class instance
			CustomTreePopupHandler handler = new CustomTreePopupHandler(servicesTree, popup, dash, this);
			// treeTabbedPane.addTab("Services", null, servicesTree, null);
			for (String dropDownMenuName : UtilMethodsFactory.dropDownsNames) {
				popup.add(getMenuItem(dropDownMenuName, handler));
			}
		}
	}

	/**
	 * Add menu items to PopUp menu with various actions defined. <br>
	 */
	private JMenuItem getMenuItem(String s, ActionListener al) {
		JMenuItem menuItem = new JMenuItem(s);
		menuItem.setActionCommand(s.toUpperCase());
		menuItem.addActionListener(al);
		return menuItem;
	}

	/**
	 * Populate tree with data. <br>
	 * Defines the behavior of the tree interface and adds it to the JPanel<br>
	 *
	 * @param top
	 *            root node
	 *
	 */
	private void createNodes(DefaultMutableTreeNode top) throws Exception {
		DefaultMutableTreeNode awsAccount = null;
		DefaultMutableTreeNode awsRegionTreeNode = null;
		DefaultMutableTreeNode awsService = null;
		Iterator<CustomRegionObject> regionsIterator = AwsCommon.getAWSRegions().iterator();
		while (regionsIterator.hasNext()) {
			CustomRegionObject customRegionObject = regionsIterator.next();
			awsRegionTreeNode = new DefaultMutableTreeNode(customRegionObject);
			top.add(awsRegionTreeNode);
			regionNodes.add(awsRegionTreeNode);
			Iterator<AWSAccount> accountsIterator = INIFilesFactory.readAWSINIAccounts(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"),
					UtilMethodsFactory.getConsoleConfig(), UtilMethodsFactory.getAWSAPIINIConfigs("config"), false).iterator();
			while (accountsIterator.hasNext()) {
				AWSAccount theAWSAccount = accountsIterator.next();
				if (theAWSAccount.getAccountRegion().contains(customRegionObject.getObjectName())) {
					awsAccount = new DefaultMutableTreeNode(theAWSAccount);
					if (treeType.equals("config")) {
						((AWSAccount) awsAccount.getUserObject()).setSelected(Boolean.valueOf(INIFilesFactory
								.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), theAWSAccount.getAccountAlias(), theAWSAccount.getAccountAlias())));
						awsRegionTreeNode.add(awsAccount);
						int y = 0;
						while (y < UtilMethodsFactory.awsServicesInfo.length) {
							if (INIFilesFactory.hasItemInSection(UtilMethodsFactory.getConsoleConfig(), theAWSAccount.getAccountAlias(),
									UtilMethodsFactory.awsServicesInfo[y][0])) {
								AWSService container = new AWSService();
								container.setAwsServiceName(UtilMethodsFactory.awsServicesInfo[y][0]);
								container.setAWSAccountAlias(theAWSAccount.getAccountAlias());
								container.setSelected(Boolean.valueOf(INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(),
										theAWSAccount.getAccountAlias(), UtilMethodsFactory.awsServicesInfo[y][0])));
								DefaultMutableTreeNode containerNode = new DefaultMutableTreeNode(container);
								awsAccount.add(containerNode);
							} else {
								AWSService container = new AWSService();
								container.setAwsServiceName(UtilMethodsFactory.awsServicesInfo[y][0]);
								container.setAWSAccountAlias(theAWSAccount.getAccountAlias());
								container.setSelected(false);
								DefaultMutableTreeNode containerNode = new DefaultMutableTreeNode(container);
								awsAccount.add(containerNode);
								INIFilesFactory.addINIFileItemToSection(UtilMethodsFactory.getConsoleConfig(), theAWSAccount.getAccountAlias(),
										UtilMethodsFactory.awsServicesInfo[y][0], false);
							}
							y++;
						}
					} else {
						if (Boolean.valueOf(INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), theAWSAccount.getAccountAlias(),
								theAWSAccount.getAccountAlias()))) {
							awsRegionTreeNode.add(awsAccount);
						}
						Iterator<AWSService> awsServicesIterator = theAWSAccount.getAwsServices().iterator();
						while (awsServicesIterator.hasNext()) {
							AWSService tehAWSService = awsServicesIterator.next();
							tehAWSService.setAWSAccountAlias(theAWSAccount.getAccountAlias());
							awsService = new DefaultMutableTreeNode(tehAWSService);
							awsAccount.add(awsService);
						}
					}
				}
			}
		}
	}

	/**
	 * Expand tree 2 nodes deep. <br>
	 */
	public void expandTwoDeep() {
		for (int i = 0; i < 2; i++) {
			cloudTree.expandRow(i);
		}
	}

	/**
	 * Expand all tree nodes. <br>
	 */
	public void expandAllNodes() {
		for (int i = 0; i < cloudTree.getRowCount(); i++) {
			cloudTree.expandRow(i);
		}
	}

	/**
	 * Build the configuration tree to define which AWS accounts, services, and objects will be displayed<br>
	 * and trees of filtered AWS objects grouped by Region, AWS Account, AWS service, and object types <br>
	 */
	@Override
	public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
		if (treeType.equals("All Applications")) {
			filter = null;
		} else {
			filter = "(.*)(?i)" + treeType + "(.*)";
		}
		if (treeType.equals("config")) {
			populateAWSObjectsConfigs((DefaultMutableTreeNode) (e.getPath().getLastPathComponent()));
		} else {
			if (e.getPath().getPathCount() < 3) {
				populateAccountsAndServices((DefaultMutableTreeNode) (e.getPath().getLastPathComponent()), filter);
			} else {
				final CustomProgressBar progFrame = new CustomProgressBar(true, false, "Loading AWS Objects");
				progFrame.getPb().setIndeterminate(true);
				SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						populateAccountsAndServices((DefaultMutableTreeNode) (e.getPath().getLastPathComponent()), filter);
						return null;
					};

					// This is called when the SwingWorker's doInBackground finishes
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
	}

	/**
	 * Populate the configuration tree to define which AWS accounts, services, and objects will be displayed based on data saved in INI configuration file<br>
	 */
	private void populateAWSObjectsConfigs(DefaultMutableTreeNode node) {
		if (node.getUserObject() instanceof AWSAccount) {
			String accountAlias = ((AWSAccount) node.getUserObject()).getAccountAlias();
			if (INIFilesFactory.hasItemInSection(UtilMethodsFactory.getConsoleConfig(), accountAlias, accountAlias)) {
				((AWSAccount) node.getUserObject())
						.setSelected(Boolean.valueOf(INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), accountAlias, accountAlias)));
			} else {
				INIFilesFactory.addINIFileItemToSection(UtilMethodsFactory.getConsoleConfig(), accountAlias, accountAlias, true);
				((AWSAccount) node.getUserObject()).setSelected(true);
			}
			Enumeration<?> children = node.preorderEnumeration();
			while (children.hasMoreElements()) {
				DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) children.nextElement();
				if (theNode.getUserObject() instanceof AWSService) {
					String serviceName = ((AWSService) theNode.getUserObject()).getAwsServiceName();
					if (INIFilesFactory.hasItemInSection(UtilMethodsFactory.getConsoleConfig(), accountAlias, serviceName)) {
						((AWSService) theNode.getUserObject()).setSelected(
								Boolean.valueOf(INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), accountAlias, serviceName)));
					} else {
						INIFilesFactory.addINIFileItemToSection(UtilMethodsFactory.getConsoleConfig(), accountAlias, serviceName, true);
						((AWSService) theNode.getUserObject()).setSelected(true);
					}
					if (serviceName.contains("S3")) {
					} else if (serviceName.contains("EC2")) {
						populateServiceAWSObjectsConfigs(theNode, accountAlias, ec2TreeContainerNames);
					} else if (serviceName.contains("Cloud Formation")) {
					} else if (serviceName.contains("Network Services")) {
					} else if (serviceName.contains("Route53")) {
						populateServiceAWSObjectsConfigs(theNode, accountAlias, route53TreeContainerNames);
					} else if (serviceName.contains("IAM")) {
						populateServiceAWSObjectsConfigs(theNode, accountAlias, iamTreeContainerNames);
					}
				}
			}
			AwsCommon common = new AwsCommon("Filters");
			common.setAWSAccountAlias(accountAlias);
			common.setAccount((AWSAccount) node.getUserObject());
			DefaultMutableTreeNode filtersContainerNode = new DefaultMutableTreeNode(common);
			Iterator<InterfaceFilter> accountFilters = ((AWSAccount) node.getUserObject()).getFilters().iterator();
			node.add(filtersContainerNode);
			while (accountFilters.hasNext()) {
				InterfaceFilter interfaceFilter = accountFilters.next();
				interfaceFilter.setAWSAccountAlias(accountAlias);
				DefaultMutableTreeNode filterNode = new DefaultMutableTreeNode(interfaceFilter);
				filtersContainerNode.add(filterNode);
			}
		}
	}

	private void populateServiceAWSObjectsConfigs(DefaultMutableTreeNode theNode, String accountAlias, String treeContainerNames[]) {
		int x = 0;
		CustomTreeContainer container;
		while (x < treeContainerNames.length) {
			container = new CustomTreeContainer();
			container.setContainerName(treeContainerNames[x]);
			container.setAWSAccountAlias(accountAlias);
			if (INIFilesFactory.hasItemInSection(UtilMethodsFactory.getConsoleConfig(), accountAlias, treeContainerNames[x])) {
				container.setSelected(Boolean.valueOf(
						INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), accountAlias, treeContainerNames[x])));
			} else {
				INIFilesFactory.addINIFileItemToSection(UtilMethodsFactory.getConsoleConfig(), accountAlias, treeContainerNames[x],
						container.isSelected());
			}
			DefaultMutableTreeNode containerNode = new DefaultMutableTreeNode(container);
			theNode.add(containerNode);
			x++;
		}
	}


	/**
	 * Populate AWS accounts and services based on data saved in INI configuration file<br>
	 */
	private void populateAccountsAndServices(DefaultMutableTreeNode node, String appFilter) {
		if (node.getUserObject() instanceof AWSAccount) {
			Enumeration<?> children = node.preorderEnumeration();
			while (children.hasMoreElements()) {
				DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) children.nextElement();
				if (theNode.getUserObject() instanceof AWSService) {
					AWSAccount awsAccount = ((AWSService) theNode.getUserObject()).getTheAccount();
					if (((AWSService) theNode.getUserObject()).getAwsServiceName().contains("S3")) {
						populateS3Nodes(theNode);
					} else if (((AWSService) theNode.getUserObject()).getAwsServiceName().contains("EC2")) {
						populateEC2Nodes(theNode, appFilter);
					} else if (((AWSService) theNode.getUserObject()).getAwsServiceName().contains("Cloud Formation")) {
						CustomTreeContainer container = new CustomTreeContainer();
						DefaultMutableTreeNode cfStackContainerNode = new DefaultMutableTreeNode(container);
						container.setAccount(awsAccount);
						container.setChildObject(CFStackTreeNodeUserObject.class);
						container.setContainerName("Stacks");
						theNode.add(cfStackContainerNode);
						populateContainerObjects(cfStackContainerNode, appFilter);
					} else if (((AWSService) theNode.getUserObject()).getAwsServiceName().equals("IAM")) {
						populateIAMNodes(theNode, appFilter);
					} else if (((AWSService) theNode.getUserObject()).getAwsServiceName().equals("Route53")) {
						populateRout53Nodes(theNode, appFilter);
					}
				}
			}
		}
	}

	/**
	 * Populate AWS S3 service data<br>
	 */
	private void populateS3Nodes(DefaultMutableTreeNode s3Node) {
		AWSAccount awsAccount = ((AWSService) s3Node.getUserObject()).getTheAccount();
		ArrayList<Bucket> s3BucketsObjects = S3Common.retrieveS3Buckets(S3Common.connectToS3(AwsCommon.getAWSCredentials(awsAccount.getAccountAlias())));
		Iterator<Bucket> s3BucketsIterator = s3BucketsObjects.iterator();
		while (s3BucketsIterator.hasNext()) {
			Bucket bucket = s3BucketsIterator.next();
			if (bucket.getName().matches(UtilMethodsFactory.getMatchString(awsAccount))) {
				DefaultMutableTreeNode s3Bucket = new DefaultMutableTreeNode(bucket);
				s3Node.add(s3Bucket);
				S3Common.returnBucketStructure(awsAccount, bucket, "", s3Bucket);
			}
		}
	}

	/**
	 * Populate AWS EC2 service data<br>
	 */
	private void populateEC2Nodes(DefaultMutableTreeNode ec2Node, String appFilter) {
		AWSAccount awsAccount = ((AWSService) ec2Node.getUserObject()).getTheAccount();
		LinkedHashMap<Class<?>, String> objectsMap = new LinkedHashMap<Class<?>, String>();
		for (int i = 0; i < treeContainerEC2Classes.length; i++) {
			objectsMap.put(treeContainerEC2Classes[i], ec2TreeContainerNames[i]);
		}
		for (Class<?> key : objectsMap.keySet()) {
			if (Boolean
					.valueOf(INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), awsAccount.getAccountAlias(), objectsMap.get(key)))) {
				CustomTreeContainer container = new CustomTreeContainer();
				container.setAccount(awsAccount);
				container.setChildObject(key);
				container.setContainerName(objectsMap.get(key));
				DefaultMutableTreeNode ecContainerNode = new DefaultMutableTreeNode(container);
				ec2Node.add(ecContainerNode);
				if (objectsMap.get(key).equals("ELB Polices")) {
					try {
						populatePolices(ecContainerNode);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						populateContainerObjects(ecContainerNode, appFilter);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Populate AWS EC2 ELB Policies. <br>
	 */
	private void populatePolices(DefaultMutableTreeNode node) {
		CustomTreeContainer container = (CustomTreeContainer) node.getUserObject();
		CustomEC2ELB customEC2ELB = new CustomEC2ELB();
		Iterator<String> elbPolicyTypes = customEC2ELB.getELBPolicyTypes(container.getAccount()).iterator();
		while (elbPolicyTypes.hasNext()) {
			CustomEC2ELBPolicyType ec2ELBPolicyType = new CustomEC2ELBPolicyType(elbPolicyTypes.next());
			ec2ELBPolicyType.setAccount(container.getAccount());
			DefaultMutableTreeNode elbPolicyTypeNode = new DefaultMutableTreeNode(ec2ELBPolicyType);
			node.add(elbPolicyTypeNode);
			Iterator<PolicyDescription> defaultElbPolicies = customEC2ELB.getDefaultELBPolicies(container.getAccount()).iterator();
			while (defaultElbPolicies.hasNext()) {
				PolicyDescription policyDescription = defaultElbPolicies.next();
				if (policyDescription.getPolicyTypeName().equals(ec2ELBPolicyType.getPolicyName())) {
					CustomELBPolicyDescription elbPOlicyDescription = new CustomELBPolicyDescription(policyDescription, null);
					DefaultMutableTreeNode ec2ELBPolicyNode = new DefaultMutableTreeNode(elbPOlicyDescription);
					elbPolicyTypeNode.add(ec2ELBPolicyNode);
				}
			}
		}
		Hashtable<CustomEC2ELB, PolicyDescription> elbPolicies = customEC2ELB
				.getAllELBsSLListenerPolicies(((CustomTreeContainer) node.getUserObject()).getAccount());
		Enumeration<?> elbPolicesChildrenNodes = node.preorderEnumeration();
		Collection<String> policyNames = new ArrayList<String>();
		while (elbPolicesChildrenNodes.hasMoreElements()) {
			DefaultMutableTreeNode eldPolicyNode = (DefaultMutableTreeNode) elbPolicesChildrenNodes.nextElement();
			if (eldPolicyNode.getUserObject() instanceof CustomEC2ELBPolicyType) {
				Set<CustomEC2ELB> elbs = elbPolicies.keySet();
				for (CustomEC2ELB elb : elbs) {
					PolicyDescription policyDescription = elbPolicies.get(elb);
					if (policyDescription.getPolicyTypeName().equals(((CustomEC2ELBPolicyType) eldPolicyNode.getUserObject()).getPolicyName())
							&& !policyNames.contains(policyDescription.getPolicyName())) {
						policyNames.add(policyDescription.getPolicyName());
						CustomELBPolicyDescription elbPOlicyDescription = new CustomELBPolicyDescription(policyDescription, elb);
						DefaultMutableTreeNode ec2ELBPolicyNode = new DefaultMutableTreeNode(elbPOlicyDescription);
						eldPolicyNode.add(ec2ELBPolicyNode);
					}
				}
			}
		}
	}

	/**
	 * Populate AWS IAM service data<br>
	 */
	private void populateIAMNodes(DefaultMutableTreeNode iamNode, String appFilter) {
		AWSAccount awsAccount = ((AWSService) iamNode.getUserObject()).getTheAccount();
		LinkedHashMap<Class<?>, String> objectsMap = new LinkedHashMap<Class<?>, String>();
		for (int i = 0; i < treeContainerIAMClasses.length; i++) {
			objectsMap.put(treeContainerIAMClasses[i], iamTreeContainerNames[i]);
		}
		for (Class<?> key : objectsMap.keySet()) {
			if (Boolean
					.valueOf(INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), awsAccount.getAccountAlias(), objectsMap.get(key)))) {
				CustomTreeContainer container = new CustomTreeContainer();
				container.setAccount(awsAccount);
				container.setChildObject(key);
				container.setContainerName(objectsMap.get(key));
				DefaultMutableTreeNode iamContainerNode = new DefaultMutableTreeNode(container);
				iamNode.add(iamContainerNode);
				try {
					populateContainerObjects(iamContainerNode, appFilter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Populate AWS IAM service data<br>
	 */
	private void populateRout53Nodes(DefaultMutableTreeNode rout53ZoneNode, String appFilter) {
		AWSAccount awsAccount = ((AWSService) rout53ZoneNode.getUserObject()).getTheAccount();
		LinkedHashMap<Class<?>, String> objectsMap = new LinkedHashMap<Class<?>, String>();
		for (int i = 0; i < treeContainerRoute53Classes.length; i++) {
			objectsMap.put(treeContainerRoute53Classes[i], route53TreeContainerNames[i]);
		}
		for (Class<?> key : objectsMap.keySet()) {
			if (Boolean
					.valueOf(INIFilesFactory.getItemValueFromINI(UtilMethodsFactory.getConsoleConfig(), awsAccount.getAccountAlias(), objectsMap.get(key)))) {
				CustomTreeContainer container = new CustomTreeContainer();
				container.setAccount(awsAccount);
				container.setChildObject(key);
				container.setContainerName(objectsMap.get(key));
				DefaultMutableTreeNode route53ContainerNode = new DefaultMutableTreeNode(container);
				rout53ZoneNode.add(route53ContainerNode);
				try {
					populateContainerObjects(route53ContainerNode, appFilter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Populate filtered AWS objects<br>
	 */
	private void populateContainerObjects(DefaultMutableTreeNode containerNode, String appFilter) {
		Object containerObject = containerNode.getUserObject();
		CustomTreeContainer customTreeContainer = (CustomTreeContainer) containerObject;
		AWSAccount account = customTreeContainer.getAccount();
		ArrayList<?> filteredObjects = customTreeContainer.getFilteredAWSObjects(account, appFilter);
		customTreeContainer.setEc2Objects(filteredObjects);
		DefaultMutableTreeNode customObjectNode = null;
		Iterator<?> objectsIterator = null;
		try {
			objectsIterator = filteredObjects.iterator();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		while (objectsIterator.hasNext()) {
			Object obj = objectsIterator.next();
			if (obj instanceof CustomIAMInstanceProfile) {
				((CustomIAMInstanceProfile) obj).setAccount(account);
			}
			customObjectNode = new DefaultMutableTreeNode(obj);
			containerNode.add(customObjectNode);
			if (customObjectNode.getUserObject() instanceof CustomIAMInstanceProfile) {
				CustomIAMRole role = new CustomIAMRole();
				role.setAccount(account);
				Iterator<?> customIAMRolesIterator = role.getFilteredAWSObjects((CustomIAMInstanceProfile) customObjectNode.getUserObject()).iterator();
				while (customIAMRolesIterator.hasNext()) {
					DefaultMutableTreeNode customIAMRoleNode = new DefaultMutableTreeNode(customIAMRolesIterator.next());
					customObjectNode.add(customIAMRoleNode);
				}
			}
		}
	}

	/**
	 * Public method to refresh tree after changing the filters with progress bar. <br>
	 */
	public void refreshTreeNodeWithProgress(DefaultMutableTreeNode node, boolean decorationState, String appFilter) {
		final CustomProgressBar progFrame = new CustomProgressBar(true, decorationState, "Loading EC2 Objects");
		progFrame.getPb().setIndeterminate(true);
		SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				refreshTreeNode(node, appFilter);
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

	/**
	 * Public method to refresh tree after changing the filters. <br>
	 */
	public void refreshTreeNode(DefaultMutableTreeNode node, String appFilter) {
		if (treeType.equals("All Applications")) {
			filter = null;
		} else {
			filter = "(.*)(?i)" + treeType + "(.*)";
		}
		/*if (node == null) {
			top.removeAllChildren();
			treeModel.reload();
			try {
				invokeWithProgressBar(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			expandAllNodes();
		} else {*/
			node.removeAllChildren();
			treeModel.nodeStructureChanged(node);
			if (node.getUserObject() instanceof CustomTreeContainer) {
			}
			if (node.getUserObject() instanceof AWSService) {
				if (((AWSService) node.getUserObject()).getAwsServiceName().equals("S3 Service")) {
					populateS3Nodes(node);
				} else if (((AWSService) node.getUserObject()).getAwsServiceName().equals("EC2 Service")) {
					populateEC2Nodes(node, filter);
				}
			} else if (node.getUserObject() instanceof AwsCommon) {
				try {
					createNodes(top);
					expandTwoDeep();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (node.getUserObject() instanceof AWSAccount) {
				if (treeType.equals("config")) {
					Iterator<AWSAccount> accountsIterator = INIFilesFactory.readAWSINIAccounts(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"),
							UtilMethodsFactory.getConsoleConfig(), UtilMethodsFactory.getAWSAPIINIConfigs("config"), true).iterator();
					while (accountsIterator.hasNext()) {
						AWSAccount theAWSAccount = accountsIterator.next();
						if (theAWSAccount.getAccountRegion().contains(((AWSAccount) node.getUserObject()).getAccontRegionObject().getName())
								&& theAWSAccount.getAccountAlias().equals(((AWSAccount) node.getUserObject()).getAccountAlias())) {
							node.setUserObject(theAWSAccount);
							Iterator<AWSService> awsServicesIterator = null;
							awsServicesIterator = theAWSAccount.getAwsServices().iterator();
							while (awsServicesIterator.hasNext()) {
								AWSService tehAWSService = awsServicesIterator.next();
								DefaultMutableTreeNode awsServiceNode = new DefaultMutableTreeNode(tehAWSService);
								node.add(awsServiceNode);
								populateAWSObjectsConfigs(awsServiceNode);
							}
						}
					}
				} else {
					Iterator<AWSAccount> accountsIterator = INIFilesFactory.readAWSINIAccounts(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"),
							UtilMethodsFactory.getConsoleConfig(), UtilMethodsFactory.getAWSAPIINIConfigs("config"), false).iterator();
					while (accountsIterator.hasNext()) {
						AWSAccount theAWSAccount = accountsIterator.next();
						if (theAWSAccount.getAccountRegion().contains(((AWSAccount) node.getUserObject()).getAccontRegionObject().getName())
								&& theAWSAccount.getAccountAlias().equals(((AWSAccount) node.getUserObject()).getAccountAlias())) {
							node.setUserObject(theAWSAccount);
							Iterator<AWSService> awsServicesIterator = null;
							awsServicesIterator = theAWSAccount.getAwsServices().iterator();
							while (awsServicesIterator.hasNext()) {
								AWSService tehAWSService = awsServicesIterator.next();
								DefaultMutableTreeNode awsServiceNode = new DefaultMutableTreeNode(tehAWSService);
								node.add(awsServiceNode);
								populateAccountsAndServices(awsServiceNode, filter);
							}
						}
					}
				}
			} else {
				if (((CustomTreeContainer) node.getUserObject()).getContainerName().equals("ELB Polices")) {
					populatePolices(node);
				} else {
					populateContainerObjects(node, filter);
				}
			}
			expandNodesBelow(node);
		//}
	}

	/**
	 * Expand nodes below selected node after tree refresh. <br>
	 */
	private void expandNodesBelow(DefaultMutableTreeNode node) {
		cloudTree.expandPath(new TreePath(((DefaultTreeModel) cloudTree.getModel()).getPathToRoot(node)));
	}

	/**
	 * Return list of regions. <br>
	 */
	public ArrayList<DefaultMutableTreeNode> getRegionNodes() {
		return regionNodes;
	}

	/**
	 * Return reference to services tree. <br>
	 */
	public JTree getServicesTree() {
		return cloudTree;
	}

	/**
	 * This method gets called when a bound property is changed. <br>
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

	/**
	 * Override interface method to specify what to do when tree will collapse. <br>
	 */
	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
	}

	/**
	 * Register a tree selection listener to detect when the user selects a node in a tree. <br>
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		JTree sourceTree = (JTree) e.getSource();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) sourceTree.getLastSelectedPathComponent();
		if (node == null)
			return;
	}

	/*private void invokeWithProgressBar(boolean decorationState) {
		final CustomProgressBar progFrame = new CustomProgressBar(true, decorationState, "Loading EC2 Objects");
		progFrame.getPb().setIndeterminate(true);
		SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				createNodes(top);
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
	}*/
}
