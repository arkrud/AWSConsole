package com.arkrud.TableInterface;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.arkrud.Util.INIFilesFactory;
import com.arkrud.Util.UtilMethodsFactory;
import com.arkrud.aws.AWSAccount;

public class CustomTableUpdateAction extends AbstractAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Object tableDataObject;
	private String tableLabel;

	public CustomTableUpdateAction(Object tableDataObject, String tableLabel) {
		this.tableDataObject = tableDataObject;
		this.tableLabel = tableLabel;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		CustomTableCellListener tcl = (CustomTableCellListener) arg0.getSource();
		if (tableDataObject.getClass().toString().contains("AWSAccount") && tableLabel.contains("AWSAccounts")) {
			if (tcl.getColumn() == 0) {
				INIFilesFactory.renameINIFileSection(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"), (String) tcl.getOldValue(), (String) tcl.getNewValue());
			} else if (tcl.getColumn() == 3) {
				boolean test = INIFilesFactory.hasItemInSection(UtilMethodsFactory.getAWSAPIINIConfigs("config"), "profile " + ((AWSAccount) tcl.getTable().getValueAt(tcl.getRow(), 0)).getAccountAlias(),
						tcl.getTable().getColumnName(tcl.getColumn()).toLowerCase());
				if (test) {
					INIFilesFactory.updateINIFileItems(UtilMethodsFactory.getAWSAPIINIConfigs("config"), "profile " + ((AWSAccount) tcl.getTable().getValueAt(tcl.getRow(), 0)).getAccountAlias(), (String) tcl.getNewValue(),
							tcl.getTable().getColumnName(tcl.getColumn()));
				} else {
					INIFilesFactory.addINIFileItemToSection(UtilMethodsFactory.getAWSAPIINIConfigs("config"), (String) tcl.getTable().getValueAt(tcl.getRow(), 0), tcl.getTable().getColumnName(tcl.getColumn()).toLowerCase(),
							(String) tcl.getNewValue());
				}
			} else {
				boolean test = INIFilesFactory.hasItemInSection(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"), ((AWSAccount) tcl.getTable().getValueAt(tcl.getRow(), 0)).getAccountAlias(),
						tcl.getTable().getColumnName(tcl.getColumn()).toLowerCase());
				if (test) {
					INIFilesFactory.updateINIFileItems(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"), ((AWSAccount) tcl.getTable().getValueAt(tcl.getRow(), 0)).getAccountAlias(), (String) tcl.getNewValue(),
							tcl.getTable().getColumnName(tcl.getColumn()));
				} else {
					INIFilesFactory.addINIFileItemToSection(UtilMethodsFactory.getAWSAPIINIConfigs("credentials"), (String) tcl.getTable().getValueAt(tcl.getRow(), 0), tcl.getTable().getColumnName(tcl.getColumn()).toLowerCase(),
							(String) tcl.getNewValue());
				}
			}
		} else if (tableDataObject.getClass().toString().contains("AWSAccount") && tableLabel.contains("AddELBListenersSimple")) {
		} else if (tableDataObject.getClass().toString().contains("AWSAccount") && tableLabel.contains("AddELBListenersAdvanced")) {
		} else if (tableDataObject.getClass().toString().contains("AWSAccount") && tableLabel.contains("AddTags")) {
		} else {
		}
	}
}
