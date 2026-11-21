package jm.edu.utech.helpdesk.rest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.PersistenceException;
import jm.edu.utech.helpdesk.rest.exception.UserNotFoundException;
import jm.edu.utech.helpdesk.rest.model.User;
import jm.edu.utech.helpdesk.rest.persistence.UserRepository;

//the student/staff and administrator accounts, saved in the user table through UserRepository
//login register and password checks are done by AccountService
//the entity classes share names like User, so write it out to not re edit everything (jm.edu.utech.helpdesk.rest.entity.User)
@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    //every user
    public synchronized List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (jm.edu.utech.helpdesk.rest.entity.User entity : repository.findAll()) {
            users.add(toModel(entity));
        }
        return users;
    }

    //one user by username, null if there is none
    public synchronized User findByUsername(String username) {
        Optional<jm.edu.utech.helpdesk.rest.entity.User> found = repository.findByUsername(username);
        if (found.isPresent()) {
            return toModel(found.get());
        }
        return null;
    }

    //adds a user (AccountService has already checked the username is free)
    public synchronized User addUser(User user) {
        repository.save(new jm.edu.utech.helpdesk.rest.entity.User(user.username(), user.password(), user.role()));
        logger.info("User added: {} ({})", user.username(), user.role());
        return user;
    }

    //changes a user password and saves it
    public synchronized void updatePassword(String username, String newPassword) {
        jm.edu.utech.helpdesk.rest.entity.User entity = findEntity(username);
        entity.setPassword(newPassword);
        repository.save(entity);
    }

    //removes a user by username, a user who still has tickets cant be removed (foreign key)
    public synchronized void removeUser(String username) {
        jm.edu.utech.helpdesk.rest.entity.User entity = findEntity(username);
        try {
            repository.delete(entity.getId());
        } catch (PersistenceException e) {
            logger.warn("Could not remove user {}: {}", username, e.getMessage());
            throw new IllegalArgumentException("Cannot remove " + username + ", they still have tickets");
        }
        logger.info("User removed: {}", username);
    }

    //the database row for a username, error if there is none
    private jm.edu.utech.helpdesk.rest.entity.User findEntity(String username) {
        Optional<jm.edu.utech.helpdesk.rest.entity.User> found = repository.findByUsername(username);
        if (found.isEmpty()) {
            throw new UserNotFoundException("User not found: " + username);
        }
        return found.get();
    }

    //turns the database entity into the record the rest of the service uses
    private User toModel(jm.edu.utech.helpdesk.rest.entity.User entity) {
        return new User(entity.getUsername(), entity.getPassword(), entity.getRole());
    }
}
