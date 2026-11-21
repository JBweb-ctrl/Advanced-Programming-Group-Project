package jm.edu.utech.helpdesk.rest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jm.edu.utech.helpdesk.rest.exception.UserNotFoundException;
import jm.edu.utech.helpdesk.rest.model.Technician;
import jm.edu.utech.helpdesk.rest.persistence.TechnicianRepository;

//the technicians, saved in the technician table through TechnicianRepository
//login and password checks are done by AccountService
//the entity classes share names like User, so write it out to not re edit everything (jm.edu.utech.helpdesk.rest.entity.User)
@Service
public class TechnicianService {

    private static final Logger logger = LogManager.getLogger(TechnicianService.class);

    private final TechnicianRepository repository;

    @Autowired
    public TechnicianService(TechnicianRepository repository) {
        this.repository = repository;
    }

    //every technician
    public synchronized List<Technician> getAllTechnicians() {
        List<Technician> technicians = new ArrayList<>();
        for (jm.edu.utech.helpdesk.rest.entity.Technician entity : repository.findAll()) {
            technicians.add(toModel(entity));
        }
        return technicians;
    }

    //usernames of every technician (used by the workload report)
    public synchronized List<String> getTechnicianNames() {
        List<String> names = new ArrayList<>();
        for (jm.edu.utech.helpdesk.rest.entity.Technician entity : repository.findAll()) {
            names.add(entity.getUsername());
        }
        return names;
    }

    //one technician by username, null if there is none
    public synchronized Technician findByUsername(String username) {
        Optional<jm.edu.utech.helpdesk.rest.entity.Technician> found = repository.findByUsername(username);
        if (found.isPresent()) {
            return toModel(found.get());
        }
        return null;
    }

    //adds a technician (AccountService has already checked the username is free)
    public synchronized Technician addTechnician(Technician technician) {
        repository.save(new jm.edu.utech.helpdesk.rest.entity.Technician(technician.username(), technician.password()));
        logger.info("Technician added: {}", technician.username());
        return technician;
    }

    //changes a technician password and saves it
    public synchronized void updatePassword(String username, String newPassword) {
        jm.edu.utech.helpdesk.rest.entity.Technician entity = findEntity(username);
        entity.setPassword(newPassword);
        repository.save(entity);
    }

    //removes a technician by username (their tickets go back to unassigned, ON DELETE SET NULL)
    public synchronized void removeTechnician(String username) {
        jm.edu.utech.helpdesk.rest.entity.Technician entity = findEntity(username);
        repository.delete(entity.getId());
        logger.info("Technician removed: {}", username);
    }

    //the database row for a username, error if there is none
    private jm.edu.utech.helpdesk.rest.entity.Technician findEntity(String username) {
        Optional<jm.edu.utech.helpdesk.rest.entity.Technician> found = repository.findByUsername(username);
        if (found.isEmpty()) {
            throw new UserNotFoundException("Technician not found: " + username);
        }
        return found.get();
    }

    //turns the database entity into the record the rest of the service uses
    private Technician toModel(jm.edu.utech.helpdesk.rest.entity.Technician entity) {
        return new Technician(entity.getUsername(), entity.getPassword());
    }
}
