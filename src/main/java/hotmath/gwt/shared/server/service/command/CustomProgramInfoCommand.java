package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.shared.client.model.CustomProgramInfoModel;
import hotmath.gwt.shared.client.rpc.action.CustomProgramInfoAction;
import hotmath.gwt.shared.server.service.ActionHandler;

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
        CustomProgramInfoModel model = new CmCustomProgramDao().getCustomProgramInfo(conn,action.getAdminId(), action.getProgram());
        return model;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return CustomProgramInfoAction.class;
    }
}
