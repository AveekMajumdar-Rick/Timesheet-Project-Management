package com.amazonaws.lambda.ilc_timesheet_view.model;

import org.joda.time.LocalDate;

public class ItemS3Details {

	private String objectUrl;
	private LocalDate date;
	private int status;
	private byte[] fileContent;
	private String comments;

	public String getObjectUrl() {
		return objectUrl;
	}

	public void setObjectUrl(String objectUrl) {
		this.objectUrl = objectUrl;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
