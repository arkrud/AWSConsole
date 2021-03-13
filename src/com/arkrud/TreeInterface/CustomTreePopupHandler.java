package com.arkrud.TreeInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FilenameUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.CustomProgressBar;
import com.arkrud.UI.InterfaceFilter;
import com.arkrud.UI.IntrfaceFilterObject;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.UI.Dashboard.Dashboard;
import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AWSService;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.S3Folder;
import com.arkrud.aws.CustomObjects.CustomAPIGateway;
import com.arkrud.aws.CustomObjects.CustomAWSObject;
import com.arkrud.aws.StaticFactories.S3Common;
import com.tomtessier.scrollabledesktop.BaseInternalFrame;

public class CustomTreePopupHandler implements ActionListener, PropertyChangeListener {
	private JTree tree;
	private TreePath path;
	private Dashboard dash;
	private JFileChooser fc;
	private File file;
	private Bucket s3Bucket;
	private S3Folder s3Folder;
	private Upload upload;
	private CustomTreeMouseListener cml;
	private MultipleFileUpload multiUpload;
	private static TransferManager tx;
	private Bucket theBucket;
	private String theFolderName = "";
	private String topUploadDir = "";
	private DefaultTreeModel model;
	private AWSService awsService;
	private CustomTree theTree;

	public CustomTreePopupHandler(JTree tree, JPopupMenu popup, Dashboard dash, CustomTree theTree) {
		// Pass variables values into the class
		this.tree = tree;
		this.dash = dash;
		this.theTree = theTree;
		// Add Mouse listener to control which menu items will show up in drop-down menu
		cml = new CustomTreeMouseListener(popup, tree, dash);
		tree.addMouseListener(cml);
	}

