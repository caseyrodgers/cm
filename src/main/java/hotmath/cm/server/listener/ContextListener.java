package hotmath.cm.server.listener;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * Provide access to context events; for example, when the context is initialized
 *
 * @author bob
 *
 */

public final class ContextListener implements ServletContextAttributeListener, ServletContextListener {

	private static Logger logger = Logger.getLogger(ContextListener.class);

    private ServletContext context = null;

    private static Date startDate;
    
    

    /**
     * Record the fact that a servlet context attribute was added.
     *
     * @param event The servlet context attribute event
     */
    public void attributeAdded(ServletContextAttributeEvent event) {

        if (logger.isDebugEnabled())
        	logger.info("attributeAdded('" + event.getName() + "', '" +
                event.getValue() + "')");

    }


    /**
     * Record the fact that a servlet context attribute was removed.
     *
     * @param event The servlet context attribute event
     */
    public void attributeRemoved(ServletContextAttributeEvent event) {

        if (logger.isDebugEnabled())
        	logger.info("attributeRemoved('" + event.getName() + "', '" +
                event.getValue() + "')");

    }


    /**
     * Record the fact that a servlet context attribute was replaced.
     * 
     * @param event The servlet context attribute event
     */
    public void attributeReplaced(ServletContextAttributeEvent event) {

    	if (logger.isDebugEnabled())
            logger.debug("attributeReplaced('" + event.getName() + "', '" +
                event.getValue() + "')");

    }


    /**
     * Record the fact that this web application has been destroyed.
     *
     * @param event The servlet context event
     */
    public void contextDestroyed(ServletContextEvent event) {

        logger.info("+++ contextDestroyed()");
        this.context = null;

    }


    /**
     * Record the fact that this web application has been initialized.
     *
     * @param event The servlet context event
     */
    public void contextInitialized(ServletContextEvent event) {
	
	    startDate = new Date();

        this.context = event.getServletContext();
        logger.info("+++ contextInitialized()");

    }

    public static Date getStartDate() {
    	return startDate;
    }


}