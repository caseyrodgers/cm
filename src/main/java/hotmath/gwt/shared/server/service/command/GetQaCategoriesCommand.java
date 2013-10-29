package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_qa.server.CmQaDao;
import hotmath.gwt.cm_rpc.client.model.CategoryModel;
import hotmath.gwt.cm_rpc.client.rpc.GetQaCategoriesAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetQaCategoriesCommand implements ActionHandler<GetQaCategoriesAction, CmList<CategoryModel>>{

    @Override
    public CmList<CategoryModel> execute(Connection conn, GetQaCategoriesAction action) throws Exception {
        return new CmQaDao().getQaCategories(conn);
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetQaCategoriesAction.class;
    }
}
