package hotmath.gwt.solution_editor.server.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.SolutionSearchModel;
import hotmath.gwt.solution_editor.client.rpc.SearchForSolutionsAction;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;

import java.sql.Connection;

public class SearchForSolutionsCommand implements ActionHandler<SearchForSolutionsAction, CmList<SolutionSearchModel>> {
    
    @Override
    public CmList<SolutionSearchModel> execute(Connection conn, SearchForSolutionsAction action) throws Exception {
        return new CmSolutionManagerDao().searchForSolutions(conn,action.getSearchFor(), action.getSearchText(),action.isIncludeInActive());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return SearchForSolutionsAction.class;
    }
}
