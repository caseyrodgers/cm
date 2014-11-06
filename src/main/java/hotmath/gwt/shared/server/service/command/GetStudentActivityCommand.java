package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.StudentActivityDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.shared.client.rpc.action.GetStudentActivityAction;

import java.sql.Connection;
import java.util.List;

public class GetStudentActivityCommand implements ActionHandler<GetStudentActivityAction, CmList<StudentActivityModel>>, ActionHandlerManualConnectionManagement{

    @Override
    public CmList<StudentActivityModel>  execute(Connection conn, GetStudentActivityAction action) throws Exception {
       StudentActivityDao dao = StudentActivityDao.getInstance();
       
       List<StudentActivityModel> activity = dao.getStudentActivity(action.getStudent().getUid(), action.getFromDate(),action.getToDate());

       CmList<StudentActivityModel> list = new CmArrayList<StudentActivityModel>();
       list.addAll(activity);
       
       return list;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetStudentActivityAction.class;
    }

}
