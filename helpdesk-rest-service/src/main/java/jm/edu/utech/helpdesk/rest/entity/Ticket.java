package jm.edu.utech.helpdesk.rest.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

//a support ticket, mapped to the ticket table
@Entity                         // Hibernate manages this class
@Table(name = "ticket")         // map the class to the ticket table
public class Ticket {

	@Id                                                 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ticket_id")                  
	private Long id;

	@Column(nullable = false, length = 150)
	private String subject;

	@Column(nullable = false, length = 1000)
	private String description;

	@Column(name = "creation_date", nullable = false)
	private LocalDateTime creationDate;

	@Column(name = "resolved_date")
	private LocalDateTime resolvedDate;                  //empty until the ticket is resolved or closed

	@Column(nullable = false, length = 10)
	private String priority;                             //LOW, MEDIUM or HIGH

	@Column(nullable = false, length = 15)
	private String status;                               //NEW, ASSIGNED, IN_PROGRESS, PENDING, RESOLVED, CLOSED or REOPENED

	@Column(name = "resolution_notes", length = 1000)
	private String resolutionNotes;                      // empty until the technician adds notes

	//many tickets belong to one user (the user_id column)
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	//many tickets can be assigned to one technician
	@ManyToOne
	@JoinColumn(name = "technician_id")
	private Technician technician;

	//one ticket has many comments, saving or deleting a ticket does the same to its comments
	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();

	
	protected Ticket() {}

	//a new ticket starts as NEW with today's date
	public Ticket(String subject, String description, String priority, User user) {
		this.subject = subject;
		this.description = description;
		this.priority = priority;
		this.user = user;
		this.status = "NEW";
		this.creationDate = LocalDateTime.now();
	}

	public Long getId() {
		return id;
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

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public LocalDateTime getResolvedDate() {
		return resolvedDate;
	}

	public void setResolvedDate(LocalDateTime resolvedDate) {
		this.resolvedDate = resolvedDate;
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

	public String getResolutionNotes() {
		return resolutionNotes;
	}

	public void setResolutionNotes(String resolutionNotes) {
		this.resolutionNotes = resolutionNotes;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Technician getTechnician() {
		return technician;
	}

	public void setTechnician(Technician technician) {
		this.technician = technician;
	}

	public List<Comment> getComments() {
		return comments;
	}
}
