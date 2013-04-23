package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataDetailAction;

import java.sql.Connection;
import java.util.List;

public class GetAdminTrendingDataDetailCommand implements ActionHandler<GetAdminTrendingDataDetailAction, CmList<StudentModelI>>{

    List<StudentModelI> studentPool;
    @Override
    public CmList<StudentModelI> execute(Connection conn, GetAdminTrendingDataDetailAction action) throws Exception {
        
        studentPool = new GetStudentGridPageCommand().getStudentPool(action.getDataAction());
        if(studentPool.size() == 0)
            throw new CmRpcException("No students found");
        
        if(action.getDataType() == GetAdminTrendingDataDetailAction.DataType.PROGRAM_USERS) {
            return CmAdminDao.getInstance().getStudentsWhoHaveBeenAssignedProgramSegment(conn, studentPool,action.getTestDefId(), action.getQuizSegment(),false);
        }
        else if(action.getDataType() == GetAdminTrendingDataDetailAction.DataType.LESSON_USERS) {
            return CmAdminDao.getInstance().getStudentsWhoHaveBeenAssignedLesson(conn, studentPool,action.getLessonName(),false);
        }
        else {
            throw new CmRpcException("Unknown DataType request: " + action.getDataType());
        }
    }

    public List<StudentModelI> getStudentPool() {
        return studentPool;
    }

    public void setStudentPool(List<StudentModelI> studentPool) {
        this.studentPool = studentPool;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAdminTrendingDataDetailAction.class;
    }
}
