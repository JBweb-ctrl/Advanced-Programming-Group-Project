package jm.edu.utech.helpdesk.rest.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jm.edu.utech.helpdesk.rest.entity.Comment;

//database work for comments

@Repository
public class CommentRepository {

	//insert a new comment or update an existing one
	public Comment save(Comment comment) {
		EntityManager em = JpaUtil.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Comment saved = comment.getId() == null
					? persistNew(em, comment) : em.merge(comment);   // copy detached changes into a managed entity
			tx.commit();
			return saved;
		} catch (RuntimeException ex) {
			if (tx.isActive()) tx.rollback();
			throw ex;
		} finally {
			em.close();
		}
	}

	private Comment persistNew(EntityManager em, Comment comment) {
		em.persist(comment);     // schedule an INSERT
		return comment;          // id is filled in after persistence
	}

	//find one comment by its id
	public Optional<Comment> findById(Long id) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			return Optional.ofNullable(em.find(Comment.class, id));
		} finally {
			em.close();
		}
	}

	//every row, JPQL uses the entity name Comment, not the table name
	public List<Comment> findAll() {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			return em.createQuery(
					"select c from Comment c order by c.id", Comment.class)
					.getResultList();
		} finally {
			em.close();
		}
	}

	//comments whose text, author or ticket id contains the text
	public List<Comment> search(String text) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			String pattern = "%" + text.trim().toLowerCase() + "%";
			return em.createQuery(
					"select c from Comment c " +
					"where lower(c.text) like :pattern " +
					"or lower(c.author) like :pattern " +
					"or cast(c.ticket.id as string) like :pattern " +
					"order by c.id", Comment.class)
					.setParameter("pattern", pattern)
					.getResultList();
		} finally {
			em.close();
		}
	}

	//all the comments on one ticket, oldest first
	public List<Comment> findByTicketId(Long ticketId) {
		EntityManager em = JpaUtil.createEntityManager();
		try {
			return em.createQuery(
					"select c from Comment c where c.ticket.id = :ticketId order by c.id", Comment.class)
					.setParameter("ticketId", ticketId)
					.getResultList();
		} finally {
			em.close();
		}
	}

	//delete a comment by its id
	public void delete(Long id) {
		EntityManager em = JpaUtil.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Comment managed = em.find(Comment.class, id);
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
