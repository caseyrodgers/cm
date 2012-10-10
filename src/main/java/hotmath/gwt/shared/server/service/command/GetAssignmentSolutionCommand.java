package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

import org.apache.log4j.Logger;


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
        
        SolutionContext context = new SolutionContext();
        context.setContextJson(AssignmentDao.getInstance().getSolutionContext(action.getUid(),action.getAssignKey(),action.getPid()));
        info.setContext(context);
        
        String lastUserWidgetValue = AssignmentDao.getInstance().getAssignmentLastWidgetInputValue(action.getUid(), action.getAssignKey(),action.getPid());
        
        AssignmentProblem assProb = new AssignmentProblem(action.getUid(),action.getAssignKey(),info,determineProblemType(info.getHtml()),lastUserWidgetValue);
    
        AssignmentDao.getInstance().makeSurePidStatusExists(action.getAssignKey(),action.getUid(),action.getPid());
        
        return assProb;
    }

    /**
     * Given the a problem PID, determine the type of problem
     * 
     * Meaning what type of input is required
     * 
     * @param defaultLabel
     * @return
     */
    public ProblemType determineProblemType(String html)  {
        try {
            if (html.indexOf("input_widget") > -1) {
                return ProblemType.INPUT_WIDGET;
            } else if (false && html.indexOf("hm_question_def") > -1) {
                return ProblemType.MULTI_CHOICE;
            }
        }
        catch(Exception e) {
            __logger.error("Error determining problem type: " + html, e);
        }

        return ProblemType.WHITEBOARD; 
    }

}
