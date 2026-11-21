package jm.edu.utech.helpdesk.rest.model;

//a technician account the password never goes back to the client 
public record Technician(
        String username,
        String password) {
}
