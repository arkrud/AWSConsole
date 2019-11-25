package com.arkrud.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.CustomObjects.CustomAWSObject;
import com.tomtessier.scrollabledesktop.JScrollableDesktopPane;

public class LinkLikeButton extends JButton implements ActionListener, MouseListener  {
	private static final long serialVersionUID = 1L;
	private JScrollableDesktopPane jScrollableDesktopPan;
	private AWSAccount account;
	private CustomAWSObject customAWSObject;
	private Object buttonObject;

	@SuppressWarnings("unchecked")
	public LinkLikeButton(Object buttonObject) {
		this.buttonObject = buttonObject;
		if (buttonObject instanceof String) {
			setText((String) buttonObject);
		} else if (buttonObject instanceof CustomAWSObject) {
			setText(((CustomAWSObject) buttonObject).getObjectAWSID());
		}
		setBackground(null);
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		setMargin(new Insets(0, 0, 0, 0));
		setHorizontalAlignment(SwingConstants.LEFT);
		Font buttonTextFont = new Font("Serif", Font.BOLD, 12);
		@SuppressWarnings("rawtypes")
		Map attributes = buttonTextFont.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		setFont(buttonTextFont.deriveFont(attributes));
		setForeground(Color.BLUE);
		//addActionListener(this);
		addMouseListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		LinkLikeButton source = (LinkLikeButton) e.getSource();
		if (source.getName().equals("TableLinkLikeButton")) {
			if (source.getText().equals("Tags") || source.getText().equals("Ingress") || source.getText().equals("Egress") || source.getText().equals("Rules") || source.getText().equals("All Actions") || source.getText().equals("All Conditions")
					|| source.getText().equals("Redirect") || source.getText().startsWith("i-") || source.getText().startsWith("ELBs") || source.getText().contains("targetgroup")) {
				// Do nothing
			} else {
				source.getCustomAWSObject().showDetailesFrame(getAccount(), source.getCustomAWSObject(), jScrollableDesktopPan);
			}
		} else {
			source.getCustomAWSObject().showDetailesFrame(getAccount(), source.getCustomAWSObject(), jScrollableDesktopPan);
		}
	}

	public JScrollableDesktopPane getjScrollableDesktopPan() {
		return jScrollableDesktopPan;
	}

	public void setjScrollableDesktopPan(JScrollableDesktopPane jScrollableDesktopPan) {
		this.jScrollableDesktopPan = jScrollableDesktopPan;
	}

	public AWSAccount getAccount() {
		return account;
	}

	public void setAccount(AWSAccount account) {
		this.account = account;
	}

	public CustomAWSObject getCustomAWSObject() {
		return customAWSObject;
	}

	public void setCustomAWSObject(CustomAWSObject customAWSObject) {
		this.customAWSObject = customAWSObject;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		LinkLikeButton source = (LinkLikeButton) e.getSource();
		if(e.getClickCount()==1){
		if (source.getName().equals("TableLinkLikeButton")) {
			if (source.getText().equals("Tags") || source.getText().equals("Ingress") || source.getText().equals("Egress") || source.getText().equals("Rules") || source.getText().equals("All Actions") || source.getText().equals("All Conditions")
					|| source.getText().equals("Redirect")  || source.getText().startsWith("ELBs") || source.getText().contains("targetgroup")) {
				// Do nothing
			} else {
				source.getCustomAWSObject().showDetailesFrame(getAccount(), source.getCustomAWSObject(), jScrollableDesktopPan);
			}
		} else {
				source.getCustomAWSObject().showDetailesFrame(getAccount(), source.getCustomAWSObject(), jScrollableDesktopPan);
		}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
