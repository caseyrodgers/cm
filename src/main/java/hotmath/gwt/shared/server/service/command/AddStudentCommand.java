package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.AddStudentAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;

public class AddStudentCommand implements ActionHandler<AddStudentAction, StudentModel> {

    @Override
    public StudentModel execute(Connection conn, AddStudentAction action) throws Exception {
        CmStudentDao dao = new CmStudentDao();
        return dao.addStudent(conn, action.getStudent());
    }

    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return AddStudentAction.class;
    }
}
