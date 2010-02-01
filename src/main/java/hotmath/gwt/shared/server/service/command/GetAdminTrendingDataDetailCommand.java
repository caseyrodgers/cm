package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataDetailAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;
import java.util.List;

public class GetAdminTrendingDataDetailCommand implements ActionHandler<GetAdminTrendingDataDetailAction, CmList<StudentModelExt>>{

    List<StudentModelExt> studentPool;
    @Override
    public CmList<StudentModelExt> execute(Connection conn, GetAdminTrendingDataDetailAction action) throws Exception {
        
        studentPool = new GetStudentGridPageCommand().getStudentPool(conn, action.getDataAction());
        if(studentPool.size() == 0)
            throw new CmRpcException("No students found");
        
        if(action.getDataType() == GetAdminTrendingDataDetailAction.DataType.PROGRAM_USERS) {
            return new CmAdminDao().getStudentsWhoHaveBeenAssignedProgramSegment(conn, studentPool,action.getTestDefId(), action.getQuizSegment());
        }
        else if(action.getDataType() == GetAdminTrendingDataDetailAction.DataType.LESSON_USERS) {
            return new CmAdminDao().getStudentsWhoHaveBeenAssignedLesson(conn, studentPool,action.getLessonName());
        }
        else {
            throw new CmRpcException("Unknown DataType request: " + action.getDataType());
        }
    }

    public List<StudentModelExt> getStudentPool() {
        return studentPool;
    }

    public void setStudentPool(List<StudentModelExt> studentPool) {
        this.studentPool = studentPool;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAdminTrendingDataDetailAction.class;
    }
}
