package com.arkrud.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.commons.codec.binary.Base64;

import com.arkrud.Shareware.SpringUtilities;
import com.arkrud.TableInterface.CustomTable;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.CustomObjects.CFStackTreeNodeUserObject;
import com.arkrud.aws.CustomObjects.CustomAPIGateway;
import com.arkrud.aws.CustomObjects.CustomEC2LC;
import com.arkrud.aws.CustomObjects.CustomIAMRole;
import com.arkrud.aws.CustomObjects.CustomLambdaFunction;
import com.arkrud.aws.CustomObjects.CustomSNSTopic;
import com.arkrud.aws.CustomObjects.CustomVpcEndpoint;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class PropertiesTabbedPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private CustomTableViewInternalFrame theTableViewInternalFrame;
	private JScrollableDesktopPane jScrollableDesktopPan;
	private JTabbedPane tabbedPane;
	private AWSAccount account;
	private Object userObject;
	private ArrayList<Integer> resizableTabsIndex = new ArrayList<Integer>();
	private ArrayList<Integer> resizableTabsHeights = new ArrayList<Integer>();
	private ArrayList<Boolean> tabNeedResize = new ArrayList<Boolean>();

	public PropertiesTabbedPane(Object userObject, AWSAccount account, JScrollableDesktopPane jScrollableDesktopPan) {
		this.jScrollableDesktopPan = jScrollableDesktopPan;
		this.account = account;
		this.userObject = userObject;
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.NORTH);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	public void addrResizableTabsIndex(int index) {
		resizableTabsIndex.add(index);
	}

	public void addResizableTabsHeights(int height) {
		resizableTabsHeights.add(height);
	}

	public void addTabNeedResize(boolean needResize) {
		tabNeedResize.add(needResize);
	}

	public void addListInfoPane(String dataFlag, String paneTitle, String paneTip, ImageIcon icon, int tabIndex, int mnemonic) {
		OverviewPanel detailsPanel = new OverviewPanel(userObject, account, jScrollableDesktopPan, dataFlag);
		tabbedPane.addTab(paneTitle, icon, detailsPanel, paneTip);
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int x = 0;
				while (x < tabbedPane.getTabCount()) {
					int y = 0;
					while (y < resizableTabsIndex.size()) {
						int tabIndex = resizableTabsIndex.get(y);
						if (tabbedPane.getSelectedIndex() == tabIndex) {
							if (tabNeedResize.get(y)) {
								tabbedPane.setPreferredSize(new Dimension(1, resizableTabsHeights.get(y) * 50));
							}
						}
						y++;
					}
					x++;
				}
			}
		});
		// tabbedPane.setMnemonicAt(tabIndex, mnemonic);
	}

	public void addTableInfoPane(String[] dataFlags, String[][] tagsColumnHeadersArray, String paneTitle, String paneTip, ImageIcon icon, int tabIndex, int mnemonic) {
		JPanel instancesAndSubnetsPanel = new JPanel(new SpringLayout());
		int x = 0;
		while (x < dataFlags.length) {
			ArrayList<String> tableColumnHeadersArray = new ArrayList<String>(Arrays.asList(tagsColumnHeadersArray[x]));
			CustomTable infoTable = new CustomTable(userObject, tableColumnHeadersArray, jScrollableDesktopPan, dataFlags[x], false);
			x++;
			JScrollPane infoPane = new JScrollPane(infoTable);
			instancesAndSubnetsPanel.add(infoPane);
		}
		SpringUtilities.makeCompactGrid(instancesAndSubnetsPanel, dataFlags.length, 1, 0, 0, 0, 0);
		tabbedPane.addTab(paneTitle, icon, instancesAndSubnetsPanel, paneTip);
		tabbedPane.setMnemonicAt(tabIndex, mnemonic);
	}

	public void addDocumentInfoPane(String dataFlag, String paneTitle, String paneTip, ImageIcon icon, int tabIndex, int mnemonic) {
		JPanel docPanel = new JPanel();
		docPanel.setLayout(new BorderLayout());
		JTextPane textPane = new JTextPane();
		StyledDocument doc = (StyledDocument) textPane.getDocument();
		Style style = doc.addStyle("StyleName", null);
		StyleConstants.setForeground(style, Color.red);
		((AbstractDocument) textPane.getDocument()).setDocumentFilter(new CustomDocumentFilter(textPane));
		try {
			if (dataFlag.equals("StackTemplate")) {
				doc.insertString(doc.getLength(), CFStackTreeNodeUserObject.getStacksTemplate(account, ((CFStackTreeNodeUserObject) userObject).getStack().getStackName()), style);
			} else if (dataFlag.equals("RolePolicyDocument")) {
				try {
					doc.insertString(doc.getLength(), java.net.URLDecoder.decode(UtilMethodsFactory.formatJSON(((CustomIAMRole) userObject).getAssumeRolePolicyDocument()), "UTF-8"), style);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else if (dataFlag.contains("RolePolicy")) {
				List<String> rolePolices = CustomIAMRole.getIAMRolePolices(((CustomIAMRole) userObject).getAccount().getAccountAlias(), ((CustomIAMRole) userObject).getRoleName());
				int x = 0;
				while (x < rolePolices.size()) {
					String policy = CustomIAMRole.getIAMRolePolicyDocument(rolePolices.get(x), ((CustomIAMRole) userObject).getRoleName(), ((CustomIAMRole) userObject).getAccount().getAccountAlias());
					try {
						doc.insertString(doc.getLength(), java.net.URLDecoder.decode(UtilMethodsFactory.formatJSON(policy), "UTF-8"), style);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					x++;
				}
			} else if (dataFlag.contains("ResourcePolicy")) {
				String policy = ((CustomAPIGateway) userObject).getPolicy();
				if (policy != null) {
					try {
						doc.insertString(doc.getLength(), java.net.URLDecoder.decode(UtilMethodsFactory.formatJSON(((CustomAPIGateway) userObject).getPolicy().replace("\\", "")), "UTF-8"), style);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			} else if (dataFlag.contains("ResourceBasedPolicy")) {
				String policy = ((CustomLambdaFunction) userObject).getPolicy();
				if (policy != null) {
					try {
						doc.insertString(doc.getLength(), java.net.URLDecoder.decode(UtilMethodsFactory.formatJSON(((CustomLambdaFunction) userObject).getPolicy().replace("\\", "")), "UTF-8"), style);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			} else if (dataFlag.contains("IAMRole")) {
				String policy = ((CustomLambdaFunction) userObject).getRolePolicyDocumentText();
				if (policy != null) {
					try {
						doc.insertString(doc.getLength(), java.net.URLDecoder.decode(UtilMethodsFactory.formatJSON(((CustomLambdaFunction) userObject).getRolePolicyDocumentText().replace("\\", "")), "UTF-8"), style);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			} else if (dataFlag.contains("SNSTopicAccessPolicy")) {
				String policy = ((CustomSNSTopic) userObject).getSNSTopicAccessPolicy();
				if (policy != null) {
					try {
						doc.insertString(doc.getLength(), java.net.URLDecoder.decode(UtilMethodsFactory.formatJSON(((CustomSNSTopic) userObject).getSNSTopicAccessPolicy().replace("\\", "")), "UTF-8"), style);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			} else if (dataFlag.contains("SNSTopicEffectiveDeliveryPolicy")) {
				String policy = ((CustomSNSTopic) userObject).getSNSTopicEffectiveDeliveryPolicy();
				if (policy != null) {
					try {
						doc.insertString(doc.getLength(), java.net.URLDecoder.decode(UtilMethodsFactory.formatJSON(((CustomSNSTopic) userObject).getSNSTopicEffectiveDeliveryPolicy().replace("\\", "")), "UTF-8"), style);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			} else if (dataFlag.contains("VPCEndpointPolicy")) {
				String policy = ((CustomVpcEndpoint) userObject).getPolicyDocument();
				if (policy != null) {
					try {
						doc.insertString(doc.getLength(), java.net.URLDecoder.decode(UtilMethodsFactory.formatJSON(((CustomVpcEndpoint) userObject).getPolicyDocument().replace("\\", "")), "UTF-8"), style);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			} else if (dataFlag.contains("LCUserData")) {
				byte[] valueDecoded = Base64.decodeBase64(((CustomEC2LC) userObject).getUserData());
				doc.insertString(doc.getLength(), new String(valueDecoded), style);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		JScrollPane docScrollPane = new JScrollPane(textPane);
		// docScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		docScrollPane.setMinimumSize(textPane.getPreferredSize());
		docPanel.add(docScrollPane);
		tabbedPane.addTab(paneTitle, icon, docPanel, paneTip);
		tabbedPane.setMnemonicAt(tabIndex, mnemonic);
	}

	public void setPaneSelection(int index) {
		tabbedPane.setSelectedIndex(index);
	}

	public void setContainigFrame(CustomTableViewInternalFrame tableViewInternalFrame) {
		theTableViewInternalFrame = tableViewInternalFrame;
	}

	public CustomTableViewInternalFrame getContainigFrame() {
		return theTableViewInternalFrame;
	}

	public JScrollableDesktopPane getjScrollableDesktopPan() {
		return jScrollableDesktopPan;
	}

	public void setjScrollableDesktopPan(JScrollableDesktopPane jScrollableDesktopPan) {
		this.jScrollableDesktopPan = jScrollableDesktopPan;
	}
}
