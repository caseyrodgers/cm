package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.gwt.shared.client.rpc.action.GetLessonQuestionsAction;

import java.sql.Connection;

/** Get all questions that are 'linked to' lesson named
 * 
 * @author casey
 *
 */
public class GetLessonQuestionsCommand implements ActionHandler<GetLessonQuestionsAction, CmList<QuizQuestion>>{

    @Override
    public CmList<QuizQuestion> execute(Connection conn, GetLessonQuestionsAction action) throws Exception {
        /** How are questions linked to this lesson?
         * 
         * - inmh_map has file .. and a range .. 
         * - but each range represents an RPP not necessarily a question
         * - can only use solutions that are setup for 'quiz questions'
         * - this means having the <question> format setup manually  (must be in problem statement)
         * - or, it means the hm_question_def format (must be in problem statement)
         * 
         * 
         * - possibly, use the file and look at all solutions linked to it that are in a textcode that is
         *   is being used as a source for a program.
         *   
         */
        return new CmQuizzesDao().getQuestionsFor(conn, action.getLesson(), action.getSubject());
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetLessonQuestionsAction.class;
    }
}
