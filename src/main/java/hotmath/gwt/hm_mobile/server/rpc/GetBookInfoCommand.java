package hotmath.gwt.hm_mobile.server.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.hm_mobile.client.model.BookInfoModel;
import hotmath.gwt.hm_mobile.client.rpc.GetBookInfoAction;
import hotmath.gwt.hm_mobile.server.dao.BooksDao;

import java.sql.Connection;

public class GetBookInfoCommand implements ActionHandler<GetBookInfoAction, BookInfoModel>{

	@Override
    public BookInfoModel execute(Connection conn, GetBookInfoAction action) throws Exception {
	    return BooksDao.getInstance().getBookInfo(action.getBookModel());
    }

	@Override
    public Class<? extends Action<? extends Response>> getActionType() {
	    return GetBookInfoAction.class;
    }
}
