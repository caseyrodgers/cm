package hotmath.cm.util.service;

import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentHTMLAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentHTML;
import hotmath.gwt.shared.server.service.command.GetAssignmentHTMLCommand;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author bob
 *
 */
public class AssignmentHTMLService extends HttpServlet {
	
	private static final long serialVersionUID = 6706582815611415847L;

	private static final Logger logger = Logger.getLogger(AssignmentHTMLService.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

		try {
            assignmentHTML(request, response, "GET");
        } catch (Exception e) {
            logger.info("Error generating Assignment HTML", e);
            request.getRequestDispatcher("/gwt-resources/assignment_fail.html").forward(request, response);
        }	    
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

        try {
            assignmentHTML(request, response, "POST");
        } catch (Exception e) {
            logger.info("Error generating PDF", e);
            request.getRequestDispatcher("/gwt-resources/assignment_fail.html").forward(request, response);
        }       	    
	}

	/**
	 * Performs the action: generate Assignment HTML from a GET or POST.
	 * 
	 * @param request	the request object
	 * @param response	the response object
	 * @param methodGetPost	the method that was used in the form
	 */
	public void assignmentHTML(HttpServletRequest request, HttpServletResponse response, String methodGetPost) throws Exception {

		String key = null;
		key = request.getParameter("key");
		int assignKey = 0;
		if (key != null && key.trim().length() > 0) {
			try {
				assignKey = Integer.parseInt(key);
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Assignment key is invalid");
			}
		}

		GetAssignmentHTMLAction action = new GetAssignmentHTMLAction();
		action.setAssignKey(assignKey);
		GetAssignmentHTMLCommand command = new GetAssignmentHTMLCommand();
		Connection conn = null;

		try {
			conn = HMConnectionPool.getConnection();
		
    		AssignmentHTML assignmentHTML = command.execute(conn, action);
    		if (assignmentHTML != null && assignmentHTML.getHtml() != null && assignmentHTML.getHtml().length() > 0) {
    	        response.getWriter().write(assignmentHTML.getHtml());
    		}

		    else {
			    throw new Exception("Assigment generation failed");
		    }
		}
		finally {
		    SqlUtilities.releaseResources(null, null, conn);
		}
	}

	public void destroy() {
	}

}