package com.arkrud.UI;

import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.amazonaws.services.elasticloadbalancingv2.model.Action;
import com.amazonaws.services.elasticloadbalancingv2.model.RedirectActionConfig;
import com.arkrud.Shareware.SpringUtilities;
import com.arkrud.UI.Dashboard.CustomTableViewInternalFrame;
import com.arkrud.Util.UtilMethodsFactory;

public class PropertyPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private CustomTableViewInternalFrame theTableViewInternalFrame;

	public PropertyPanel(Object awsObject, String dataFlag, JLabel[] propertyNameLabels) {
		formatData(awsObject, dataFlag, propertyNameLabels);
	}

	private void formatData(Object awsObject, String dataFlag, JLabel[] propertyNameLabels) {
		List<Object> properties = getProperties(awsObject, dataFlag);
		int x = 0;
		while (x < propertyNameLabels.length) {
			propertyNameLabels[x].setFont(new Font("Serif", Font.BOLD, 12));
			add(propertyNameLabels[x]);
			Object property = properties.get(x);
			if (property instanceof List<?>) {
				if (((List<Object>) property).isEmpty()) {
					LabelLikeTextPane labelLikeTextPane = new LabelLikeTextPane(" - ", false);
					add(labelLikeTextPane);
				} else {
					if (UtilMethodsFactory.getListObjectType((List<Object>) property).contains("String")) {
					} else {
					}
				}
			} else if (property instanceof Integer) {
				PropertyLabel labelLikeTextPane = new PropertyLabel(Integer.toString((Integer) property));
				add(labelLikeTextPane);
			} else if (property instanceof Date) {
				SimpleDateFormat sdfr = new SimpleDateFormat("dd/MMM/yyyy");
				PropertyLabel labelLikeTextPane = new PropertyLabel(sdfr.format(property));
				add(labelLikeTextPane);
			} else if (property instanceof Boolean) {
				PropertyLabel labelLikeTextPane = new PropertyLabel(String.valueOf(property));
				add(labelLikeTextPane);
			} else {
				PropertyLabel label = new PropertyLabel((String) property);
				add(label);
			}
			x++;
			SpringUtilities.makeCompactGrid(this, properties.size(), 2, 10, 10, 10, 10);
		}
	}

	private List<Object> getProperties(Object awsObject, String dataFlag) {
		List<Object> properties = new ArrayList<Object>();
		switch (dataFlag) {
		case "RedirectActionConfig":
			RedirectActionConfig redirectActionConfig = ((Action) awsObject).getRedirectConfig();
			properties.add(redirectActionConfig.getHost());
			properties.add(redirectActionConfig.getPath());
			properties.add(redirectActionConfig.getPort());
			properties.add(redirectActionConfig.getProtocol());
			properties.add(redirectActionConfig.getQuery());
			properties.add(redirectActionConfig.getStatusCode());
			break;
		default:
			break;
		}
		return properties;
	}

	public void setContainigFrame(CustomTableViewInternalFrame tableViewInternalFrame) {
		theTableViewInternalFrame = tableViewInternalFrame;
	}

	public CustomTableViewInternalFrame getContainigFrame() {
		return theTableViewInternalFrame;
	}
}
