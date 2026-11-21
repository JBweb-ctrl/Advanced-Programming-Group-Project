package jm.edu.utech.helpdesk.rest.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;

//checks the MySQL connection when the REST service starts

@Component
public class DatabaseCheck {

    private static final Logger logger = LogManager.getLogger(DatabaseCheck.class);

    //spring calls this once the REST service has started
    @PostConstruct
    public void checkConnection() {
        EntityManager em = null;
        try {
            em = JpaUtil.createEntityManager();
            em.createNativeQuery("SELECT 1").getSingleResult();
            logger.info("Database connection successful (helpdesk_db)");
        } catch (Throwable e) {
            //Throwable because JpaUtil fails while it is loading if MySQL is off (an Error, not an Exception)
            logger.error("Database connection failed, start MySQL then restart the REST service", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    //spring calls this when the REST service stops
    @PreDestroy
    public void close() {
        JpaUtil.close();
    }
}
