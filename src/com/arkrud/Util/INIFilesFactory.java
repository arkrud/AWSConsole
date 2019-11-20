package com.arkrud.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.dtools.ini.BasicIniFile;
import org.dtools.ini.FormatException;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniFileWriter;
import org.dtools.ini.IniItem;
import org.dtools.ini.IniSection;

import com.arkrud.UI.InterfaceFilter;
import com.arkrud.UI.IntrfaceFilterObject;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AWSService;

/**
 * Static methods used to work with .ini configuration files.<br>
 *
 */
public class INIFilesFactory {
	/**
	 * Check if section exist in INI. <br>
	 *
	 * @param iniFile INI File object
	 * @param sectionName INI file section name
	 *
	 * @return <code>true</code> is specific section present in INI file
	 *         <code>false</code> if section is not present in INI file
	 */
	public static boolean hasINIFileSection(File iniFile, String sectionName) {
		IniFile ini = readINI(iniFile);
		if (ini.getSection(sectionName) == null) {
			return false;
		}
		return true;
	}

	// Add new section to INI configuration file
	public static void addINIFileSection(File iniFile, String sectionName, HashMap<String, String> sectionKeys) {
		IniFile ini = readINI(iniFile);
		ini.addSection(sectionName);
		IniSection awsaccountSection = ini.getSection(sectionName);
		Set<String> keys = sectionKeys.keySet();
		for (String key : keys) {
			awsaccountSection.addItem(key);
			awsaccountSection.getItem(key).setValue(sectionKeys.get(key));
		}
		writeINI(iniFile, ini);
	}

	// Add new section to INI configuration file
	public static void addINIFileEmptySection(File iniFile, String sectionName) {
		IniFile ini = readINI(iniFile);
		ini.addSection(sectionName);
		writeINI(iniFile, ini);
	}

	// Rename INI file section (effectively add and remove section)
	public static void renameINIFileSection(File iniFile, String oldSectionName, String newSectionName) {
		IniFile ini = readINI(iniFile);
		IniSection oldAwsaccountSection = ini.getSection(oldSectionName);
		ini.addSection(newSectionName);
		IniSection awsaccountSection = ini.getSection(newSectionName);
		Iterator<IniItem> it = oldAwsaccountSection.getItems().iterator();
		while (it.hasNext()) {
			awsaccountSection.addItem(it.next());
		}
		ini.removeSection(oldSectionName);
		writeINI(iniFile, ini);
	}

