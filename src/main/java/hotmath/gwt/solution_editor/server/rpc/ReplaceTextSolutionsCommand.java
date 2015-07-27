package hotmath.gwt.solution_editor.server.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.rpc.ReplaceTextSolutionsAction;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;

import java.sql.Connection;

public class ReplaceTextSolutionsCommand implements ActionHandler<ReplaceTextSolutionsAction, RpcData> {
    
    @Override
    public RpcData execute(Connection conn, ReplaceTextSolutionsAction action) throws Exception {
        //return new CmSolutionManagerDao().searchForSolutions(conn,action.getSearchFor(), action.getSearchText(),action.isIncludeInActive());
        new CmSolutionManagerDao().replaceTextInSolutions(conn, action.getPidsToReplace(), action.getSearchFor(), action.getReplaceWith());
        return new RpcData("status=OK");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return ReplaceTextSolutionsAction.class;
    }
}
