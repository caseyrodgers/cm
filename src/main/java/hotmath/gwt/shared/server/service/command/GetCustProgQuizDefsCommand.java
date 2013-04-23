package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.client.ui.GetCustProgQuizDefsAction;
import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CustomQuizDef;

import java.sql.Connection;

public class GetCustProgQuizDefsCommand implements ActionHandler<GetCustProgQuizDefsAction, CmList<CustomQuizDef>>{


    @Override
    public CmList<CustomQuizDef> execute(Connection conn, GetCustProgQuizDefsAction action) throws Exception {
        return CmQuizzesDao.getInstance().getCustomQuizDefinitions(action.getAdminId());
    }


    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetCustProgQuizDefsAction.class;
    }    
}
