package com.arkrud.Util;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimplePBEConfig;

import com.amazonaws.services.autoscaling.model.BlockDeviceMapping;
import com.amazonaws.services.autoscaling.model.TagDescription;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.InstanceBlockDeviceMapping;
import com.amazonaws.services.ec2.model.InstanceNetworkInterface;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.VolumeAttachment;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancingv2.model.AvailabilityZone;
import com.amazonaws.services.s3.model.Bucket;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.TableInterface.CustomTableModel;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.UI.AddApplicationTreeFrame;
import com.arkrud.UI.InterfaceFilter;
import com.arkrud.UI.LabelLikeTextPane;
import com.arkrud.UI.PropertiesTabbedPane;
import com.arkrud.UI.AWSAccount.AWSAccountFrame;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.UI.SecurityGroups.AddSecurityGroupFrame;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.CustomObjects.CustomAWSObject;
import com.arkrud.aws.CustomObjects.CustomEC2ELB;
import com.arkrud.aws.CustomObjects.CustomEC2ELBV2;
import com.arkrud.aws.CustomObjects.CustomEC2TargetGroup;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomtessier.scrollabledesktop.BaseInternalFrame;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

/**
 * Static methods and constants accessed across application code.<br>
 *
 */
public class UtilMethodsFactory {
	public static String[] dropDownsNames = { "Manage AWS Accounts", "Add AWS Account", "Remove AWS Account", "Manage AWS Services", "S3 Service", "Filters", "Add Bucket", "Delete Bucket", "Delete Folder", "Add Folder", "Upload Data",
			"Add Security Group", "Delete Security Group", "Security Group Properties", "Create CF Stack", "Delete CF Stack", "Stack Properties", "Update CF Stack", "Create ELB", "ELB Properties", "ELBV2 Properties", "Instance Properties",
			"Delete ELB", "AMI Properties", "Deregister AMI", "Refresh", "Create Instance", "Delete Snapshot", "Snapshot Properties", "Instance Profile Properties", "Delete Instance Profile", "IAM Role Properties", "Delete IAM Role",
			"Delete AutoScaling Group", "AutoScaling Group Properties", "Create AutoScaling Group", "Launch Configuration Properties", "Copy Launch Configuration", "Volume Properties", "Delete Volume", "Delete KeyPair", "Add Filter", "Delete Filter",
			"Network Interface Properties", "Delete Network Interface" , "TargetGroup Properties", "Hosted Zone Properties", "DNSRecordSet Properties" };
	public static String[] securityGroupsRulesTableColumnHeaders = { "Rule Type", "Protocol", "Port Range", "Source Type", "Range" };
	public static String[] elbv2ListenerRulesTableColumnHeaders = { "ARN", "Default", "Priority", "Rules Actions", "Rules Conditions" };
	private static String[] awsAccountsTableColumnHeaders = { "Account", "aws_access_key_id", "aws_secret_access_key", "Region" };
	public static String[] tagsTableColumnHeaders = { "Key", "Value" };
	public static String[][] awsServicesInfo = { { "S3 Service", "Unlimited And Undistructable Storage", "s3_service" }, { "EC2 Service", "AWS Compute Service", "ec2_service" }, { "APIGateway Service", "AWS API Gateway Service", "apigateway_service" }, { "Cloud Formation", "AWS Environment Automation", "cf_service" },
			{ "IAM", "AWS Identity and Access Management", "iam_service" }, { "Network Services", "Virtual Network Services", "net_service" } , { "Route53", "DNS Services", "dns_service" } };

	public static void addInternalFrameToScrolableDesctopPane(String frameTitle, JScrollableDesktopPane jScrollableDesktopPan, BaseInternalFrame theFrame) {
		if (Dashboard.INTERNAL_FRAMES.get(frameTitle) == null) {
			jScrollableDesktopPan.add(theFrame);
			Dashboard.INTERNAL_FRAMES.put(frameTitle, theFrame);
		} else {
			jScrollableDesktopPan.remove(Dashboard.INTERNAL_FRAMES.get(frameTitle));
			jScrollableDesktopPan.add(theFrame);
			jScrollableDesktopPan.setSelectedFrame(theFrame);
			Dashboard.INTERNAL_FRAMES.put(frameTitle, theFrame);
		}
	}

