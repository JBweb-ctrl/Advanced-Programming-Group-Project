package jm.edu.utech.helpdesk.service;

public class ApiResponse {

	private final int status;
	private final String body;

	public ApiResponse(int status, String body) {
		this.status = status;
		this.body = body;
	}

	public int getStatus() {
		return status;
	}

	public String getBody() {
		return body;
	}

	public boolean isOk() {
		return status >= 200 && status < 300;
	}
}