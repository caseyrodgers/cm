package hotmath.gwt.shared.server.service.command.helper;

import hotmath.HotMathUtilities;
import hotmath.ProblemID;
import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.shared.server.service.command.SolutionHTMLCreatorImplFileSystem;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.gwt.solution_editor.server.solution.TutorSolution;
import hotmath.solution.SolutionParts;
import hotmath.solution.writer.SolutionHTMLCreator;
import hotmath.solution.writer.TutorProperties;
import hotmath.testset.ha.SolutionDao;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <code>GetAssignmentHTMLHelper</code> supports the assembly of Assignment HTML
 *  
 * @author bob
 *
 */

public class GetAssignmentHTMLHelper {

	private static final Logger LOGGER = Logger.getLogger(GetAssignmentHTMLHelper.class);

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

	private static final String PROB_STMT_DIV_OPEN_FMT =
			"<div id='prob-%d' class='prob-stmt' style='margin-bottom:%dem;' context='%s'>";
	private static final String DIV_CLOSE = "</div>\n";
	private static final int DEFAULT_WORK_LINES = 15;

	private AssignmentDao assignmentDao;
	private SolutionDao  solutionDao;
	//private CmSolutionManagerDao solnMgrDao;
	private Parser htmlParser = new Parser();
	private Node root;
	private String baseDirectory = "";

    public static SolutionHTMLCreator __creator;
    static TutorProperties __tutorProps = new TutorProperties();
    static {
        try {
            __creator = new SolutionHTMLCreatorImplFileSystem(__tutorProps.getTemplate(), __tutorProps.getTutor());
        } catch (Exception hme) {
            LOGGER.error("Error creating solution creator: ", hme);
        }
    }


	public GetAssignmentHTMLHelper() throws Exception {
		assignmentDao = AssignmentDao.getInstance();
		//solnMgrDao = new CmSolutionManagerDao();
		solutionDao = SolutionDao.getInstance();
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

			try {
				StringBuilder sb = new StringBuilder();
	
	            ProblemID ppid = new ProblemID(prob.getPid());
	
	            SolutionParts sp = __creator.getSolutionHTML(null,null, prob.getPid());
	            String solutionHtml= sp.getMainHtml();
	            String solutionData = sp.getData();
	
	            String path = ppid.getSolutionPath_DirOnly("solutions");
	            solutionHtml = HotMathUtilities.makeAbsolutePaths(path, solutionHtml);
	
	            SolutionContext solutionContext = solutionDao.getGlobalSolutionContext(prob.getPid());
	            String pidFull = (solutionContext != null) ? solutionContext.getPid(): "";
	            String pidParts[] = pidFull.split("\\$");
	            String contextGuid = (pidParts.length > 1) ? pidParts[1] : "";
				String divOpen = String.format(PROB_STMT_DIV_OPEN_FMT, idx, (numWorkLines > 0) ? numWorkLines : DEFAULT_WORK_LINES, contextGuid);
				sb.append(divOpen);
				
                 				
                // add the tutorData need to initialize this tutor
                sb.append("<div class='tutorData'><![CDATA[" + solutionData + "]]></div>");
				
				sb.append(solutionHtml).append(DIV_CLOSE);
				htmlSb.append(sb);   // add entire tutor source cleanupHtml(sb.toString(), idx)
				htmlSb.append("\n");
				idx++;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return htmlSb.toString();
	}

	public String getHeader(int assignKey) throws Exception {

		Assignment assignment = assignmentDao.getAssignment(assignKey);

		StringBuilder sb = new StringBuilder();
		sb.append("<div class='assignment-header'>\n <h1 class='assignment-name'>").append(assignment.getComments()).append("</h1>\n");
		sb.append("<h3 class='assignment-due-date'>Due date: ");
		Date dueDate = assignment.getDueDate();
		if (dueDate != null) {
			sb.append(DATE_FMT.format(dueDate));
		}
		sb.append("</h3>\n</div>\n");
		return sb.toString();
	}

	boolean firstPtag;
	private String cleanupHtml(String html, final int probNum) throws Exception {

		htmlParser.setInputHTML(html);
		firstPtag = true;
		htmlParser.visitAllNodesWith(new NodeVisitor() {
			@Override
			public void visitTag(Tag tag) {
				String tn = tag.getTagName().toLowerCase();
				if (tn.equals("div")) {
					if (tag.getAttribute("class") != null) {
						if (tag.getAttribute("class").equals("prob-stmt")) {
							root = tag; // mark it for extraction
							return;
						}
					}
					if (tag.getAttribute("id") != null) {
						String attr = tag.getAttribute("id");
						if (attr.equalsIgnoreCase("problem_statement")) {
							tag.removeAttribute("id");
						}
						else if (attr.toLowerCase().matches("hm_flash_widget|hm_flash_widget_def") == true) {
							tag.removeAttribute("id");
							tag.setAttribute("class", "'" + attr + "'");
						}
					}
				}
				else if (tn.equals("p")) {
					String attr = tag.getAttribute("class");
					if (attr != null && attr.toLowerCase().matches("msnormal|msonormal") == true) {
						tag.removeAttribute("class");
					}
					if (firstPtag == true && tag.getFirstChild() != null) {
						String text = tag.getFirstChild().getText();
						if (text != null) {
							text = text.replaceAll("\\n|\\t", " ");
							if (text.trim().length() > 0) {
								text = String.format("%d. %s", probNum, text);
								NodeList nodeList = new NodeList(new TextNode(text));
								NodeList origList = tag.getChildren();
								for (int i=1; i<origList.size(); i++) {
									nodeList.add(origList.elementAt(i));
								}
								tag.setChildren(nodeList);
								firstPtag = false;
							}
						}
					}
				}
				else if (tn.equals("img")) {
					if (tag.getAttribute("src").startsWith("/") == false) {
						// update all relative images
						String path = baseDirectory + "/" + tag.getAttribute("src");
						tag.setAttribute("src", "'" + path + "'");
					}
				}
				else if (tn.equals("h1") == true) {
					tag.setAttribute("style", "'display:none'");
				}
			}
		});

		html = root.toHtml();

		return html;
	}

	
}
