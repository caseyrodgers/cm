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
        String messages = "";
        CmSolutionManagerDao.SearchResult sr = new CmSolutionManagerDao().replaceTextInSolutions(conn, action.getPidsToReplace(), action.getSearchFor(), action.getReplaceWith());
        int replaced = sr.getReplaced();
        if(sr.getErrors().size() > 0) {
           
            for(String e: sr.getErrors()) {
                if(messages.length() > 0) {
                   messages += "<br/>";
                }
                messages += e;
            }
            messages = "<br/>" + messages;
        }
        RpcData rpcData = new RpcData("status=OK");
        rpcData.putData("errors",  messages);
        rpcData.putData("replaced", replaced);
        return rpcData;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return ReplaceTextSolutionsAction.class;
    }
}
