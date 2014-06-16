package hotmath.gwt.shared.server.service.command.helper;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.gwt.solution_editor.server.solution.TutorSolution;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * <code>GetAssignmentHTMLHelper</code> supports the assembly of Assignment HTML
 *  
 * @author bob
 *
 */

public class GetAssignmentHTMLHelper {

	private static final Logger LOGGER = Logger.getLogger(GetAssignmentHTMLHelper.class);

	private static final String PROB_STMT_DIV_OPEN_FMT = "<div id='prob-%d' class='prob-stmt' style='margin-bottom:%dem;'>";
	private static final String DIV_CLOSE = "</div>";
	private static final int DEFAULT_WORK_LINES = 15;

	private static final String ASSIGNMENT_TEMPLATE_1 =
		"<html><head>" +
        "<meta http-equiv='content-type' content='text/html; charset=iso-8859-1'>" +
        "<title>Assignment</title>" +
        "<link rel='stylesheet' type='text/css' href='/gwt-resources/css/CatchupMath.css></head>" +
        "<body><div id='main-content' class='assignment'>";
	private static final String ASSIGNMENT_TEMPLATE_2 = 
        "</div>"+
        "<script type='text/javascript' language='javascript' src='/gwt-resources/js/CatchupMath_combined.js'></script>" +
        "</body></html>";

	private AssignmentDao assignmentDao;
	private CmSolutionManagerDao solutionDao;

	public GetAssignmentHTMLHelper() throws Exception {
		assignmentDao = AssignmentDao.getInstance();
		solutionDao = new CmSolutionManagerDao();
		
	}

	public String getAssignmentHTML(int assignKey, int numWorkLines, final Connection conn) throws Exception {

		List<ProblemDto> probs = assignmentDao.getProblemsForAssignment(assignKey);

		StringBuilder htmlSb = new StringBuilder();
		htmlSb.append(ASSIGNMENT_TEMPLATE_1);
		int idx = 1;
		for(ProblemDto prob : probs) {

			String divOpen = String.format(PROB_STMT_DIV_OPEN_FMT, idx++, (numWorkLines > 0) ? numWorkLines : DEFAULT_WORK_LINES);

			htmlSb.append(divOpen);

			TutorSolution solution = solutionDao.getTutorSolution(conn, prob.getPid());
			String html = solution.getProblem().getStatement();
			html = html.replaceAll("id=", "class=");
			html = html.replaceAll(" class=MsoNormal", "");

			htmlSb.append(html).append(DIV_CLOSE);

		}
		htmlSb.append(ASSIGNMENT_TEMPLATE_2);
		
		return htmlSb.toString();
	}

	
}
