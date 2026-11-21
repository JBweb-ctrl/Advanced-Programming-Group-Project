package jm.edu.utech.helpdesk.rest.dto;
//what the client is told about an account: the username and role never the password
public record AccountResponse(String username, String role) {

}
