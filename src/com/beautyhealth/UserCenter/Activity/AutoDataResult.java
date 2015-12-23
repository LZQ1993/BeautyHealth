package com.beautyhealth.UserCenter.Activity;

import java.util.List;

public class AutoDataResult {
	public String resultcode;
	public String currentpage;
	public String total;
	public String rowsofapage;
	public List<AutoReturnTrasactionMessage> result;

	public String getResultcode() {
		return resultcode;
	}

	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getRowsofapage() {
		return rowsofapage;
	}

	public void setRowsofapage(String rowsofapage) {
		this.rowsofapage = rowsofapage;
	}

	public String getCurrentpage() {
		return currentpage;
	}

	public void setCurrentpage(String currentpage) {
		this.currentpage = currentpage;
	}

	public List<AutoReturnTrasactionMessage> getResult() {
		return result;
	}

	public void setResult(List<AutoReturnTrasactionMessage> result) {
		this.result = result;
	}

}