	public static void closeOrphanWindows(String orphansCommonPath, JScrollableDesktopPane jScrollableDesktopPan) {
		Enumeration<String> keys = Dashboard.INTERNAL_FRAMES.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (key.contains(orphansCommonPath)) {
				jScrollableDesktopPan.remove(Dashboard.INTERNAL_FRAMES.get(key));
				Dashboard.INTERNAL_FRAMES.remove(key);
			}
		}
	}

	// Convert a stream into a single, newline separated string
	public static String convertStreamToString(InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder stringbuilder = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			stringbuilder.append(line + "\n");
		}
		in.close();
		return stringbuilder.toString();
	}

	public static JLabel[] convertStringArrayToLabels(String[] labelNames) {
		JLabel[] labels = new JLabel[labelNames.length];
		int x = 0;
		while (x < labelNames.length) {
			JLabel headerlabel = new JLabel(labelNames[x]);
			headerlabel.setFont(new Font("Serif", Font.BOLD, 12));
			labels[x] = headerlabel;
			x++;
		}
		return labels;
	}

	public static void copyFile(File source, File dest) throws IOException {
		Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Create image icon for tree nodes. <br>
	 *
	 * @return <code>ImageIcon</code> of tree user objects
	 * @param path reference to image file name
	 */
	public static ImageIcon createImageIcon(String path) {
		URL imgURL = null;
		try {
			imgURL = new URL("file:" + getConfigPath() + path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static void deleteFile(String fileName) {
		try {
			File file = new File(fileName);
			if (file.delete()) {
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<ArrayList<Object>> emptyTableData(int columnCount) {
		ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> rows = new ArrayList<Object>();
		for (int i = 0; i < columnCount; i++) {
			rows.add("");
		}
		data.add(rows);
		return data;
	}

	public static void exitApp() {
		System.exit(0);
	}

	public static String[] filterDirectories(String rootPath) {
		File file = new File(rootPath);
		String[] directories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		return directories;
	}

	public static String formatJSON(String input) {
		String indented = "";
		ObjectMapper mapper = new ObjectMapper();
		Object json = null;
		try {
			json = mapper.readValue(java.net.URLDecoder.decode(input, "UTF-8"), Object.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return indented;
	}

	public static PropertiesTabbedPane generateEC2ObjectPropertiesPane(Object nodeObject, JScrollableDesktopPane jScrollableDesktopPan) {
		PropertiesTabbedPane propertiesTabbedPane = new PropertiesTabbedPane(nodeObject, ((CustomAWSObject) nodeObject).getAccount(), jScrollableDesktopPan);
		int n = 0;
		int m = 0;
		for (Map.Entry<String, String> entry : ((CustomAWSObject) nodeObject).getpropertiesPaneTabs().entrySet()) {
			if (entry.getValue().equals("ListInfoPane")) {
				propertiesTabbedPane.addListInfoPane(entry.getKey(), ((CustomAWSObject) nodeObject).getListTabHeaders(entry.getKey()), ((CustomAWSObject) nodeObject).getListTabToolTips(entry.getKey()),
						((CustomAWSObject) nodeObject).getAssociatedImage(), n, ((CustomAWSObject) nodeObject).getkeyEvents().get(n));
				propertiesTabbedPane.addResizableTabsHeights(((CustomAWSObject) nodeObject).getPropertyPanelsFieldsCount().get(m));
				propertiesTabbedPane.addrResizableTabsIndex(n);
				propertiesTabbedPane.addTabNeedResize(true);
				m++;
			} else if (entry.getValue().equals("TableInfoPane")) {
				for (Map.Entry<String[][], String[][][]> entry1 : ((CustomAWSObject) nodeObject).getPropertiesPaneTableParams().entrySet()) {
					int x = 0;
					while (x < entry1.getKey().length) {
						int y = 0;
						while (y < entry1.getKey()[x].length) {
							if (entry.getKey().equals(entry1.getKey()[x][y])) {
								propertiesTabbedPane.addTableInfoPane(entry1.getKey()[x], entry1.getValue()[x], ((CustomAWSObject) nodeObject).getTableTabHeaders(entry.getKey()), ((CustomAWSObject) nodeObject).getTableTabToolTips(entry.getKey()),
										((CustomAWSObject) nodeObject).getAssociatedImage(), n, ((CustomAWSObject) nodeObject).getkeyEvents().get(n));
								
							}
							y++;
						}
						x++;
					}
					
				}
				propertiesTabbedPane.addResizableTabsHeights(10);
				propertiesTabbedPane.addrResizableTabsIndex(n);
				propertiesTabbedPane.addTabNeedResize(true);
			} else if (entry.getValue().equals("DocumentInfoPane")) {
				propertiesTabbedPane.addDocumentInfoPane(entry.getKey(), ((CustomAWSObject) nodeObject).getDocumentTabHeaders(entry.getKey()), ((CustomAWSObject) nodeObject).getDocumentTabToolTips(entry.getKey()),
						((CustomAWSObject) nodeObject).getAssociatedImage(), n, ((CustomAWSObject) nodeObject).getkeyEvents().get(n));
				propertiesTabbedPane.addResizableTabsHeights(1);
				propertiesTabbedPane.addrResizableTabsIndex(n);
				propertiesTabbedPane.addTabNeedResize(false);
			}
			n++;
		}
		propertiesTabbedPane.setPaneSelection(n - 1);
		return propertiesTabbedPane;
	}

	// Retrieve path for aws configs - users\<user_home>\.aws
	public static File getAWSAPIINIConfigs(String configFileName) {
		String awsCredentialsINIFilePath = System.getProperty("user.home") + "\\.aws\\" + configFileName;
		File awsCredentialsINIFile = new File(awsCredentialsINIFilePath);
		return awsCredentialsINIFile;
	}

	public static ArrayList<ArrayList<Object>> getAWSObjectTagsData(Iterator<Tag> tags) {
		ArrayList<ArrayList<Object>> tagsData = new ArrayList<ArrayList<Object>>();
		while (tags.hasNext()) {
			Tag tag = tags.next();
			ArrayList<Object> tagData = new ArrayList<Object>();
			tagData.add(tag.getKey());
			tagData.add(tag.getValue());
			tagsData.add(tagData);
		}
		return tagsData;
	}

	public static ArrayList<ArrayList<Object>> getAWSObjectTagsDEscriptionData(Iterator<TagDescription> tags) {
		ArrayList<ArrayList<Object>> tagsData = new ArrayList<ArrayList<Object>>();
		while (tags.hasNext()) {
			TagDescription tag = tags.next();
			ArrayList<Object> tagData = new ArrayList<Object>();
			tagData.add(tag.getKey());
			tagData.add(tag.getValue());
			tagsData.add(tagData);
		}
		return tagsData;
	}

	public static ArrayList<ArrayList<Object>> getAWSRoute53ObjectTagsData(Iterator<com.amazonaws.services.route53.model.Tag> tags) {
		ArrayList<ArrayList<Object>> tagsData = new ArrayList<ArrayList<Object>>();
		while (tags.hasNext()) {
			com.amazonaws.services.route53.model.Tag tag = tags.next();
			ArrayList<Object> tagData = new ArrayList<Object>();
			tagData.add(tag.getKey());
			tagData.add(tag.getValue());
			tagsData.add(tagData);
		}
		return tagsData;
	}

	public static JTextPane getCommaDelimitedLabel(List<String> list) {
		Iterator<String> li = list.iterator();
		String labelString = "";
		while (li.hasNext()) {
			labelString = labelString + li.next() + " , ";
		}
		labelString = labelString.substring(0, labelString.length() - 2);
		LabelLikeTextPane labelLikeTextPane = new LabelLikeTextPane(labelString, false);
		return labelLikeTextPane;
	}

	// Retrieve the path of the root of the solution - src\
	public static String getConfigPath() {
		String binPath = UtilMethodsFactory.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String configPath = binPath.substring(0, binPath.indexOf(binPath.split("/")[binPath.split("/").length - 1]));
		return configPath;
	}

	/**
	 * Retrieve path for this console config - src\config.ini <br>
	 *
	 * @return <code>File</code> object representing Dashboard configuration file
	 */
	public static File getConsoleConfig() {
		String confPath = getConfigPath() + "config.ini";
		File configINIFile = new File(confPath);
		return configINIFile;
	}

	// Retrieve name tag
	public static String getEC2ObjectFilterTag(List<Tag> tagsData, String filterTag) {
		String volumeName = "";
		Iterator<Tag> it = tagsData.iterator();
		while (it.hasNext()) {
			Tag tag = it.next();
			if (tag.getKey().startsWith(filterTag)) {
				volumeName = tag.getValue();
			}
		}
		return volumeName;
	}

	public static StandardPBEStringEncryptor getEncryptor() {
		SimplePBEConfig config = new SimplePBEConfig();
		config.setAlgorithm("PBEWithMD5AndTripleDES");
		config.setKeyObtentionIterations(1000);
		config.setPassword("propertiesFilePassword");
		StandardPBEStringEncryptor encryptor = new org.jasypt.encryption.pbe.StandardPBEStringEncryptor();
		encryptor.setConfig(config);
		encryptor.initialize();
		return encryptor;
	}

	public static String[] getFileNames() {
		String binPath = UtilMethodsFactory.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String configPath = binPath.substring(0, binPath.indexOf(binPath.split("/")[binPath.split("/").length - 1])) + "/images";
		File folder = new File(configPath);
		String[] files = folder.list();
		return files;
	}

	public static String getListObjectType(List<Object> list) {
		String objectType = "";
		Iterator<Object> li1 = list.iterator();
		while (li1.hasNext()) {
			Object obj = li1.next();
			if (obj instanceof String) {
				objectType = "String";
			} else if (obj instanceof Instance) {
				objectType = "ELBInstance";
			} else if (obj instanceof CustomEC2ELBV2) {
				objectType = "CustomEC2ELBV2";
			} else if (obj instanceof CustomEC2TargetGroup) {
				objectType = "CustomEC2TargetGroup";
			} else if (obj instanceof CustomEC2ELB) {
				objectType = "CustomEC2ELB";
			} else if (obj instanceof InstanceBlockDeviceMapping) {
				objectType = "InstanceBlockDeviceMapping";
			} else if (obj instanceof BlockDeviceMapping) {
				objectType = "BlockDeviceMapping";
			} else if (obj instanceof GroupIdentifier) {
				objectType = "GroupIdentifier";
			} else if (obj instanceof InstanceNetworkInterface) {
				objectType = "InstanceNetworkInterface";
			} else if (obj instanceof VolumeAttachment) {
				objectType = "VolumeAttachment";
			} else if (obj instanceof AvailabilityZone) {
				objectType = "AvailabilityZone";
			} else if (obj instanceof Subnet) {
				objectType = "Subnet";
			}
			break;
		}
		return objectType;
	}

	public static String getListTabsData(String tableIdentifier, String[] keys, String[] values) {
		String tablePaneToolTip = "";
		int x = 0;
		while (x < keys.length) {
			if (keys[x].equals(tableIdentifier)) {
				tablePaneToolTip = values[x];
			}
			x++;
		}
		return tablePaneToolTip;
	}

	public static String getMatchString(AWSAccount theAWSAccount) {
		String matching = "";
		Iterator<InterfaceFilter> accountFilters = theAWSAccount.getFilters().iterator();
		String filtersMatchString = "(.*)(?i)";
		boolean filtersSet = false;
		while (accountFilters.hasNext()) {
			InterfaceFilter interfaceFilter = accountFilters.next();
			String filterString = interfaceFilter.getFilterString();
			if (interfaceFilter.isFilterState()) {
				filtersSet = true;
				if (filterString.length() > 1) {
					filtersMatchString = filtersMatchString + filterString + "(.*)|(.*)";
				}
				if (filtersMatchString.length() > 6) {
					matching = filtersMatchString.substring(0, filtersMatchString.length() - 5);
				}
			}
		}
		if (!filtersSet) {
			matching = ".+";
		}
		return matching;
	}

	public static String getPuttyCertPath() {
		String binPath = UtilMethodsFactory.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String configPath = binPath.substring(0, binPath.indexOf(binPath.split("/")[binPath.split("/").length - 1])).replace("/", "\\");
		String puttyConfigPath = configPath.substring(1, configPath.length());
		return puttyConfigPath;
	}

	public static String getTableTabsData(String tableIdentifier, String[][] keys, String[][] values) {
		String tablePaneToolTip = "";
		int n = 0;
		while (n < keys.length) {
			String[] key = keys[n];
			String[] value = values[n];
			int x = 0;
			while (x < key.length) {
				if (key[x].equals(tableIdentifier)) {
					tablePaneToolTip = value[x];
				}
				x++;
			}
			n++;
		}
		return tablePaneToolTip;
	}

	public static void openURLInBrowser(String url) {
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.browse(URI.create(url));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Populate tree nodes with respective icons
	public static HashMap<String, ImageIcon> populateInterfaceImages() {
		HashMap<String, ImageIcon> images = new HashMap<String, ImageIcon>();
		String[] objectIcons = getFileNames();
		for (String objectIcon : objectIcons) {
			ImageIcon icon = createImageIcon("images/" + objectIcon);
			images.put(objectIcon.split("\\.")[0], icon);
		}
		return images;
	}

	public static void reloadTableModelAfterRowRemoval(CustomTable table) {
		CustomTableModel model = (CustomTableModel) (table.getModel());
		int[] selection = table.getSelectedRows();
		model.removeRows(selection);
	}

	public static void removeAction(AWSAccount account, CustomAWSObject customAWSObject, DefaultMutableTreeNode node, JTree tree, Dashboard dash, String objectAction, String objectType) {
		int response = JOptionPane.showConfirmDialog(null, "Do you want to " + objectAction + " " + customAWSObject.getObjectAWSID() + "?", objectAction + " " + objectType, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.NO_OPTION) {
		} else if (response == JOptionPane.YES_OPTION) {
			customAWSObject.remove();
			CustomTreeContainer container = (CustomTreeContainer) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
			container.removeAWSObject(customAWSObject);
			((DefaultTreeModel) tree.getModel()).removeNodeFromParent(node);
			UtilMethodsFactory.replaceContainerTable(container, dash, node, tree);
		} else if (response == JOptionPane.CLOSED_OPTION) {
		}
	}

	public static void removeAction(CustomTable table, String objectAction, String objectType) {
		CustomAWSObject awsObject = null;
		List<String> confirmationStrings = new ArrayList<String>();
		CustomTreeContainer container = (CustomTreeContainer) table.getParentTreeNode().getUserObject();
		int z = 0;
		int[] selections = table.getSelectedRows();
		while (z < table.getSelectedRows().length) {
			awsObject = (CustomAWSObject) table.getModel().getValueAt(table.convertRowIndexToModel(selections[z]), 0);
			confirmationStrings.add(awsObject.getObjectName());
			z++;
		}
		String confirmationString = confirmationStrings.stream().collect(Collectors.joining(",\n"));
		int response = JOptionPane.showConfirmDialog(null, "Do you want to " + objectAction + "\n" + confirmationString + "\n" + objectType + "s?", objectType + "(s) " + objectAction, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.NO_OPTION) {
		} else if (response == JOptionPane.YES_OPTION) {
			int y = 0;
			while (y < table.getSelectedRows().length) {
				awsObject = (CustomAWSObject) table.getModel().getValueAt(table.convertRowIndexToModel(selections[y]), 0);
				awsObject.remove();
				int x = 0;
				while (x < table.getParentTreeNode().getChildCount()) {
					DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) table.getParentTreeNode().getChildAt(x);
					if (((CustomAWSObject) theNode.getUserObject()).getObjectName().contains(awsObject.getObjectName())) {
						((DefaultTreeModel) table.getTree().getModel()).removeNodeFromParent(theNode);
						container.removeAWSObject(awsObject);
					}
					x++;
				}
				y++;
			}
		} else if (response == JOptionPane.CLOSED_OPTION) {
		}
		UtilMethodsFactory.replaceContainerTable(table.getParentTreeNode().getUserObject(), table.getDash(), table.getParentTreeNode(), table.getTree());
	}

	// Replace container table based on updated container object
	public static void replaceContainerTable(Object userObject, Dashboard dash, DefaultMutableTreeNode node, JTree tree) {
		CustomTable table = null;
		String frameTitle = "";
		JScrollableDesktopPane pane = dash.getJScrollableDesktopPane();
		if (userObject instanceof CustomTreeContainer) {
			String containerChildrenType = ((CustomTreeContainer) userObject).getChildObject().getSimpleName();
			CustomTreeContainer container = (CustomTreeContainer) userObject;
			frameTitle = container.getContainerName() + " For " + container.getAccount().getAccountAlias();
			ArrayList<String> objectTableColumnHeadersArray = new ArrayList<String>(Arrays.asList(((CustomTreeContainer) userObject).getObjectTableColumnHeaders()));
			table = new CustomTable(container, objectTableColumnHeadersArray, pane, containerChildrenType, false);
			table.setAwsAccount(container.getAccount());
			container.setCustomTable(table);
		} else {
			AwsCommon container = (AwsCommon) userObject;
			ArrayList<String> accountsTableColumnHeadersArray = new ArrayList<String>(Arrays.asList(awsAccountsTableColumnHeaders));
			table = new CustomTable(container, accountsTableColumnHeadersArray, pane, "AWSAccounts", false);
		}
		table.setParentTreeNode(node);
		table.setTree(tree);
		table.setDash(dash);
		BaseInternalFrame theFrame = new CustomTableViewInternalFrame(frameTitle, table);
		addInternalFrameToScrolableDesctopPane(frameTitle, pane, theFrame);
	}

	public static void showDialogToDesctop(String frameType, int width, int height, DefaultMutableTreeNode node, JTree tree, Dashboard dash, AWSAccount account, String parentFolder, Bucket s3Bucket, CustomTable table) {
		JDialog dialog = null;
		switch (frameType) {
		case "AddSecurityGroupFrame":
			dialog = new AddSecurityGroupFrame(account, node, tree, dash);
			break;
		case "AWSAccountFrame":
			dialog = new AWSAccountFrame(dash, node, tree);
			break;
		case "AddAppicationTreesFrame":
			dialog = new AddApplicationTreeFrame(table, dash);
			break;
		default:
			break;
		}
		dialog.setSize(width, height);
		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		// Determine the new location of the window
		int w = dialog.getSize().width;
		int h = dialog.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		// Move the window
		dialog.setLocation(x, y);
		dialog.setVisible(true);
	}

	public static CustomTableViewInternalFrame showFrame(Object nodeObject, JScrollableDesktopPane jScrollableDesktopPan) {
		String frameTitle = ((CustomAWSObject) nodeObject).getpropertiesPaneTitle();
		PropertiesTabbedPane propertiesTabbedPane = generateEC2ObjectPropertiesPane(nodeObject, jScrollableDesktopPan);
		CustomTableViewInternalFrame theFrame = new CustomTableViewInternalFrame(frameTitle, propertiesTabbedPane);
		addInternalFrameToScrolableDesctopPane(frameTitle, jScrollableDesktopPan, theFrame);
		return theFrame;
	}

	public static String upperCaseFirst(String value) {
		char[] array = value.toCharArray();
		array[0] = Character.toUpperCase(array[0]);
		return new String(array);
	}
	
	public static void writeToFile(String outputString, String fileName, boolean writable) {
		File fout = new File(fileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fout, writable);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		try {
			bw.write(outputString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
