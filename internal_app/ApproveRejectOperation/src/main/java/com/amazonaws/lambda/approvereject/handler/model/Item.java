package com.amazonaws.lambda.approvereject.handler.model;

public class Item {

	private String employeeId;
	private String employeeName;
	private String employeeEmail;
	private String itemUploadedDateTime;
	private String itemType;
	private String itemRejectReason;
	private boolean itemFlag;
	private String approvedRejectBy;

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getItemUploadedDateTime() {
		return itemUploadedDateTime;
	}

	public void setItemUploadedDateTime(String itemUploadedDateTime) {
		this.itemUploadedDateTime = itemUploadedDateTime;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getItemRejectReason() {
		return itemRejectReason;
	}

	public void setItemRejectReason(String itemRejectReason) {
		this.itemRejectReason = itemRejectReason;
	}

	public boolean isItemFlag() {
		return itemFlag;
	}

	public void setItemFlag(boolean itemFlag) {
		this.itemFlag = itemFlag;
	}

	public String getApprovedRejectBy() {
		return approvedRejectBy;
	}

	public void setApprovedRejectBy(String approvedRejectBy) {
		this.approvedRejectBy = approvedRejectBy;
	}

	public String getEmployeeEmail() {
		return employeeEmail;
	}

	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}
	
}
