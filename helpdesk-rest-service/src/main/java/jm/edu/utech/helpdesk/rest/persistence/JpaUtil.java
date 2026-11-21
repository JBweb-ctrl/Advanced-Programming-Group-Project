package jm.edu.utech.helpdesk.rest.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

//creates EntityManagers 
//make one EntityManagerFactory and reuse it
public final class JpaUtil {

	private static final EntityManagerFactory FACTORY =
			Persistence.createEntityManagerFactory("helpdeskPU");

	public static EntityManager createEntityManager() {
		return FACTORY.createEntityManager();
	}

	public static void close() {
		if (FACTORY.isOpen()) FACTORY.close();
	}

	private JpaUtil() {}
}
