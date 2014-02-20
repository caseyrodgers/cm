package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.rpc.GetAdminTeachersAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;

public class GetAdminTeachersCommand implements ActionHandler<GetAdminTeachersAction, CmList<TeacherIdentity>>{
    
    @Override
    public CmList<TeacherIdentity> execute(Connection conn, GetAdminTeachersAction action) throws Exception {
        return new CmArrayList<>(CustomProblemDao.getInstance().getAdminTeachers(action.getAdminId()));
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAdminTeachersAction.class;
    }


}
