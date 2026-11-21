package jm.edu.utech.helpdesk.rest.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jm.edu.utech.helpdesk.rest.entity.Ticket;

//database work for tickets
@Repository
public class TicketRepository {

	//insert a new ticket or update an existing one
	public Ticket save(Ticket ticket) {
		EntityManager em = JpaUtil.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Ticket saved = ticket.getId() == null
					? persistNew(em, ticket) : em.merge(ticket);   // copy detached changes into a managed entity
			tx.commit();
			return saved;
		} catch (RuntimeException ex) {
			if (tx.isActive()) tx.rollback();
			throw ex;
		} finally {
			em.close();
		}
	}

	private Ticket persistNew(EntityManager em, Ticket ticket) {
		em.persist(ticket);     
		return ticket;         
	}

	//find one ticket by its id
	public Optional<Ticket> findById(Long id) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			return Optional.ofNullable(em.find(Ticket.class, id));
		} finally {
			em.close();
		}
	}

	
	public List<Ticket> findAll() {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			return em.createQuery(
					"select t from Ticket t order by t.id", Ticket.class)
					.getResultList();
		} finally {
			em.close();
		}
	}

	//tickets whose subject, description or id contains the text
	
	public List<Ticket> search(String text) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			String pattern = "%" + text.trim().toLowerCase() + "%";
			return em.createQuery(
					"select t from Ticket t " +
					"where lower(t.subject) like :pattern " +
					"or lower(t.description) like :pattern " +
					"or cast(t.id as string) like :pattern " +
					"order by t.id", Ticket.class)
					.setParameter("pattern", pattern)
					.getResultList();
		} finally {
			em.close();
		}
	}

	//tickets one user submitted
	public List<Ticket> findByUserId(Long userId) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			return em.createQuery(
					"select t from Ticket t where t.user.id = :userId order by t.id", Ticket.class)
					.setParameter("userId", userId)
					.getResultList();
		} finally {
			em.close();
		}
	}

	//tickets assigned to one technician
	public List<Ticket> findByTechnicianId(Long technicianId) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			return em.createQuery(
					"select t from Ticket t where t.technician.id = :technicianId order by t.id", Ticket.class)
					.setParameter("technicianId", technicianId)
					.getResultList();
		} finally {
			em.close();
		}
	}

	//delete a ticket by its id
	public void delete(Long id) {
		EntityManager em = JpaUtil.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Ticket managed = em.find(Ticket.class, id);
			if (managed != null) em.remove(managed);
			tx.commit();
		} catch (RuntimeException ex) {
			if (tx.isActive()) tx.rollback();
			throw ex;
		} finally {
			em.close();
		}
	}
}
