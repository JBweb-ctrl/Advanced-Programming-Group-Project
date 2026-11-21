package jm.edu.utech.helpdesk.rest.dto;

//body of PUT /api/users/{username}/password
public record PasswordChangeRequest(String currentPassword, String newPassword) {

}
