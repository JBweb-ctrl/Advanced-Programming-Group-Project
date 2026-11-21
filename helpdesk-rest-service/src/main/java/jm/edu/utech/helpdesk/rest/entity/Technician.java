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

//a technician who gets assigned tickets, mapped to the technician table
@Entity                         // Hibernate manages this class
@Table(name = "technician")     // map the class to the technician table
public class Technician {

	@Id                                                  // primary-key field
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "technician_id")                      // MySQL AUTO_INCREMENT column
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, length = 100)
	private String password;

	@Column(length = 50)
	private String name;

	@Column(length = 50)
	private String position;

	@Column(name = "phone_number", length = 20)
	private String phoneNumber;

	//one technician has many tickets (the ticket table holds the technician_id)
	@OneToMany(mappedBy = "technician")
	private List<Ticket> tickets = new ArrayList<>();

	// JPA requires an accessible no-argument constructor
	protected Technician() {}

	public Technician(String username, String password) {
		this.username = username;
		this.password = password;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
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
