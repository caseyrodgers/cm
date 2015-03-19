package hotmath.cm.server.rest;

import hotmath.gwt.cm_rpc.client.rpc.GetCm2QuizHtmlAction;

/** centeral place to request a CM2 request
 *  with any specialized formatting required.
 *  
 * @author casey
 *
 */
public class Cm2ActionManager {

    public static String getUserProgram(int userId) throws Exception {
        // new         // 678549
        // GetCmProgramFlowAction flowAction = new GetCmProgramFlowAction(userId, FlowType.ACTIVE);
        // GetUserSyncAction syncAction = new GetUserSyncAction(678549);
        
        GetCm2QuizHtmlAction action = new GetCm2QuizHtmlAction(2594726);
        // GetQuizHtmlAction action = new GetQuizHtmlAction(2593696);
        String jsonResponse = new ActionDispacherWrapper().execute(action);
        return jsonResponse;
    }

}
