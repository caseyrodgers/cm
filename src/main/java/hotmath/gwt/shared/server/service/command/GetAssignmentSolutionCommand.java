package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentUserInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.SolutionDao;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/** Get the solution requested and mark this solution
 *  as viewed.
 *  
 * @author casey
 *
 */
public class GetAssignmentSolutionCommand implements ActionHandler<GetAssignmentSolutionAction, AssignmentProblem> {
    
    static Logger __logger = Logger.getLogger(GetAssignmentSolutionCommand.class);

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentSolutionAction.class;
    }

    @Override
    public AssignmentProblem execute(Connection conn, GetAssignmentSolutionAction action) throws Exception {
        SolutionInfo info = new GetSolutionCommand().execute(conn,  new GetSolutionAction(action.getUid(),  0, action.getPid()));
        
        info.setHtml(convertQuestionDefToInput(info.getHtml()));
        
        String lastUserWidgetValue = AssignmentDao.getInstance().getAssignmentLastWidgetInputValue(action.getUid(), action.getAssignKey(),action.getPid());
        
        StudentProblemDto stuProblem = AssignmentDao.getInstance().getStudentProblem(action.getUid(), action.getAssignKey(),action.getPid());
        
        String problemStatus = AssignmentDao.getInstance().getProblemStatusForStudent(action.getUid(), action.getAssignKey(), action.getPid());
        
        StudentAssignmentUserInfo assInfo = AssignmentDao.getInstance().getStudentAssignmentUserInfo(action.getUid(),action.getAssignKey());
        
        AssignmentProblem assProb = new AssignmentProblem(action.getUid(),action.getAssignKey(),assInfo.isGraded(),!assInfo.isEditable(),info, stuProblem, lastUserWidgetValue, problemStatus, assInfo.getDueDate(), assInfo.isPastDueSubmitable(), assInfo.isPreventLessonAccess());

        AssignmentDao.getInstance().makeSurePidStatusExists(action.getAssignKey(),action.getUid(),action.getPid());
        
        
        assProb.getLessons().addAll(SolutionDao.getInstance().getLessonsAssociatedForPid(conn, action.getPid()));
        
        return assProb;
    }

    private String convertQuestionDefToInput(String html) {
        
        String wTag = "wrap_" + System.currentTimeMillis();
        html = "<div id='" + wTag + "'>" + html + "</div>";
        try {
            Document doc = Jsoup.parse(html);
            Elements qElements = doc.getElementsByClass("hm_question_def");
            if(qElements.size() > 0) {
                Element qElement = qElements.get(0);
                String qHtmlRaw = qElement.html();
                String qHtmlCooked = convertQuestionHtml(qHtmlRaw);
                qElement.html(qHtmlCooked);
                
                html = doc.getElementById(wTag).html();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return html;
    }
    
    /**
     * 
     *
     * decorate HTML with input elements
     * to allow tracking of selected values
     * 
     * 
     * tutor_questionGuessChanged defined in tutor_tablet.js
     * 
     * 
     * @param htmlRaw
     * @return
     */
    static int __uniquer;
    private String convertQuestionHtml(String htmlRaw) {
        
        htmlRaw = "<div id='q_wrapper'>" + htmlRaw + "</div>";
        String tag = "answer_" + __uniquer;
        Document doc = Jsoup.parseBodyFragment(htmlRaw);
        int optionNumber=0;
        for(Element li: doc.getElementsByTag("li")) {

            String isCorrect = li.attr("correct");
            
            // remove answer from student source view
            li.removeAttr("correct");
            
            // might be correct/incorrect or yes/no
            String trueOrFalse = (isCorrect.equals("correct") || isCorrect.equals("yes"))?"true":"false";
            
            Elements elems = li.getElementsByTag("div");
            

            Element answerEle = elems.get(0);
            
            __uniquer++;

            String id= "answer_id_" + __uniquer;

            /** append the input element */
            String inputElement = "<span class='question-input' style='margin-right: 10px'>"
                + "<input option_number='" + (++optionNumber) + "' value='"
                + trueOrFalse
                + "' type='radio' name='"
                + tag
                + "' id='"
                + id
                + "' onclick='tutor_questionGuessChanged(this)'/>"
                + "</span>";

            answerEle.html(inputElement + answerEle.html());
        }
        
        String cooked = doc.getElementById("q_wrapper").html();
        return cooked;
    }
    


}
