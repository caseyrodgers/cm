package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CustomQuizInfoModel;
import hotmath.gwt.shared.client.rpc.action.CustomQuizInfoAction;

import java.sql.Connection;

/** Provide status information about custom program 
 *  including counts of assigned, use,history and list 
 *  of students who are currently assiged.
 *  
 * @author casey
 *
 */
public class CustomQuizInfoCommand implements ActionHandler<CustomQuizInfoAction, CustomQuizInfoModel> {

    @Override
    public CustomQuizInfoModel execute(Connection conn, CustomQuizInfoAction action) throws Exception {
        return new CmCustomProgramDao().getCustomQuizInfo(conn,action.getAdminId(), action.getQuiz().getQuizId());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CustomQuizInfoAction.class;
    }
}