	// Create AWS account object from INI configuration files
	public static ArrayList<AWSAccount> readAWSINIAccounts(File awsCredentialsINIFile, File consoleConfigFile, File awsConfigINIFile, boolean toRefresh) {
		ArrayList<AWSAccount> awsAccounts = new ArrayList<AWSAccount>();
		IniFile awsCredentialsIni = readINI(awsCredentialsINIFile);
		Iterator<IniSection> sections = awsCredentialsIni.getSections().iterator();
		while (sections.hasNext()) {
			AWSAccount account = new AWSAccount();
			IniSection theSection = sections.next();
			account.setAccountAlias(theSection.getName());
			Iterator<IniItem> params = theSection.getItems().iterator();
			while (params.hasNext()) {
				IniItem item = params.next();
				if (item.getName().contains("aws_access_key_id")) {
					account.setAccountKey(item.getValue());
				} else if (item.getName().contains("aws_secret_access_key")) {
					account.setAccountSecret(item.getValue());
				}
			}
			IniFile configINI = readINI(consoleConfigFile);
			Iterator<IniSection> configINISections = configINI.getSections().iterator();
			while (configINISections.hasNext()) {
				IniSection coonfigSection = configINISections.next();
				if (coonfigSection.getName().contains(theSection.getName())) {
					Iterator<IniItem> configParams = coonfigSection.getItems().iterator();
					InterfaceFilter interfaceFilter = null;
					IntrfaceFilterObject ifo = null;
					while (configParams.hasNext()) {
						IniItem sectionItem = configParams.next();
						for (String[] label : UtilMethodsFactory.awsServicesInfo) {
							if (sectionItem.getName().contains(label[0])) {
								if (toRefresh) {
									AWSService service = new AWSService();
									service.setAwsServiceName(label[0]);
									service.setAwsServiceDescription(label[1]);
									service.setTheAccount(account);
									account.addAWSService(service);
								} else {
									if (sectionItem.getValue().contains("true")) {
										AWSService service = new AWSService();
										service.setAwsServiceName(label[0]);
										service.setAwsServiceDescription(label[1]);
										service.setTheAccount(account);
										account.addAWSService(service);
									}
								}
							} else if ((sectionItem.getName().contains("filter"))) {
								ifo = new IntrfaceFilterObject();
								ifo.setFilterItemName(sectionItem.getName());
								ifo.setFilterItemVelue(sectionItem.getValue());
								interfaceFilter = new InterfaceFilter();
								interfaceFilter.setFilterString(sectionItem.getValue());
							} else if ((sectionItem.getName().contains("flag"))) {
								ifo.setFilterStateItemName(sectionItem.getName());
								ifo.setFilterStareItemValue(Boolean.valueOf(sectionItem.getValue()));
								interfaceFilter.setFilterState(Boolean.valueOf(sectionItem.getValue()));
								interfaceFilter.setIfo(ifo);
								account.addFilter(interfaceFilter);
								break;
							}
						}
					}
				}
			}
			IniFile awsConfigINI = readINI(awsConfigINIFile);
			Iterator<IniSection> awsConfigINISections = awsConfigINI.getSections().iterator();
			while (awsConfigINISections.hasNext()) {
				IniSection awsCoonfigSection = awsConfigINISections.next();
				if (awsCoonfigSection.getName().contains("profile " + theSection.getName())) {
					Iterator<IniItem> awsConfigParams = awsCoonfigSection.getItems().iterator();
					while (awsConfigParams.hasNext()) {
						IniItem configSectionItem = awsConfigParams.next();
						if (configSectionItem.getName().contains("region")) {
							account.setAccountRegion(configSectionItem.getValue());
						}
					}
				}
			}
			awsAccounts.add(account);
		}
		return awsAccounts;
	}

	// Retrieve AWS accounts info from INI configuration file
	public static ArrayList<ArrayList<Object>> getAWSAccountsFromINIConfig(File iniFile, File configINIFile) {
		ArrayList<ArrayList<Object>> awsAccounts = new ArrayList<ArrayList<Object>>();
		IniFile ini = readINI(iniFile);
		Iterator<IniSection> sections = ini.getSections().iterator();
		while (sections.hasNext()) {
			ArrayList<Object> accountData = new ArrayList<Object>();
			IniSection theSection = sections.next();
			AWSAccount account = new AWSAccount();
			account.setAccountAlias(theSection.getName());
			accountData.add(account);
			Iterator<IniItem> params = theSection.getItems().iterator();
			while (params.hasNext()) {
				IniItem item = params.next();
				if (item.getName().contains("aws_access_key_id")) {
					accountData.add(item.getValue());
				} else if (item.getName().contains("aws_secret_access_key")) {
					accountData.add(item.getValue());
				}
			}
			IniFile iniConfig = readINI(configINIFile);
			Iterator<IniSection> iniConfigSections = iniConfig.getSections().iterator();
			while (iniConfigSections.hasNext()) {
				IniSection theConfigSection = iniConfigSections.next();
				if (theConfigSection.getName().contains(theSection.getName())) {
					Iterator<IniItem> theConfigSectionParams = theConfigSection.getItems().iterator();
					while (theConfigSectionParams.hasNext()) {
						IniItem theItem = theConfigSectionParams.next();
						if (theItem.getName().contains("region")) {
							accountData.add(theItem.getValue());
						}
					}
				}
			}
			awsAccounts.add(accountData);
		}
		return awsAccounts;
	}

	// Retrieve Applications Tree info from INI configuration file
	public static ArrayList<ArrayList<Object>> getAppTreesConfigInfo(File iniFile) {
		ArrayList<ArrayList<Object>> appsInfo = new ArrayList<ArrayList<Object>>();
		IniFile ini = readINI(iniFile);
		Iterator<IniSection> sections = ini.getSections().iterator();
		while (sections.hasNext()) {
			IniSection theSection = sections.next();
			if (theSection.getName().equals("Applications")) {
				Iterator<IniItem> params = theSection.getItems().iterator();
				while (params.hasNext()) {
					ArrayList<Object> appInfo = new ArrayList<Object>();
					IniItem item = params.next();
					appInfo.add(item.getName());
					appInfo.add(Boolean.valueOf(item.getValue()));
					appsInfo.add(appInfo);
				}
			}
		}
		return appsInfo;
	}

