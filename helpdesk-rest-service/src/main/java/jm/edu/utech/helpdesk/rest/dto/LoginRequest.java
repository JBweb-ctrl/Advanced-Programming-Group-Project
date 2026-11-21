package jm.edu.utech.helpdesk.rest.dto;

//body of POST /api/users/login
public record LoginRequest(String username, String password) {
}
