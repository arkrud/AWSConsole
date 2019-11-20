package com.arkrud.TableInterface;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
class CustomIntegerCellEditor extends DefaultCellEditor {
	private @SuppressWarnings("rawtypes") Class[] argTypes = new Class[] { String.class };
	private @SuppressWarnings("rawtypes") java.lang.reflect.Constructor constructor;
	private Object value;
	private String tableUsageIdentifier;

	public CustomIntegerCellEditor(String tableUsageIdentifier) {
		super(new JTextField());
		this.tableUsageIdentifier = tableUsageIdentifier;
		getComponent().setName("Table.editor");
		((JTextField) getComponent()).setHorizontalAlignment(JTextField.RIGHT);
	}

	public boolean stopCellEditing() {
		String s = (String) super.getCellEditorValue();
		if ("".equals(s)) {
			if (constructor.getDeclaringClass() == String.class) {
				value = s;
			}
			super.stopCellEditing();
		}
		if (tableUsageIdentifier.equals("AddELBListenersSimple") || tableUsageIdentifier.equals("EditELBListenersSimple")) {
			try {
				value = constructor.newInstance(new Object[] { s });
				if (value instanceof Number && ((Number) value).doubleValue() > 1023 && ((Number) value).doubleValue() < 65536 || ((Number) value).doubleValue() == 25 || ((Number) value).doubleValue() == 80 || ((Number) value).doubleValue() == 443
						|| ((Number) value).doubleValue() == 465 || ((Number) value).doubleValue() == 587) {
					return super.stopCellEditing();
				} else {
					throw new RuntimeException("Input must be a positive number.");
				}
			} catch (Exception e) {
				JOptionPane.showConfirmDialog(null, "EC2-Classic load balancer port must be either 25, 80, 443, 465, 587 or 1024~65535 inclusive", "Field Validation Formatting error", JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE);
				((JComponent) getComponent()).setBorder(new LineBorder(Color.red));
				return false;
			}
		} else if (tableUsageIdentifier.equals("AddELBListenersAdvanced") || tableUsageIdentifier.equals("EditELBListenersAdvanced")) {
			try {
				value = constructor.newInstance(new Object[] { s });
				if (value instanceof Number && ((Number) value).doubleValue() > 0 && ((Number) value).doubleValue() < 65536) {
					return super.stopCellEditing();
				} else {
					throw new RuntimeException("Input must be a positive number.");
				}
			} catch (Exception e) {
				JOptionPane.showConfirmDialog(null, "EC2-Classic load balancer port must be either 25, 80, 443, 465, 587 or 1024~65535 inclusive", "Field Validation Formatting error", JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE);
				((JComponent) getComponent()).setBorder(new LineBorder(Color.red));
				return false;
			}
		} else {
			return false;
		}
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.value = null;
		((JComponent) getComponent()).setBorder(new LineBorder(Color.black));
		try {
			Class<?> type = table.getColumnClass(column);
			if (type == Object.class) {
				type = String.class;
			}
			constructor = type.getConstructor(argTypes);
		} catch (Exception e) {
			return null;
		}
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	public Object getCellEditorValue() {
		return value;
	}
}
