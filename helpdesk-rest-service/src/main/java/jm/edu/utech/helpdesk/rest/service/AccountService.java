package jm.edu.utech.helpdesk.rest.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jm.edu.utech.helpdesk.rest.dto.AccountResponse;
import jm.edu.utech.helpdesk.rest.dto.LoginRequest;
import jm.edu.utech.helpdesk.rest.dto.PasswordChangeRequest;
import jm.edu.utech.helpdesk.rest.exception.DuplicateUserException;
import jm.edu.utech.helpdesk.rest.exception.InvalidCredentialsException;
import jm.edu.utech.helpdesk.rest.exception.UserNotFoundException;
import jm.edu.utech.helpdesk.rest.model.Technician;
import jm.edu.utech.helpdesk.rest.model.User;

//everything about accounts that covers both users and technicians
//this is the only class that looks at passwords
@Service
public class AccountService {

    private static final Logger logger = LogManager.getLogger(AccountService.class);

    //accounts the admin creates get this password, the person changes it after logging in
    public static final String DEFAULT_PASSWORD = "password123";

    private final UserService userService;
    private final TechnicianService technicianService;

    @Autowired
    public AccountService(UserService userService, TechnicianService technicianService) {
        this.userService = userService;
        this.technicianService = technicianService;
    }

    //checks the username and password, gives back the username and role so the client opens the right dashboard
    public AccountResponse login(LoginRequest request) {
        if (request.username() == null || request.password() == null) {
            throw new InvalidCredentialsException("Username or password is incorrect");
        }
        User user = userService.findByUsername(request.username());
        if (user != null && passwordMatches(user.password(), request.password())) {
            logger.info("Login successful: {} ({})", user.username(), user.role());
            return new AccountResponse(user.username(), user.role());
        }
        Technician technician = technicianService.findByUsername(request.username());
        if (technician != null && passwordMatches(technician.password(), request.password())) {
            logger.info("Login successful: {} (Technician)", technician.username());
            return new AccountResponse(technician.username(), "Technician");
        }
        throw new InvalidCredentialsException("Username or password is incorrect");
    }

    //register screen (role is empty, so Student/Staff) or the admin's Manage Users screen (password is empty, so the default)
    public synchronized AccountResponse createUser(User request) {
        String username = checkUsername(request.username());
        String password = request.password() == null ? DEFAULT_PASSWORD : request.password();
        checkPassword(password);
        String role = request.role() == null ? "Student/Staff" : request.role();
        if (!role.equals("Student/Staff") && !role.equals("Administrator")) {
            throw new IllegalArgumentException("Role must be Student/Staff or Administrator");
        }
        User created = userService.addUser(new User(username, preparePassword(password), role));
        return new AccountResponse(created.username(), created.role());
    }

    //admins manage technicians screen, a new technician gets the default password
    public synchronized AccountResponse createTechnician(Technician request) {
        String username = checkUsername(request.username());
        Technician created = technicianService.addTechnician(new Technician(username, preparePassword(DEFAULT_PASSWORD)));
        return new AccountResponse(created.username(), "Technician");
    }

    //change password screen, works for users and technicians, the current password must be right
    public synchronized void changePassword(String username, PasswordChangeRequest request) {
        if (request.currentPassword() == null || request.newPassword() == null) {
            throw new IllegalArgumentException("Current and new password are required");
        }
        checkPassword(request.newPassword());

        User user = userService.findByUsername(username);
        if (user != null) {
            if (!passwordMatches(user.password(), request.currentPassword())) {
                throw new InvalidCredentialsException("Current password is incorrect");
            }
            userService.updatePassword(user.username(), preparePassword(request.newPassword()));
            logger.info("Password changed: {}", user.username());
            return;
        }
        Technician technician = technicianService.findByUsername(username);
        if (technician != null) {
            if (!passwordMatches(technician.password(), request.currentPassword())) {
                throw new InvalidCredentialsException("Current password is incorrect");
            }
            technicianService.updatePassword(technician.username(), preparePassword(request.newPassword()));
            logger.info("Password changed: {}", technician.username());
            return;
        }
        throw new UserNotFoundException("User not found: " + username);
    }

    //username cant be empty or already used by a user or a technician
    private String checkUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        String trimmed = username.trim();
        if (userService.findByUsername(trimmed) != null || technicianService.findByUsername(trimmed) != null) {
            logger.warn("Duplicate user account attempt: {}", trimmed);
            throw new DuplicateUserException("That username already exists: " + trimmed);
        }
        return trimmed;
    }

    //same rule as the clients register and change password screens
    private void checkPassword(String password) {
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    //how a password is stored, plain text for now (to hash later, only this and passwordMatches change)
    private String preparePassword(String password) {
        return password;
    }

    //compares the stored password with the one typed in
    private boolean passwordMatches(String stored, String typed) {
        return stored.equals(preparePassword(typed));
    }
}
