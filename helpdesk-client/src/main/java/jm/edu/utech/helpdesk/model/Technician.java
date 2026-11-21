package jm.edu.utech.helpdesk.model;

public class Technician {
	private String username;
	
	public Technician() {
		
	}

	public Technician(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
