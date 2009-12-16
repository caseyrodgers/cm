package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.UpdateStudentAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;

public class UpdateStudentCommand implements ActionHandler<UpdateStudentAction, StudentModelI> {

    @Override
    public StudentModelI execute(Connection conn, UpdateStudentAction action) throws Exception {
        CmStudentDao dao = new CmStudentDao();
        return dao.updateStudent(conn, action.getStudent(), action.getStuChanged(), action.getProgChanged(), action.getProgIsNew(), action.getPasscodeChanged());
    }

    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return UpdateStudentAction.class;
    }
}
