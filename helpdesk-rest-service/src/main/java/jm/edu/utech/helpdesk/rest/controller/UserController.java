package jm.edu.utech.helpdesk.rest.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jm.edu.utech.helpdesk.rest.dto.AccountResponse;
import jm.edu.utech.helpdesk.rest.dto.LoginRequest;
import jm.edu.utech.helpdesk.rest.dto.PasswordChangeRequest;
import jm.edu.utech.helpdesk.rest.model.User;
import jm.edu.utech.helpdesk.rest.service.AccountService;
import jm.edu.utech.helpdesk.rest.service.UserService;

//REST endpoints for users: list, create, remove, login and change password
//errors 401, 404, 409 etc come from GlobalExceptionHandler
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    //GET /api/users
    @GetMapping
    public List<AccountResponse> getAllUsers() {
        List<AccountResponse> accounts = new ArrayList<>();
        for (User user : userService.getAllUsers()) {
            accounts.add(new AccountResponse(user.username(), user.role()));
        }
        return accounts;
    }

    //POST /api/users  201 Created
    @PostMapping
    public ResponseEntity<AccountResponse> createUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createUser(user));
    }

    //POST /api/users/login  username and role or 401
    @PostMapping("/login")
    public AccountResponse login(@RequestBody LoginRequest request) {
        return accountService.login(request);
    }

    //PUT /api/users/student1/password  204 No Content
    @PutMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(@PathVariable String username, @RequestBody PasswordChangeRequest request) {
        accountService.changePassword(username, request);
        return ResponseEntity.noContent().build();
    }

    //DELETE /api/users/student1 204 No Content
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> removeUser(@PathVariable String username) {
        userService.removeUser(username);
        return ResponseEntity.noContent().build();
    }
}
