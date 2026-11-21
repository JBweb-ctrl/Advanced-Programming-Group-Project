package jm.edu.utech.helpdesk.rest.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jm.edu.utech.helpdesk.rest.entity.Technician;

//database work for technicians
@Repository
public class TechnicianRepository {

	//insert a new technician or update an existing one
	public Technician save(Technician technician) {
		EntityManager em = JpaUtil.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Technician saved = technician.getId() == null
					? persistNew(em, technician) : em.merge(technician);   // copy detached changes into a managed entity
			tx.commit();
			return saved;
		} catch (RuntimeException ex) {
			if (tx.isActive()) tx.rollback();
			throw ex;
		} finally {
			em.close();
		}
	}

	private Technician persistNew(EntityManager em, Technician technician) {
		em.persist(technician);     // schedule an INSERT
		return technician;          // id is filled in after persistence
	}

	//find one technician by its id
	public Optional<Technician> findById(Long id) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			return Optional.ofNullable(em.find(Technician.class, id));
		} finally {
			em.close();
		}
	}

	//find one technician by username (used for login), empty if there is none
	public Optional<Technician> findByUsername(String username) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			List<Technician> found = em.createQuery(
					"select t from Technician t where lower(t.username) = lower(:username)", Technician.class)
					.setParameter("username", username.trim())
					.getResultList();
			return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
		} finally {
			em.close();
		}
	}

	//every row, JPQL uses the entity name Technician, not the table name
	public List<Technician> findAll() {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			return em.createQuery(
					"select t from Technician t order by t.username", Technician.class)
					.getResultList();
		} finally {
			em.close();
		}
	}

	//technicians whose username, name or position contains the text
	public List<Technician> search(String text) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			String pattern = "%" + text.trim().toLowerCase() + "%";
			return em.createQuery(
					"select t from Technician t " +
					"where lower(t.username) like :pattern " +
					"or lower(t.name) like :pattern " +
					"or lower(t.position) like :pattern " +
					"order by t.username", Technician.class)
					.setParameter("pattern", pattern)
					.getResultList();
		} finally {
			em.close();
		}
	}

	//delete a technician by its id
	public void delete(Long id) {
		EntityManager em = JpaUtil.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Technician managed = em.find(Technician.class, id);
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
