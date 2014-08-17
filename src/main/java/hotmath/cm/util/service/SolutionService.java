package hotmath.cm.util.service;

import hotmath.ProblemID;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.shared.server.service.command.SolutionHTMLCreatorImplFileSystem;
import hotmath.solution.StaticWriter;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import sb.util.SbFile;

/**
 * Return soultion HTML and related JS exposed as servlet
 * 
 * html and JS are returned seperated by ---tutor_data---.  For example
 * 
 * <html>
 * </html>
 * 
 * ---tutor_data---
 * 
 * THE RELATED JS
 * 
 * @see SolutionHTMLCreatorImplFileSystem
 * 
 * @author bob
 * 
 */
public class SolutionService extends HttpServlet {

	private static final long serialVersionUID = 5021091911533642662L;

	private static final Logger logger = Logger.getLogger(SolutionService.class);

    public static final String HTML_JS_DELIMITER = "--tutor_data--";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    this.doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	    try {
	        
	        String pidStr = req.getParameter("pid");
	        if(pidStr == null) {
	            throw new Exception("Must provide 'pid'");
	        }
	        String base = CatchupMathProperties.getInstance().getSolutionBase();
            
            ProblemID pid = new ProblemID(pidStr);
            String path = pid.getSolutionPath_DirOnly("solutions");
    
            String solutionHtml = new SbFile(new File(base, path + "/" + StaticWriter.STEPS_HTML_FILE)).getFileContents().toString("\n");
            String solutionData = new SbFile(new File(base, path + "/" +  "/tutor_data.js")).getFileContents().toString("\n");
            
            resp.getWriter().write(solutionHtml);
            resp.getWriter().write(HTML_JS_DELIMITER);
            resp.getWriter().write(solutionData);
            
            
            resp.setContentType("text/plain");
	    }
	    catch(Exception e) {
	        e.printStackTrace();
	    }
	}
}