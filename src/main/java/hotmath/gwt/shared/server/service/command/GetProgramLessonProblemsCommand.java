package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonProblemsAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/** Provide high level access to program listing data
 * 
 * @author casey
 *
 */
public class GetProgramLessonProblemsCommand implements ActionHandler<GetProgramLessonProblemsAction, CmList<ProblemDto>>{

    @Override
    public CmList<ProblemDto> execute(final Connection conn, GetProgramLessonProblemsAction action) throws Exception {
        
        List<ProblemDto> problems=null;
        if(action.getFile() == null) {
            // is custom?
            List<CustomProblemModel> custProbs = CustomProblemDao.getInstance().getCustomProblemsFor(new TeacherIdentity(action.getAdminId(),null, 0));
            problems = new ArrayList<ProblemDto>();
            for(CustomProblemModel cm: custProbs) {
                problems.add(new ProblemDto(cm.getProblemNumber(),0, new LessonModel("",""), cm.getTeacher().getTeacherName() + " " + cm.getProblemNumber(),cm.getPid(),0));
            }
        }
        else {
            problems = AssignmentDao.getInstance().getLessonProblemsFor(conn,action.getLesson(),action.getFile(), action.getSubject());
        }
        
        
        CmList<ProblemDto> data = new CmArrayList<ProblemDto>();
        data.addAll(problems);
        return data;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetProgramLessonProblemsAction.class;
    }
}
