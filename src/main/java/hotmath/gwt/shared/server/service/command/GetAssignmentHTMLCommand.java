package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentHTMLAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentHTML;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.server.service.command.helper.GetAssignmentHTMLHelper;

import java.sql.Connection;

public class GetAssignmentHTMLCommand implements ActionHandler<GetAssignmentHTMLAction, AssignmentHTML> {


    @Override
    public AssignmentHTML execute(Connection conn, GetAssignmentHTMLAction action) throws Exception {
    	GetAssignmentHTMLHelper helper = new GetAssignmentHTMLHelper();
    	AssignmentHTML assignmentHTML = new AssignmentHTML();
    	assignmentHTML.setHtml(helper.getAssignmentHTML(action.getAssignKey(), 0, conn));
    	return assignmentHTML;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentHTMLAction.class;
    }

}
