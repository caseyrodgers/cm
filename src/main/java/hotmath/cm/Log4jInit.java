package hotmath.cm;

import hotmath.cm.util.CmWebResourceManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4jInit extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -771897979715468574L;

    public void init() {
        String prefix = getServletContext().getRealPath("/");
        String file = getInitParameter("log4j-init-file");
        // if the log4j-init-file is not set, then no point in trying
        if (file != null) {
            PropertyConfigurator.configure(prefix + file);
            Logger.getLogger(this.getClass()) .info("Catchup Log4J intialized");
        }
        
        
        CmWebResourceManager.setFileBase(getServletContext().getRealPath("temp"));
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
    }
}
