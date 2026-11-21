package jm.edu.utech.helpdesk.model;

import java.util.ArrayList;
import java.util.List;

//details of a support ticket
public class Ticket {

	//ticket attributes
	private Long id;
    private String subject;
    private String description;
    private String creationDate;
    private String priority;
    private String status;
    private String assignedTechnician;
    private String resolutionNotes;
    private List<Comment> comments = new ArrayList<>();
    private String submittedBy;
    private String resolvedDate;

 
	//empty ticket
    public Ticket() {}

    //ticket constructor
    public Ticket(Long id, String subject, String priority, String status, String creationDate) {
        this.id = id;
        this.subject = subject;
        this.priority = priority;
        this.status = status;
        this.creationDate = creationDate;
    }

    //getters and setters for ticket
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAssignedTechnician() {
		return assignedTechnician;
	}

	public void setAssignedTechnician(String assignedTechnician) {
		this.assignedTechnician = assignedTechnician;
	}

	public String getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}

	public String getResolutionNotes() {
		return resolutionNotes;
	}

	public void setResolutionNotes(String resolutionNotes) {
		this.resolutionNotes = resolutionNotes;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public String getResolvedDate() {
		return resolvedDate;
	}

	public void setResolvedDate(String resolvedDate) {
		this.resolvedDate = resolvedDate;
	}

	
}
