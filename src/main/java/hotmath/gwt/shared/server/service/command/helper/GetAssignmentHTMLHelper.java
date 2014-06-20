package hotmath.gwt.shared.server.service.command.helper;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.gwt.solution_editor.server.solution.TutorSolution;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;

import java.sql.Connection;
import java.util.List;

/**
 * <code>GetAssignmentHTMLHelper</code> supports the assembly of Assignment HTML
 *  
 * @author bob
 *
 */

public class GetAssignmentHTMLHelper {

	private static final Logger LOGGER = Logger.getLogger(GetAssignmentHTMLHelper.class);

	private static final String PROB_STMT_DIV_OPEN_FMT = "<div id='prob-%d' class='prob-stmt' style='margin-bottom:%dem;'>";
	private static final String DIV_CLOSE = "</div>\n";
	private static final int DEFAULT_WORK_LINES = 15;

	private AssignmentDao assignmentDao;
	private CmSolutionManagerDao solutionDao;
	private Parser htmlParser = new Parser();
	private Node root;
	private String baseDirectory = "";

	public GetAssignmentHTMLHelper() throws Exception {
		assignmentDao = AssignmentDao.getInstance();
		solutionDao = new CmSolutionManagerDao();
	}

	public String getBaseDirectory() {
		return baseDirectory;
	}

	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public String getAssignmentHTML(int assignKey, int numWorkLines, final Connection conn) throws Exception {

		List<ProblemDto> probs = assignmentDao.getProblemsForAssignment(assignKey);
		LOGGER.info(String.format("getAssignmentHTML(): assignKey: %d, num probs: %d", assignKey, probs.size()));

		StringBuilder htmlSb = new StringBuilder();
		int idx = 1;
		for(ProblemDto prob : probs) {

			StringBuilder sb = new StringBuilder();
			String divOpen = String.format(PROB_STMT_DIV_OPEN_FMT, idx++, (numWorkLines > 0) ? numWorkLines : DEFAULT_WORK_LINES);
			sb.append(divOpen);

			TutorSolution solution = solutionDao.getTutorSolution(conn, prob.getPid());
			String html = solution.getProblem().getStatement();
			sb.append(html).append(DIV_CLOSE);

			htmlSb.append(cleanupHtml(sb.toString()));
			htmlSb.append("\n");
		}
		
		return htmlSb.toString();
	}

	private String cleanupHtml(String html) throws Exception {

		htmlParser.setInputHTML(html);
		htmlParser.visitAllNodesWith(new NodeVisitor() {
			@Override
			public void visitTag(Tag tag) {
				String tn = tag.getTagName().toLowerCase();
				if (tn.equals("div")) {
					if (tag.getAttribute("class") != null) {
						if (tag.getAttribute("class").equals("prob-stmt")) {
							root = tag; // mark it for extraction
						}
					}
				}
				else if (tn.equals("p")) {
					String attr = tag.getAttribute("class");
					if (attr != null && attr.toLowerCase().matches("msnormal|msonormal") == true) {
						tag.removeAttribute("class");
					}
				}
				else if (tn.equals("img")) {
					if (tag.getAttribute("src").startsWith("/") == false) {
						// update all relative images
						String path = baseDirectory + "/" + tag.getAttribute("src");
						tag.setAttribute("src", path);
					}
				}
			}
		});

		html = root.toHtml();

		return html;
	}

	
}
