package jm.edu.utech.helpdesk.rest.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jm.edu.utech.helpdesk.rest.entity.User;

//database work for users

@Repository
public class UserRepository {

	//insert a new user or update an existing one
	public User save(User user) {
		EntityManager em = JpaUtil.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			User saved = user.getId() == null
					? persistNew(em, user) : em.merge(user);   // copy detached changes into a managed entity
			tx.commit();
			return saved;
		} catch (RuntimeException ex) {
			if (tx.isActive()) tx.rollback();
			throw ex;
		} finally {
			em.close();
		}
	}

	private User persistNew(EntityManager em, User user) {
		em.persist(user);     
		return user;         
	}

	//find one user by its id
	public Optional<User> findById(Long id) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			return Optional.ofNullable(em.find(User.class, id));
		} finally {
			em.close();
		}
	}

	//find one user by username (used for login), empty if there is none
	public Optional<User> findByUsername(String username) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			List<User> found = em.createQuery(
					"select u from User u where lower(u.username) = lower(:username)", User.class)
					.setParameter("username", username.trim())
					.getResultList();
			return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
		} finally {
			em.close();
		}
	}

	//every row, JPQL uses the entity name User, not the table name
	public List<User> findAll() {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			return em.createQuery(
					"select u from User u order by u.username", User.class)
					.getResultList();
		} finally {
			em.close();
		}
	}

	//users whose username, name or email contains the text
	
	public List<User> search(String text) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			String pattern = "%" + text.trim().toLowerCase() + "%";
			return em.createQuery(
					"select u from User u " +
					"where lower(u.username) like :pattern " +
					"or lower(u.name) like :pattern " +
					"or lower(u.email) like :pattern " +
					"order by u.username", User.class)
					.setParameter("pattern", pattern)
					.getResultList();
		} finally {
			em.close();
		}
	}

	//delete a user by its id
	public void delete(Long id) {
		EntityManager em = JpaUtil.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			User managed = em.find(User.class, id);
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