	// Remove section from INI file
	public static void removeAccountFromINIFile(File iniFile, String sectionName) {
		IniFile ini = readINI(iniFile);
		ini.removeSection(sectionName);
		writeINI(iniFile, ini);
	}

	// Update INI file items in section
	public static void updateINIFileItems(File iniFile, String section, String newItemValue, String itemName) {
		IniFile ini = readINI(iniFile);
		IniSection iniSection = ini.getSection(section);
		iniSection.getItem(itemName).setValue(newItemValue);
		writeINI(iniFile, ini);
	}

	public static void removeINIFileItems(File iniFile, String section, String[] itemName) {
		IniFile ini = readINI(iniFile);
		IniSection iniSection = ini.getSection(section);
		int x = 0;
		while (x < itemName.length) {
			iniSection.removeItem(itemName[x]);
			x++;
		}
		writeINI(iniFile, ini);
	}

	// Update filters in INI configuration file
	public static void updateFiltersInINIFile(File iciConfigfile, String section, ArrayList<JTextField> filterTextFields, ArrayList<JCheckBox> filterCheckBox) {
		Iterator<JTextField> textFieldsIterator = filterTextFields.iterator();
		Iterator<JCheckBox> checkBoxesIterator = filterCheckBox.iterator();
		IniFile iniConfig = readINI(iciConfigfile);
		IniSection iniSection = iniConfig.getSection(section);
		Iterator<IniItem> items = iniSection.getItems().iterator();
		while (items.hasNext()) {
			IniItem iniItem = items.next();
			if (iniItem.getName().contains("filter")) {
				iniSection.getItem(iniItem.getName()).setValue(textFieldsIterator.next().getText());
			} else if (iniItem.getName().contains("flag")) {
				iniSection.getItem(iniItem.getName()).setValue(checkBoxesIterator.next().isSelected());
			}
		}
		writeINI(iciConfigfile, iniConfig);
	}

	// Add boolean INI item to section
	public static void addINIFileItemToSection(File iniFile, String section, String itemName, Object itemValue) {
		IniFile ini = readINI(iniFile);
		IniSection iniSection = ini.getSection(section);
		iniSection.addItem(itemName);
		iniSection.getItem(itemName).setValue(itemValue);
		writeINI(iniFile, ini);
	}

	// Update boolean INI item to section
	public static void editINIFileItemInSection(File iniFile, String section, String itemName, Object itemValue) {
		IniFile ini = readINI(iniFile);
		IniSection iniSection = ini.getSection(section);
		iniSection.getItem(itemName).setValue(itemValue);
		writeINI(iniFile, ini);
	}

	// Check if item is present in INI file section
	public static boolean hasItemInSection(File iniFile, String section, String itemName) {
		boolean itemExist = false;
		IniFile ini = readINI(iniFile);
		IniSection iniSection = ini.getSection(section);
		if (iniSection.hasItem(itemName)) {
			itemExist = true;
		}
		return itemExist;
	}

	/**
	 * Read INI file. <br>
	 * @param file INI File object
	 * @return <code>IniFile</code> object
	 */
	public static IniFile readINI(File file) {
		IniFile ini = new BasicIniFile();
		IniFileReader reader = new IniFileReader(ini, file);
		try {
			reader.read();
		} catch (FormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ini;
	}

	// Write to INI
	private static void writeINI(File iniFile, IniFile ini) {
		IniFileWriter writer = new IniFileWriter(ini, iniFile);
		try {
			writer.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get item value from INI. <br>
	 *
	 * @param iniFile INI File object
	 * @param iniSectionName INI file section name
	 * @param itemName INI file item name
	 * @return value of the parameter in INI file
	 */
	public static String getItemValueFromINI(File iniFile, String iniSectionName, String itemName) {
		IniFile ini = readINI(iniFile);
		IniSection iniSection = ini.getSection(iniSectionName);
		return iniSection.getItem(itemName).getValue();
	}
}
