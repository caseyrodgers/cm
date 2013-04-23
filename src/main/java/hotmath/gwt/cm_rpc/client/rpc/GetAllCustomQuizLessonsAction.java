package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;

/** Get all lessons available for custom quizzes
 * 
 * @author casey
 *
 */
public class GetAllCustomQuizLessonsAction implements Action<CmList<CustomLessonModel>>{
    public GetAllCustomQuizLessonsAction() {}
}
