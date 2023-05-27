package com.amazonaws.lambda.internal_app_login.modal;

public class LoginResponse {
	
	
	private String message;
	private Boolean adminFlag;

	public Boolean getAdminFlag() {
		return adminFlag;
	}

	public void setAdminFlag(Boolean adminFlag) {
		this.adminFlag = adminFlag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	

}
