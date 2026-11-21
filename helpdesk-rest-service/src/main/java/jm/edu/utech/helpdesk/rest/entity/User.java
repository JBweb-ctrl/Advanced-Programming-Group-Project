package jm.edu.utech.helpdesk.rest.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

//a student/staff user or the administrator, mapped to the user table
@Entity                         // Hibernate manages this class
@Table(name = "user")           // map the class to the user table
public class User {

	@Id                                                  // primary-key field
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")                            // MySQL AUTO_INCREMENT column
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, length = 100)
	private String password;

	@Column(nullable = false, length = 20)
	private String role;                                 // "Student/Staff" or "Administrator"

	@Column(length = 50)
	private String name;

	@Column(length = 50)
	private String email;

	@Column(name = "phone_number", length = 20)
	private String phoneNumber;

	//one user has many tickets (the ticket table holds the user_id)
	@OneToMany(mappedBy = "user")
	private List<Ticket> tickets = new ArrayList<>();

	// JPA requires an accessible no-argument constructor
	protected User() {}

	public User(String username, String password, String role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<Ticket> getTickets() {
		return tickets;
	}
}
