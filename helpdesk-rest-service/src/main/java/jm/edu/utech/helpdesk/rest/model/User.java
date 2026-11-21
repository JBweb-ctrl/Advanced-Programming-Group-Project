package jm.edu.utech.helpdesk.rest.model;
//a student/staff user or the administrator
//role is "Student/Staff" or "Administrator"
public record User(
        String username,
        String password,
        String role) {
}
