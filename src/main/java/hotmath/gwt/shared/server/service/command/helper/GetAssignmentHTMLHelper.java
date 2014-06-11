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

	private static final String PROB_STMT_DIV_OPEN_FMT = "<div class='prob-stmt' style='margin-bottom:%dem;'>";
	private static final String DIV_CLOSE = "</div>";
	private static final int DEFAULT_WORK_LINES = 15;

	private AssignmentDao assignmentDao;
	private CmSolutionManagerDao solutionDao;

	public GetAssignmentHTMLHelper() throws Exception {
		assignmentDao = AssignmentDao.getInstance();
		solutionDao = new CmSolutionManagerDao();
		
	}

	public String getAssignmentHTML(int assignKey, int numWorkLines, final Connection conn) throws Exception {

		List<ProblemDto> probs = assignmentDao.getProblemsForAssignment(assignKey);

		String divOpen = String.format(PROB_STMT_DIV_OPEN_FMT, (numWorkLines > 0) ? numWorkLines : DEFAULT_WORK_LINES);

		StringBuilder htmlSb = new StringBuilder();
			for(ProblemDto prob : probs) {
				htmlSb.append(divOpen);

				TutorSolution solution = solutionDao.getTutorSolution(conn, prob.getPid());

				String html = solution.getProblem().getStatement();

				htmlSb.append(html).append(DIV_CLOSE);

			}
		
		return htmlSb.toString();
	}

	
}
