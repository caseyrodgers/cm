package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWorkForUserAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;

public class GetAssignmentWorkForUserCommand implements ActionHandler<GetAssignmentWorkForUserAction, CmList<StudentAssignment>>{

    @Override
    public CmList<StudentAssignment> execute(Connection conn, GetAssignmentWorkForUserAction action) throws Exception {
        CmList<StudentAssignment> cmList = new CmArrayList<StudentAssignment>();
        cmList.addAll(AssignmentDao.getInstance().getAssignmentWorkForStudent(action.getUid(), action.getFromDate(), action.getToDate()));

        // sort by descending date
        Collections.sort(cmList, new Comparator<StudentAssignment>() {

			@Override
			public int compare(StudentAssignment sa1, StudentAssignment sa2) {
				int test = sa1.getDueDate().after(sa2.getDueDate()) ? 0 : 1;
				if (test != 0) return test;

				return (sa1.getComments().compareTo(sa2.getComments()));
			}
        	
        });
        return cmList;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentWorkForUserAction.class;
    }
}
