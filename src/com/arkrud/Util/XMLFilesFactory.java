package com.arkrud.Util;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

public class XMLFilesFactory {
	public XMLFilesFactory() {
	}

	public static void writeToXMLConfig(XMLConfiguration config) {
		StringWriter s = new StringWriter();
		try {
			config.save(s);
			File file = new File(UtilMethodsFactory.getConfigPath() + "config.xml");
			config.save(file);
		} catch (ConfigurationException e1) {
			e1.printStackTrace();
		}
	}

	public static XMLConfiguration getXMLConfiguration() {
		String confPath = UtilMethodsFactory.getConfigPath() + "config.xml";
		XMLConfiguration config = null;
		try {
			config = new XMLConfiguration(confPath);
		} catch (ConfigurationException e1) {
			e1.printStackTrace();
		}
		return config;
	}

	public static List<HierarchicalConfiguration> getConfiguration(String path) {
		XMLConfiguration config = getXMLConfiguration();
		List<HierarchicalConfiguration> hc = config.configurationsAt(path);
		return hc;
	}

	public static ArrayList<ArrayList<String>> getSectionsConfigData(String rootNode, String sectionNode, String[] paramList) {
		ArrayList<ArrayList<String>> stacksConfigData = new ArrayList<ArrayList<String>>();
		XMLConfiguration config = getXMLConfiguration();
		List<HierarchicalConfiguration> services = config.configurationsAt(rootNode + "." + sectionNode);
		if (services.size() > 0) {
			for (HierarchicalConfiguration service : services) {
				ArrayList<String> stackConfigData = new ArrayList<String>();
				for (String stackParameter : paramList) {
					stackConfigData.add(service.getString(stackParameter));
				}
				stacksConfigData.add(stackConfigData);
			}
		} else {
			ArrayList<String> stackConfigData = new ArrayList<String>();
			for (String stackParameter : paramList) {
				stackConfigData.add("");
			}
			stacksConfigData.add(stackConfigData);
		}
		return stacksConfigData;
	}

	public static ArrayList<ArrayList<String>> getSectionsConfigDataWithFilter(String rootNode, String sectionNode, String[] paramList, String filterField, String filterFieldValue) {
		ArrayList<ArrayList<String>> stacksConfigData = new ArrayList<ArrayList<String>>();
		XMLConfiguration config = getXMLConfiguration();
		List<HierarchicalConfiguration> services = config.configurationsAt(rootNode + "." + sectionNode);
		if (services.size() > 0) {
			for (HierarchicalConfiguration service : services) {
				if (service.getString(filterField).contains(filterFieldValue)) {
					ArrayList<String> stackConfigData = new ArrayList<String>();
					for (String stackParameter : paramList) {
						stackConfigData.add(service.getString(stackParameter));
					}
					stacksConfigData.add(stackConfigData);
				}
			}
		} else {
			ArrayList<String> stackConfigData = new ArrayList<String>();
			for (String stackParameter : paramList) {
				stackConfigData.add("");
			}
			stacksConfigData.add(stackConfigData);
		}
		return stacksConfigData;
	}

	public static boolean isSectionExist(String root, String section) {
		XMLConfiguration config = XMLFilesFactory.getXMLConfiguration();
		boolean isFirst = false;
		List<HierarchicalConfiguration> services = config.configurationsAt(root + "." + section);
		if (services.size() < 1) {
			isFirst = true;
		}
		return isFirst;
	}

	public static boolean isFirstStackUnderAccount(String root, String section, String stackAccount) {
		XMLConfiguration config = XMLFilesFactory.getXMLConfiguration();
		boolean isFirstAccountStack = true;
		List<HierarchicalConfiguration> services = config.configurationsAt(root + "." + section);
		for (HierarchicalConfiguration service : services) {
			if (service.getString("awsprofile").contains(stackAccount)) {
				isFirstAccountStack = false;
				break;
			}
		}
		return isFirstAccountStack;
	}

	public static String getParamValue(String root, String section, String mutchParameter, String mutchParameterValue, String requestedParameter) {
		String buildVersion = "";
		XMLConfiguration config = XMLFilesFactory.getXMLConfiguration();
		List<HierarchicalConfiguration> services = config.configurationsAt(root + "." + section);
		for (HierarchicalConfiguration service : services) {
			if (service.getString(mutchParameter).contains(mutchParameterValue)) {
				buildVersion = service.getString(requestedParameter);
			}
		}
		return buildVersion;
	}
}
