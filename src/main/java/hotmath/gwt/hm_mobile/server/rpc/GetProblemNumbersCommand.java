package hotmath.gwt.hm_mobile.server.rpc;

import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc.server.service.ActionHandlerManualConnectionManagement;
import hotmath.gwt.hm_mobile.client.rpc.GetProblemNumbersAction;
import hotmath.gwt.hm_mobile.server.dao.BooksDao;

import java.sql.Connection;

public class GetProblemNumbersCommand implements ActionHandler<GetProblemNumbersAction, CmList<ProblemNumber>>, ActionHandlerManualConnectionManagement{
	@Override
    public CmList<ProblemNumber> execute(Connection conn, GetProblemNumbersAction action) throws Exception {

		CmList<ProblemNumber> list = new CmArrayList<ProblemNumber>();
		list.addAll(BooksDao.getInstance().getProblemNumbers(action.getBook(), action.getPage()));
		return list;
    }

	@Override
    public Class<? extends Action<? extends Response>> getActionType() {
	    return GetProblemNumbersAction.class;
    }
}