	// Perform actions on drop-down menu selections
	@Override
	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();
		AWSAccount account = null;
		DefaultMutableTreeNode node = null;
		model = (DefaultTreeModel) tree.getModel();
		path = tree.getPathForLocation(cml.getLoc().x, cml.getLoc().y);
		node = (DefaultMutableTreeNode) path.getLastPathComponent();
		Object obj = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
		// For leaf objects
		if (obj instanceof AWSAccount) {
			account = (AWSAccount) node.getUserObject();
			if (ac.equals("REFRESH")) {
				theTree.refreshTreeNodeWithProgress(node, false, null);
			}
		} else if (obj instanceof AwsCommon) {
			if (ac.equals("ADD AWS ACCOUNT")) {
				UtilMethodsFactory.showDialogToDesctop("AWSAccountFrame", 400, 200, node, tree, dash, null, null, null, null);
			} else if (ac.equals("MANAGE AWS ACCOUNTS")) {
				UtilMethodsFactory.replaceContainerTable(new AwsCommon("All Applications"), dash, node, tree);
				UtilMethodsFactory.getFileNames();
			} else if (ac.equals("REFRESH")) {
				theTree.refreshTreeNodeWithProgress(node, false, null);
			} else if (ac.equals("ADD FILTER")) {
				String s = (String) JOptionPane.showInputDialog(dash, "New Filter String", "New Filter Dialog", JOptionPane.PLAIN_MESSAGE, null, null, null);
				if ((s != null) && (s.length() > 0)) {
					InterfaceFilter interfaceFilter = new InterfaceFilter();
					interfaceFilter.setAWSAccountAlias(((AwsCommon) node.getUserObject()).getAWSAccountAlias());
					IntrfaceFilterObject intrfaceFilterObject = new IntrfaceFilterObject();
					intrfaceFilterObject.setFilterItemName(s + "-filter");
					intrfaceFilterObject.setFilterItemVelue(s);
					intrfaceFilterObject.setFilterStateItemName(s + "-flag");
					intrfaceFilterObject.setFilterStareItemValue(true);
					interfaceFilter.setIfo(intrfaceFilterObject);
					INIFilesFactory.addINIFileItemToSection(UtilMethodsFactory.getConsoleConfig(), ((AwsCommon) node.getUserObject()).getAWSAccountAlias(), s + "-filter", s);
					INIFilesFactory.addINIFileItemToSection(UtilMethodsFactory.getConsoleConfig(), ((AwsCommon) node.getUserObject()).getAWSAccountAlias(), s + "-flag", true);
					((AwsCommon) node.getUserObject()).getAccount().addFilter(interfaceFilter);
					DefaultMutableTreeNode filterNode = new DefaultMutableTreeNode(interfaceFilter);
					((DefaultTreeModel) tree.getModel()).insertNodeInto(filterNode, node, 0);
				}
			}
		} else if (obj instanceof AWSService) {
			account = ((AWSService) node.getUserObject()).getTheAccount();
			if (ac.equals("ADD BUCKET")) {
				UtilMethodsFactory.showDialogToDesctop("AddS3BucketFrame", 450, 110, node, tree, null, account, null, null, null);
			} else if (ac.equals("CREATE CF STACK")) {
				// UtilMethods.addFrameToDesctop("CFCreateStackFrame", account, null, null, dash);
			} else if (ac.equals("REFRESH")) {
				theTree.refreshTreeNodeWithProgress(node, false, null);
			}
		} else if (obj instanceof Bucket) {
			s3Bucket = ((Bucket) node.getUserObject());
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
			awsService = (AWSService) parentNode.getUserObject();
			account = awsService.getTheAccount();
			if (ac.equals("DELETE BUCKET")) {
				deleteBucket(account, s3Bucket, node, model);
			} else if (ac.equals("ADD FOLDER")) {
				UtilMethodsFactory.showDialogToDesctop("AddS3FolderFrame", 450, 110, node, tree, null, s3Folder.getFolderAccount(), null, s3Bucket, null);
			}
		} else if (obj instanceof S3Folder) {
			s3Folder = ((S3Folder) node.getUserObject());
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
			if (parentNode.getUserObject().getClass().getName().contains("Bucket")) {
				s3Bucket = ((Bucket) parentNode.getUserObject());
			} else if (parentNode.getUserObject().getClass().getName().contains("S3Folder")) {
				s3Bucket = ((S3Folder) parentNode.getUserObject()).getBucket();
			}
			account = s3Folder.getFolderAccount();
			if (ac.equals("DELETE FOLDER")) {
				deleteFolder(s3Folder, account.getAccountAlias(), s3Bucket, node, model);
			} else if (ac.equals("ADD FOLDER")) {
				UtilMethodsFactory.showDialogToDesctop("AddS3FolderFrame", 450, 110, node, tree, null, s3Folder.getFolderAccount(), s3Folder.getFolderPath(), s3Bucket, null);
			} else if (ac.equals("UPLOAD DATA")) {
				uploadToS3(account, s3Bucket, s3Folder.getFolderPath(), node, model);
			}
		} else if (obj instanceof InterfaceFilter) {
			if (ac.equals("DELETE FILTER")) {
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
				((AwsCommon) parentNode.getUserObject()).getAccount().removeFilter((InterfaceFilter) obj);
				String[] filterData = { ((InterfaceFilter) obj).getIfo().getFilterItemName(), ((InterfaceFilter) obj).getIfo().getFilterStateItemName() };
				INIFilesFactory.removeINIFileItems(UtilMethodsFactory.getConsoleConfig(), ((AwsCommon) parentNode.getUserObject()).getAccount().getAccountAlias(), filterData);
				model.removeNodeFromParent(node);
			}
			// For container objects
		} else if (obj instanceof CustomTreeContainer) {
			CustomTreeContainer container = ((CustomTreeContainer) node.getUserObject());
			account = container.getAccount();
			if (ac.equals("REFRESH")) {
		 		theTree.refreshTreeNodeWithProgress(node, false, null);
			} else if (ac.equals("ADD SECURITY GROUP")) {
				UtilMethodsFactory.showDialogToDesctop("AddSecurityGroupFrame", 300, 160, node, tree, dash, account, null, null, null);
			}
		} else {
			((CustomAWSObject) node.getUserObject()).performTreeActions((CustomAWSObject) node.getUserObject(), node, tree, dash, ac);
		}
	}

	private void removeAWSAccount(AWSAccount account, DefaultMutableTreeNode node) {
		int response = JOptionPane.showConfirmDialog(null, "Do you want to remove AWS Account " + account.getAccountAlias() + "?", "AWS Account Removal", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.NO_OPTION) {
		} else if (response == JOptionPane.YES_OPTION) {
			INIFilesFactory.removeAccountFromINIFile(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"), account.getAccountAlias());
			INIFilesFactory.removeAccountFromINIFile(UtilMethodsFactory.getConsoleConfig(), account.getAccountAlias());
			model.removeNodeFromParent(node);
			UtilMethodsFactory.replaceContainerTable(new AwsCommon("All Applications"), dash, node, tree);
		} else if (response == JOptionPane.CLOSED_OPTION) {
		}
	}

	private void deleteFolder(S3Folder s3Folder, String accountString, Bucket s3Bucketname, DefaultMutableTreeNode node, DefaultTreeModel model) {
		int response = JOptionPane.showConfirmDialog(null, "Do you want to delete " + s3Folder.getFolderPath() + "?", "S3 Folder Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.NO_OPTION) {
		} else if (response == JOptionPane.YES_OPTION) {
			S3Common.deleteFolderInBacket(S3Common.connectToS3(AwsCommon.getAWSCredentials(accountString)), s3Bucketname.getName(), s3Folder.getFolderPath());
			model.removeNodeFromParent(node);
			String orphansCommonPath = s3Folder.getFolderAccount().getAccountAlias() + " - " + s3Folder.getBucket().getName() + " - " + s3Folder.getFolderPath();
			UtilMethodsFactory.closeOrphanWindows(orphansCommonPath, dash.getJScrollableDesktopPane());
		} else if (response == JOptionPane.CLOSED_OPTION) {
		}
	}

	private void deleteBucket(AWSAccount account, Bucket s3Bucketname, DefaultMutableTreeNode node, DefaultTreeModel model) {
		int response = JOptionPane.showConfirmDialog(null, "Do you want to delete " + s3Bucketname + "?", "S3 Bucket Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.NO_OPTION) {
		} else if (response == JOptionPane.YES_OPTION) {
			S3Common.deleteS3Buckets(S3Common.connectToS3(AwsCommon.getAWSCredentials(account.getAccountAlias())), s3Bucketname.getName());
			model.removeNodeFromParent(node);
		} else if (response == JOptionPane.CLOSED_OPTION) {
		}
	}

	@SuppressWarnings("deprecation")
	private void uploadToS3(AWSAccount account, Bucket bucketName, String folderName, DefaultMutableTreeNode node, DefaultTreeModel model) {
		theBucket = bucketName;
		theFolderName = folderName;
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showOpenDialog(dash);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			tx = new TransferManager(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			if (file.isDirectory()) {
				s3Folder = new S3Folder(file.getName(), bucketName, account, theFolderName + file.getName() + "/");
				DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(s3Folder);
				model.insertNodeInto(topNode, node, 0);
				final CustomProgressBar progFrame = new CustomProgressBar(true, false, "Loading EC2 Objects");
				progFrame.getPb().setIndeterminate(true);
				// Save uploaded folder path into variable to use later to build corresponding S3 Folders structure
				topUploadDir = file.getParent();
				buildFolderTreeContents(theFolderName, file, account, theBucket, topNode, model);
				SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						multiUpload = tx.uploadDirectory(theBucket.getName(), theFolderName + file.getName(), file, true);
						try {
							multiUpload.waitForCompletion();
						} catch (AmazonServiceException e) {
							e.printStackTrace();
						} catch (AmazonClientException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return null;
					};

					// This is called when the SwingWorker's doInBackground finishes
					@Override
					protected void done() {
						progFrame.getPb().setIndeterminate(false);
						// Hide progress bar JFrame
						progFrame.setVisible(false);
					};
				};
				w.addPropertyChangeListener(this);
				w.execute();
				progFrame.setVisible(true);
			} else {
				final CustomProgressBar myProgressBar = new CustomProgressBar(false, false, "Uploading...");
				PutObjectRequest request;
				if (FilenameUtils.getExtension(file.getName()).isEmpty()) {
					request = new PutObjectRequest(bucketName.getName(), theFolderName + FilenameUtils.getBaseName(file.getName()) + ".", file);
				} else {
					request = new PutObjectRequest(bucketName.getName(), theFolderName + FilenameUtils.getBaseName(file.getName()) + "." + FilenameUtils.getExtension(file.getName()), file);
				}
				request.setGeneralProgressListener(new ProgressListener() {
					@Override
					public void progressChanged(ProgressEvent progressEvent) {
						myProgressBar.setVisible(true);
						JProgressBar pb = myProgressBar.getPb();
						pb.setValue((int) upload.getProgress().getPercentTransferred());
						switch (progressEvent.getEventType()) {
						case CLIENT_REQUEST_SUCCESS_EVENT:
							break;
						case CLIENT_REQUEST_FAILED_EVENT:
							break;
						case CLIENT_REQUEST_RETRY_EVENT:
							break;
						case CLIENT_REQUEST_STARTED_EVENT:
							break;
						case HTTP_REQUEST_COMPLETED_EVENT:
							break;
						case HTTP_REQUEST_CONTENT_RESET_EVENT:
							break;
						case HTTP_REQUEST_STARTED_EVENT:
							break;
						case HTTP_RESPONSE_COMPLETED_EVENT:
							break;
						case HTTP_RESPONSE_CONTENT_RESET_EVENT:
							break;
						case HTTP_RESPONSE_STARTED_EVENT:
							break;
						case REQUEST_BYTE_TRANSFER_EVENT:
							break;
						case REQUEST_CONTENT_LENGTH_EVENT:
							break;
						case RESPONSE_BYTE_DISCARD_EVENT:
							break;
						case RESPONSE_BYTE_TRANSFER_EVENT:
							break;
						case RESPONSE_CONTENT_LENGTH_EVENT:
							break;
						case TRANSFER_CANCELED_EVENT:
							break;
						case TRANSFER_COMPLETED_EVENT:
							pb.setValue(100);
							myProgressBar.dispose();
							String frameTitle = s3Folder.getFolderAccount().getAccountAlias() + " - " + s3Folder.getBucket().getName() + " - " + s3Folder.getFolderPath();
							ArrayList<String> columnHeaders = new ArrayList<String>();
							columnHeaders.add("Object");
							columnHeaders.add("Size");
							columnHeaders.add("Owner");
							CustomTable table = new CustomTable(s3Folder, columnHeaders, dash.getJScrollableDesktopPane(), "S3Folders", false);
							// table.setTransferHandler(dash.getArrayListTransferHandler());
							table.setS3Folder(s3Folder);
							table.setParentTreeNode((DefaultMutableTreeNode) path.getLastPathComponent());
							table.setParentTreeModel((DefaultTreeModel) tree.getModel());
							table.setDash(dash);
							table.setTree(tree);
							BaseInternalFrame theFrame = new CustomTableViewInternalFrame(frameTitle, table);
							UtilMethodsFactory.addInternalFrameToScrolableDesctopPane(frameTitle, dash.getJScrollableDesktopPane(), theFrame);
							break;
						case TRANSFER_FAILED_EVENT:
							break;
						case TRANSFER_PART_COMPLETED_EVENT:
							break;
						case TRANSFER_PART_FAILED_EVENT:
							break;
						case TRANSFER_PART_STARTED_EVENT:
							break;
						case TRANSFER_PREPARING_EVENT:
							break;
						case TRANSFER_STARTED_EVENT:
							break;
						default:
							break;
						}
					}
				});
				upload = tx.upload(request);
			}
		} else {
		}
	}

	// Build tree nodes for uploaded folder structure
	private void buildFolderTreeContents(String folderName, File dir, AWSAccount account, Bucket bucketName, DefaultMutableTreeNode topNode, DefaultTreeModel model) {
		S3Folder s3Folder;
		TreePath pathToExpand = null;
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					s3Folder = new S3Folder(file.getName(), bucketName, account, folderName + file.getCanonicalPath().replace("\\", "/").substring(topUploadDir.length() + 1) + "/");
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(s3Folder);
					model.insertNodeInto(node, topNode, 0);
					buildFolderTreeContents(folderName, file, account, bucketName, node, model);
					// Get the path for last tree node with the leaf in the uploaded folders hierarchy to expand all uploaded nodes
					pathToExpand = new TreePath(topNode.getPath());
				} else {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		tree.expandPath(pathToExpand);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}
}