package hotmath.cm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.CmWebResourceManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sb.logger.SbLogger;
import sb.logger.SbLoggerEvent;
import sb.logger.SbLoggerListenerImplVerbose;

/** Provides hooks to initialize the Catchup Math 
 *  system when plugged into a servlet environment.
 *  
 * @author casey
 *
 */
public class CmInitialzation extends HttpServlet {

    private static final long serialVersionUID = -771897979715468574L;

    private static Logger logger = Logger.getLogger(CmInitialzation.class);
    
    private static String PID = "n/a";
    
    private String pidFilename;

    public void init() {
        
        if (getInitParameter("sblogger-init") != null) {
            SbLogger.addLogListener(new SbLoggerListenerImplVerbose(){
                @Override
                public void handleMessageEvent(SbLoggerEvent event) {
                    logger.info(event.getType() + " - " + event.getMessage() + " - " + event.getThrowable());
                }
            });
            logger.info("set up SbLogger log listener");
        }
        
        String prefix = getServletContext().getRealPath("/");
        String file = getInitParameter("log4j-init-file");
        // if the log4j-init-file is not set, then no point in trying
        if (file != null) {
            PropertyConfigurator.configure(prefix + file);
            logger.info("Catchup Math Log4J intialized");
        }
        
        pidFilename = getInitParameter("pid-file");
        if (pidFilename == null) {
        	logger.error("pid-file init param not specified in web.xml");
        	pidFilename = "cm.pid";
        }
        else {
        	logger.info("pid-file: " + pidFilename);
        }

        try {
            CatchupMathProperties.getInstance().setProperty("servlet.context", getServletContext().getRealPath("."));
        }
        catch(Exception e) {
            logger.error("Could not set catchup.home", e);
        }
        
        CmWebResourceManager.setFileBase(getServletContext().getRealPath("cm_temp"));
        try {
            logger.info("Catchup Math initialized: version=" + CatchupMathProperties.getInstance().getClientVersionNumber());
            savePid();
        }
        catch(Exception e) {
            logger.error("Error starting Catchup Math", e);
        }
    }

    /**
     * This method is Unix/Linux specific 
     */
    private void savePid() {
        File pidFile = new File(System.getProperty("user.home"), pidFilename);
        logger.info("Writing PID file: " + pidFile.getAbsolutePath());

        String [] cmd = { "bash", "-c", "echo $PPID" };
        
        byte [] bo = new byte[100];

        Process p;
		try {
			p = Runtime.getRuntime().exec(cmd);
	        p.getInputStream ().read(bo);
	        PID = new String(bo);
	        if (PID != null) PID = PID.trim();

	        logger.info("PID is: " + PID);
	        
	        FileWriter fw = new FileWriter(pidFile);

	        fw.write(PID);
	        fw.flush();
	        fw.close();

		} catch (Exception e) {
			logger.error("Failed to save PID file", e);
		}

    }
    
    public static String getPid() {
    	return PID;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        /** silent */
    }
}
