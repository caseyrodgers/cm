package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CustomProgramInfoModel;
import hotmath.gwt.shared.client.rpc.action.CustomProgramInfoAction;

import java.sql.Connection;

/** Provide status information about custom program 
 *  including counts of assigned, use,history and list 
 *  of students who are currently assiged.
 *  
 * @author casey
 *
 */
public class CustomProgramInfoCommand implements ActionHandler<CustomProgramInfoAction, CustomProgramInfoModel> {

    @Override
    public CustomProgramInfoModel execute(Connection conn, CustomProgramInfoAction action) throws Exception {
        return CmCustomProgramDao.getInstance().getCustomProgramInfo(conn,action.getAdminId(), action.getProgram());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return CustomProgramInfoAction.class;
    }
}
