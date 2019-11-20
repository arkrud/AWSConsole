package com.arkrud.UI;

import com.arkrud.TreeInterface.TreeNodeState;

public class InterfaceFilter implements TreeNodeState {
	private String filterString = "";
	private boolean filterState = false;
	private String accountAlias;
	private IntrfaceFilterObject ifo;

	public String getFilterString() {
		return filterString;
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString;
	}

	public boolean isFilterState() {
		return filterState;
	}

	public void setFilterState(boolean filterState) {
		this.filterState = filterState;
	}

	@Override
	public String getNodeText() {
		return ifo.getFilterItemVelue();
	}

	@Override
	public boolean isSelected() {
		return ifo.getFilterStareItemValue();
	}

	@Override
	public void setSelected(boolean selected) {
		ifo.setFilterStareItemValue(selected);
	}

	@Override
	public String getAWSAccountAlias() {
		// TODO Auto-generated method stub
		return accountAlias;
	}

	@Override
	public void setAWSAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}

	public IntrfaceFilterObject getIfo() {
		return ifo;
	}

	public void setIfo(IntrfaceFilterObject ifo) {
		this.ifo = ifo;
	}

	@Override
	public String getNodeScreenName() {
		// TODO Auto-generated method stub
		return ifo.getFilterStateItemName();
	}
}
