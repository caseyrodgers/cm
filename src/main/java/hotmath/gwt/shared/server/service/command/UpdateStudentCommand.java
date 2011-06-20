package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.UpdateStudentAction;

import java.sql.Connection;

public class UpdateStudentCommand implements ActionHandler<UpdateStudentAction, StudentModelI> {

    @Override
    public StudentModelI execute(Connection conn, UpdateStudentAction action) throws Exception {

        CmStudentDao dao = CmStudentDao.getInstance();
        
        StudentModelI student;

        if (action.getSectionNumChanged() == false) {
            student = dao.updateStudent(conn, action.getStudent(), action.getStuChanged(), action.getProgChanged(), action.getProgIsNew(), action.getPasscodeChanged(),
        		action.getPassPercentChanged());
    	}
        else {
        	student = dao.updateStudent(conn, action.getStudent(), action.getStuChanged(), action.getProgChanged(), action.getProgIsNew(), action.getPasscodeChanged(),
            		action.getPassPercentChanged());
        	CmProgramFlow cmpFlow = new CmProgramFlow(conn, action.getStudent().getUid());
        	StudentActiveInfo info = cmpFlow.getActiveInfo();
        	info.setActiveSegment(action.getStudent().getSectionNum());
        	info.setActiveRunId(0);
        	info.setActiveTestId(0);
        	cmpFlow.saveActiveInfo(conn);
        }
        return student;
    }

    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return UpdateStudentAction.class;
    }
}
