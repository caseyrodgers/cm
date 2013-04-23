package hotmath.gwt.hm_mobile.server.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.rpc.BookSearchAction;
import hotmath.gwt.hm_mobile.server.dao.BooksDao;

import java.sql.Connection;

public class BookSearchCommand implements ActionHandler<BookSearchAction, CmList<BookModel>>, ActionHandlerManualConnectionManagement {

	@Override
    public CmList<BookModel> execute(Connection conn, BookSearchAction action) throws Exception {
		CmList<BookModel> list = new CmArrayList<BookModel>();
	    list.addAll(BooksDao.getInstance().searchForBooks(action.getSearchFor()));
	    return list;
    }

	@Override
    public Class<? extends Action<? extends Response>> getActionType() {
	    return BookSearchAction.class;
    }

}
