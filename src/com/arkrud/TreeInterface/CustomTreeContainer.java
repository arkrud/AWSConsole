package com.arkrud.TreeInterface;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;

import com.arkrud.TableInterface.CustomTable;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.CustomObjects.CustomAWSObject;

public class CustomTreeContainer implements TreeNodeState {
	private ArrayList<Object> customObjects = new ArrayList<Object>();
	private String containerName = "";
	private String accountAlias = "";
	private AWSAccount account;
	private CustomTable customTable;
	private Class<?> childObject;
	private boolean selected;

	public Class<?> getChildObject() {
		return childObject;
	}

	public void setChildObject(Class<?> childObject) {
		this.childObject = childObject;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public AWSAccount getAccount() {
		return account;
	}

	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	public CustomTable getCustomTable() {
		return customTable;
	}

	public void setCustomTable(CustomTable customTable) {
		this.customTable = customTable;
	}

	public ArrayList<?> getEc2Objects() {
		return customObjects;
	}

	@SuppressWarnings("unchecked")
	public void setEc2Objects(ArrayList<?> theEC2Objects) {
		customObjects = (ArrayList<Object>) theEC2Objects;
	}

	public void addEC2Object(Object ec2Object) {
		customObjects.add(ec2Object);
	}

	public void removeEC2Object(Object ec2Object) {
		Iterator<?> ec2LCsIterator = customObjects.iterator();
		while (ec2LCsIterator.hasNext()) {
			Object customObject = ec2LCsIterator.next();
			if (customObject.equals(ec2Object)) {
				ec2LCsIterator.remove();
			}
		}
	}

	// Factory Methods
	public ImageIcon getAssociatedImage() {
		Object obj = null;
		try {
			obj = childObject.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ((CustomAWSObject) obj).getAssociatedContainerImage();
	}

	public String[] getObjectTableColumnHeaders() {
		Object obj = null;
		try {
			obj = childObject.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ((CustomAWSObject) obj).defineTableColumnHeaders();
	}

	public ArrayList<?> getFilteredAWSObjects(AWSAccount account, String appFilter) {
		Object obj = null;
		try {
			obj = childObject.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			((CustomAWSObject) obj).getFilteredAWSObjects(account, appFilter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ((CustomAWSObject) obj).getFilteredAWSObjects(account, appFilter);
	}

	@Override
	public String getNodeText() {
		return getContainerName();
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getAWSAccountAlias() {
		return accountAlias;
	}

	@Override
	public void setAWSAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}

	@Override
	public String getNodeScreenName() {
		// TODO Auto-generated method stub
		return getContainerName();
	}
}
