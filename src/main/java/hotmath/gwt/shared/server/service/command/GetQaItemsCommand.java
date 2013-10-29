package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_qa.server.CmQaDao;
import hotmath.gwt.cm_rpc.client.model.QaEntryModel;
import hotmath.gwt.cm_rpc.client.rpc.GetQaItemsAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetQaItemsCommand implements ActionHandler<GetQaItemsAction, CmList<QaEntryModel>>{

    @Override
    public CmList<QaEntryModel> execute(Connection conn, GetQaItemsAction action) throws Exception {
        return new CmQaDao().getQaItems(conn, action.getCategory());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetQaItemsAction.class;
    }    
}
