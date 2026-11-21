package jm.edu.utech.helpdesk.rest.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//a comment on a ticket, mapped to the comment table
@Entity                         // Hibernate manages this class
@Table(name = "comment")        // map the class to the comment table
public class Comment {

	@Id                                                  // primary-key field
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")                         // MySQL AUTO_INCREMENT column
	private Long id;

	//many comments belong to one ticket (the ticket_id column)
	@ManyToOne(optional = false)
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;

	@Column(nullable = false, length = 50)
	private String author;                               // username of whoever wrote it

	@Column(nullable = false, length = 1000)
	private String text;

	@Column(name = "comment_date", nullable = false)
	private LocalDateTime commentDate;

	// JPA requires an accessible no-argument constructor
	protected Comment() {}

	//a new comment gets the current date and time
	public Comment(Ticket ticket, String author, String text) {
		this.ticket = ticket;
		this.author = author;
		this.text = text;
		this.commentDate = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public String getAuthor() {
		return author;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getCommentDate() {
		return commentDate;
	}
}
