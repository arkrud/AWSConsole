package com.arkrud.UI;

import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.amazonaws.services.autoscaling.model.BlockDeviceMapping;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.InstanceBlockDeviceMapping;
import com.amazonaws.services.ec2.model.InstanceNetworkInterface;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.VolumeAttachment;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancingv2.model.AvailabilityZone;
import com.arkrud.Shareware.SpringUtilities;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.CustomObjects.CustomAWSObject;
import com.arkrud.aws.CustomObjects.CustomAWSSubnet;
import com.arkrud.aws.CustomObjects.CustomEC2ELB;
import com.arkrud.aws.CustomObjects.CustomEC2ELBV2;
import com.arkrud.aws.CustomObjects.CustomEC2Instance;
import com.arkrud.aws.CustomObjects.CustomEC2NetworkInterface;
import com.arkrud.aws.CustomObjects.CustomEC2SecurityGroup;
import com.arkrud.aws.CustomObjects.CustomEC2TargetGroup;
import com.arkrud.aws.CustomObjects.CustomEC2Volume;
import com.arkrud.aws.CustomObjects.CustomRoute53DNSRecord;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

/**
 * Builds UI for AWS Elements TabbedPane Overview Tabs
 *
 */
public class OverviewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JScrollableDesktopPane jScrollableDesktopPane;
	private AWSAccount account;

	public OverviewPanel(Object parentPane, AWSAccount account, JScrollableDesktopPane jScrollableDesktopPane, String dataFlag) {
		this.jScrollableDesktopPane = jScrollableDesktopPane;
		this.setLayout(new SpringLayout());
		this.account = account;
		((CustomAWSObject) parentPane).populateAWSObjectPrpperties(this, (CustomAWSObject) parentPane, dataFlag);
	}

	@SuppressWarnings("unchecked")
	public void getDetailesData(Object awsObject, AWSAccount account, JLabel[] propertyNameLabels, String dataFlag) {
		List<Object> properties = getProperties(awsObject, dataFlag);
		int x = 0;
		while (x < propertyNameLabels.length) {
			propertyNameLabels[x].setFont(new Font("Serif", Font.BOLD, 12));
			add(propertyNameLabels[x]);
			Object property = properties.get(x);
			if (property != null) {
			System.out.println("property type: " + property.getClass().getSimpleName() + "and value: " +  propertyNameLabels[x]);}
			if (property instanceof List<?>) {
				if (((List<Object>) property).isEmpty()) {
					LabelLikeTextPane labelLikeTextPane = new LabelLikeTextPane(" - ", false);
					add(labelLikeTextPane);
				} else {
					if (UtilMethodsFactory.getListObjectType((List<Object>) property).contains("String")) {
						if (propertyNameLabels[x].getText().contains("Subnet")) {
							add(createButtonPanel((List<Instance>) property, "PanelLinkLikeButton", account, String.class));
						} else {
							add(UtilMethodsFactory.getCommaDelimitedLabel((List<String>) property));
						}
					} else if (UtilMethodsFactory.getListObjectType((List<Object>) property).contains("InstanceBlockDeviceMapping")) {
						add(createButtonPanel((List<Instance>) property, "PanelLinkLikeButton", account, CustomEC2Volume.class));
					} else if (UtilMethodsFactory.getListObjectType((List<Object>) property).contains("GroupIdentifier")) {
						add(createButtonPanel((List<Instance>) property, "PanelLinkLikeButton", account, CustomEC2SecurityGroup.class));
					} else if (UtilMethodsFactory.getListObjectType((List<Object>) property).contains("InstanceNetworkInterface")) {
						add(createButtonPanel((List<Instance>) property, "PanelLinkLikeButton", account, CustomEC2NetworkInterface.class));
					} else if (UtilMethodsFactory.getListObjectType((List<Object>) property).contains("ELBInstance")) {
						add(createButtonPanel((List<Instance>) property, "PanelLinkLikeButton", account, CustomEC2Instance.class));
					} else if (UtilMethodsFactory.getListObjectType((List<Object>) property).equals("CustomEC2ELB")) {
						add(createButtonPanel((List<Instance>) property, "PanelLinkLikeButton", account, CustomEC2ELB.class));
					} else if (UtilMethodsFactory.getListObjectType((List<Object>) property).equals("CustomEC2ELBV2")) {
						add(createButtonPanel((List<Instance>) property, "PanelLinkLikeButton", account, CustomEC2ELBV2.class));
					} else if (UtilMethodsFactory.getListObjectType((List<Object>) property).contains("CustomEC2TargetGroup")) {
						add(createButtonPanel((List<Instance>) property, "PanelLinkLikeButton", account, CustomEC2TargetGroup.class));
					} else if (UtilMethodsFactory.getListObjectType((List<Object>) properties.get(x)).contains("BlockDeviceMapping")) {
						add(createButtonPanel((List<Instance>) property, "PanelLinkLikeButton", account, BlockDeviceMapping.class));
					} else if (UtilMethodsFactory.getListObjectType((List<Object>) property).contains("VolumeAttachment")) {
						add(createButtonPanel((List<Instance>) property, "PanelLinkLikeButton", account, VolumeAttachment.class));
					} else if (UtilMethodsFactory.getListObjectType((List<Object>) property).contains("AvailabilityZone")) {
						add(createButtonPanel((List<Instance>) property, "PanelLinkLikeButton", account, AvailabilityZone.class));
					}
				}
			} else if (property instanceof CustomAWSObject) {
				LinkLikeButton linkLikeButton = new LinkLikeButton(property);
				linkLikeButton.setName("PanelLinkLikeButton");
				linkLikeButton.setAccount(account);
				linkLikeButton.setjScrollableDesktopPan(jScrollableDesktopPane);
				linkLikeButton.setCustomAWSObject((CustomAWSObject) property);
				add(linkLikeButton);
			} else if (property instanceof Integer) {
				PropertyLabel labelLikeTextPane = new PropertyLabel(Integer.toString((Integer) property));
				add(labelLikeTextPane);
			} else if (property instanceof Long) {
				PropertyLabel labelLikeTextPane = new PropertyLabel(Long.toString((Long) property));
				add(labelLikeTextPane);
			} else if (property instanceof Date) {
				SimpleDateFormat sdfr = new SimpleDateFormat("dd/MMM/yyyy");
				PropertyLabel labelLikeTextPane = new PropertyLabel(sdfr.format(property));
				add(labelLikeTextPane);
			} else if (property instanceof Boolean) {
				PropertyLabel labelLikeTextPane = new PropertyLabel(String.valueOf(property));
				add(labelLikeTextPane);
			} else {
				boolean justLabel = true;
				if ((String) property != null) {
					if (propertyNameLabels[x].getText().contains("IAM Instance Profile") ) {
						// || propertyNameLabels[x].getText().contains("Key Name")
						LinkLikeButton linkLikeButton = new LinkLikeButton(property);
						linkLikeButton.setName("PanelLinkLikeButton");
						add(linkLikeButton);
						justLabel = false;
					} else {
						addTextFieldPropertyObject(property, propertyNameLabels[x].getText());
						justLabel = false;
					}
				}
				if (justLabel) {
					PropertyLabel label = new PropertyLabel((String) property);
					add(label);
				}
			}
			x++;
		}
		SpringUtilities.makeCompactGrid(this, properties.size(), 2, 10, 10, 10, 10);
	}

	private List<Object> getProperties(Object awsObject, String dataFlag) {
		List<Object> properties = new ArrayList<Object>();
		CustomEC2Instance customEC2Instance;
		CustomEC2ELB customEC2ELB;
		CustomEC2TargetGroup customEC2TargetGroup;
		CustomRoute53DNSRecord customRoute53DNSRecord;
		switch (dataFlag) {
		case "InstanceStorage":
			customEC2Instance = (CustomEC2Instance) awsObject;
			properties = customEC2Instance.getInstanceStoragePaneData();
			break;
		case "InstanceSecurity":
			customEC2Instance = (CustomEC2Instance) awsObject;
			properties = customEC2Instance.getInstanceSecurityPaneData();
			break;
		case "InstanceAdvanced":
			customEC2Instance = (CustomEC2Instance) awsObject;
			properties = customEC2Instance.getInstanceAdvancedPaneData();
			break;
		case "ELBHealthCheck":
			customEC2ELB = (CustomEC2ELB) awsObject;
			properties = customEC2ELB.getELBHelthCheckData();
			break;
		case "TargetGroupHealthCheck":
			customEC2TargetGroup = (CustomEC2TargetGroup) awsObject;
			properties = customEC2TargetGroup.getHealthCheckPaneData();
			break;
		case "DNSRecordSet Advanced":
			customRoute53DNSRecord = (CustomRoute53DNSRecord) awsObject;
			properties = customRoute53DNSRecord.getRecordSetAdvancedPaneData();
			break;
		case "DNSRecordSet Details":
			customRoute53DNSRecord = (CustomRoute53DNSRecord) awsObject;
			properties = customRoute53DNSRecord.getAWSDetailesPaneData();
			break;	
		default:
			properties = ((CustomAWSObject) awsObject).getAWSDetailesPaneData();
			break;
		}
		return properties;
	}

	private JPanel createButtonPanel(List<?> objectsList, String panelHostObjectType, AWSAccount account, Class<?> panelObjectClass) {
		Iterator<?> li = (objectsList.iterator());
		JPanel buttonPanel = new JPanel(new SpringLayout());
		String objectIdentifier = "";
		while (li.hasNext()) {
			Object obj = li.next();
			if (panelObjectClass.getSimpleName().contains("CustomEC2Instance")) {
				objectIdentifier = ((Instance) obj).getInstanceId();
			} else if (panelObjectClass.getName().contains("CustomEC2Volume")) {
				objectIdentifier = ((InstanceBlockDeviceMapping) obj).getEbs().getVolumeId();
			} else if (panelObjectClass.getName().contains("CustomAWSSubnet")) {
				objectIdentifier = ((Subnet) obj).getSubnetId();
			} else if (panelObjectClass.getName().contains("CustomEC2SecurityGroup")) {
				objectIdentifier = ((GroupIdentifier) obj).getGroupId();
			} else if (panelObjectClass.getName().equals("CustomEC2ELB")) {
				objectIdentifier = ((CustomEC2ELB) obj).getLoadBalancerName();
			} else if (panelObjectClass.getSimpleName().equals("CustomEC2ELBV2")) {
				objectIdentifier = ((CustomEC2ELBV2) obj).getLoadBalancerName();
				} else if (panelObjectClass.getName().contains("CustomEC2TargetGroup")) {
				objectIdentifier = ((CustomEC2TargetGroup) obj).getTargetGroupName();
			} else if (panelObjectClass.getName().contains("CustomEC2NetworkInterface")) {
				objectIdentifier = ((InstanceNetworkInterface) obj).getNetworkInterfaceId();
			} else if (panelObjectClass.getName().contains("VolumeAttachment")) {
				objectIdentifier = ((VolumeAttachment) obj).getInstanceId();
			} else if (panelObjectClass.getName().contains("BlockDeviceMapping")) {
				objectIdentifier = ((BlockDeviceMapping) obj).getDeviceName();
			} else if (panelObjectClass.getName().contains("AvailabilityZone")) {
				objectIdentifier = ((AvailabilityZone) obj).getZoneName();
			} else if (panelObjectClass.getName().contains("String")) {
				objectIdentifier = (String) obj;
			}
			LinkLikeButton linkLikeButton = new LinkLikeButton(objectIdentifier);
			linkLikeButton.setName(panelHostObjectType);
			linkLikeButton.setjScrollableDesktopPan(jScrollableDesktopPane);
			linkLikeButton.setAccount(account);
			if (panelObjectClass.getName().contains("CustomEC2Instance")) {
				linkLikeButton.setCustomAWSObject(new CustomEC2Instance(account, objectIdentifier, false, null));
			} else if (panelObjectClass.getName().contains("CustomEC2Volume")) {
				linkLikeButton.setCustomAWSObject(new CustomEC2Volume(account, objectIdentifier));
			} else if (panelObjectClass.getName().contains("CustomEC2SecurityGroup")) {
				linkLikeButton.setCustomAWSObject(new CustomEC2SecurityGroup(account, objectIdentifier, false, null));
			} else if (panelObjectClass.getName().equals("CustomEC2ELB")) {
				linkLikeButton.setCustomAWSObject(new CustomEC2ELB(account, objectIdentifier, false, null));
			} else if (panelObjectClass.getSimpleName().equals("CustomEC2ELBV2")) {
				linkLikeButton.setCustomAWSObject(new CustomEC2ELBV2(account, objectIdentifier, false, null));
			} else if (panelObjectClass.getName().contains("CustomEC2TargetGroup")) {
				linkLikeButton.setCustomAWSObject(new CustomEC2TargetGroup(account, objectIdentifier, false, null));
			} else if (panelObjectClass.getName().contains("CustomEC2NetworkInterface")) {
				linkLikeButton.setCustomAWSObject(new CustomEC2NetworkInterface(account, objectIdentifier, null));
			} else if (panelObjectClass.getName().contains("BlockDeviceMapping")) {
				// linkLikeButton.setCustomAWSObject(new CustomEC2NetworkInterface(account, objectIdentifier, false));
			} else if (panelObjectClass.getName().contains("VolumeAttachment")) {
				linkLikeButton.setCustomAWSObject(new CustomEC2Instance(account, objectIdentifier, false, null));
			} else if (panelObjectClass.getName().contains("CustomAWSSubnet")) {
				linkLikeButton.setCustomAWSObject(new CustomAWSSubnet(account, objectIdentifier));
			} else if (panelObjectClass.getName().contains("String")) {
			}
			buttonPanel.add(linkLikeButton);
		}
		SpringUtilities.makeCompactGrid(buttonPanel, 1, objectsList.size(), 1, 1, 1, 1);
		return buttonPanel;
	}

	private void addTextFieldPropertyObject(Object property, String fieldName) {
		JTextField tf = new JTextField();
		tf.setText((String) property);
		tf.setEditable(false); // as before
		tf.setBackground(null); // this is the same as a JLabel
		tf.setBorder(null); // remove the border
		tf.setAlignmentX(LEFT_ALIGNMENT);
		Font font = new Font("Courier", Font.BOLD, 12);
		tf.setFont(font);
		tf.setPreferredSize(new Dimension(200, 30));
		JPopupMenu popup = new JPopupMenu();
		tf.addMouseListener(new CustomTextPanePopupListener(popup,account,fieldName));
		add(tf);
	}

	public void launchPermissionsData(ArrayList<String> lunchPermissions, AWSAccount account) {
		JLabel headerLabel = new JLabel("AWS Account Number");
		headerLabel.setFont(new Font("Serif", Font.BOLD, 16));
		add(headerLabel);
		Iterator<String> permissionsIterator = lunchPermissions.iterator();
		while (permissionsIterator.hasNext()) {
			PropertyLabel label = new PropertyLabel(permissionsIterator.next());
			add(label);
		}
		SpringUtilities.makeCompactGrid(this, lunchPermissions.size() + 1, 1, 10, 10, 10, 10);
	}
}
