package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.UpdateStudentAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;

public class UpdateStudentCommand implements ActionHandler<UpdateStudentAction, StudentModel> {

    @Override
    public StudentModel execute(Connection conn, UpdateStudentAction action) throws Exception {
        CmStudentDao dao = new CmStudentDao();
        return (StudentModel)dao.updateStudent(conn, action.getStudent(), action.getStuChanged(), action.getProgChanged(), action.getProgIsNew(), action.getPasscodeChanged());
    }

    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return UpdateStudentAction.class;
    }
}
