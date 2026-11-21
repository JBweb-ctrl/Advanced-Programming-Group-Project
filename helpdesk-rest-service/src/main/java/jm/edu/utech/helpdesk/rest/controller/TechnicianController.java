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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jm.edu.utech.helpdesk.rest.dto.AccountResponse;
import jm.edu.utech.helpdesk.rest.model.Technician;
import jm.edu.utech.helpdesk.rest.service.AccountService;
import jm.edu.utech.helpdesk.rest.service.TechnicianService;

//REST endpoints for technicians (GET, POST, DELETE) like the CRUD endpoints 
//the passwords never go back to the client
@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    private final TechnicianService service;
    private final AccountService accountService;

    @Autowired
    public TechnicianController(TechnicianService service, AccountService accountService) {
        this.service = service;
        this.accountService = accountService;
    }

    //GET /api/technicians
    @GetMapping
    public List<AccountResponse> getAllTechnicians() {
        List<AccountResponse> accounts = new ArrayList<>();
        for (Technician technician : service.getAllTechnicians()) {
            accounts.add(new AccountResponse(technician.username(), "Technician"));
        }
        return accounts;
    }

    //POST /api/technicians 201 Created, gets the default password
    @PostMapping
    public ResponseEntity<AccountResponse> addTechnician(@RequestBody Technician technician) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createTechnician(technician));
    }

    //DELETE /api/technicians/tech3 204 No Content
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> removeTechnician(@PathVariable String username) {
        service.removeTechnician(username);
        return ResponseEntity.noContent().build();
    }
}
