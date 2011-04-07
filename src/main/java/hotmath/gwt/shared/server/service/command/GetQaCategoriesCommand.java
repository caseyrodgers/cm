package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_qa.server.CmQaDao;
import hotmath.gwt.cm_rpc.client.model.CategoryModel;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetQaCategoriesAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

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
