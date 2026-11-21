package jm.edu.utech.helpdesk.rest.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jm.edu.utech.helpdesk.rest.dto.ReportResponse;
import jm.edu.utech.helpdesk.rest.service.ReportService;

//REST endpoints for the 5 report endpoints
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final Logger logger = LogManager.getLogger(ReportController.class);

    private final ReportService service;

    @Autowired
    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/tickets-submitted")
    public ReportResponse ticketsSubmitted() {
        logger.info("Report generated: tickets submitted");
        return new ReportResponse(service.ticketsSubmitted());
    }

    @GetMapping("/tickets-resolved")
    public ReportResponse ticketsResolved() {
        logger.info("Report generated: tickets resolved");
        return new ReportResponse(service.ticketsResolved());
    }

    @GetMapping("/open-tickets")
    public ReportResponse openTickets() {
        logger.info("Report generated: open tickets");
        return new ReportResponse(service.openTickets());
    }

    @GetMapping("/technician-workload")
    public ReportResponse technicianWorkload() {
        logger.info("Report generated: technician workload");
        return new ReportResponse(service.technicianWorkload());
    }

    @GetMapping("/average-resolution-time")
    public ReportResponse averageResolutionTime() {
        logger.info("Report generated: average resolution time");
        return new ReportResponse(service.averageResolutionTime());
    }
}
