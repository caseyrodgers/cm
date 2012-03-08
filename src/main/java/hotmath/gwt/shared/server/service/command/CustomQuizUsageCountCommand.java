package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.IntValueHolder;
import hotmath.gwt.shared.client.rpc.action.CustomQuizUsageCountAction;

import java.sql.Connection;

/** Provides historical assignment count for specified CQ Id
 *  
 * @author bob
 *
 */
public class CustomQuizUsageCountCommand implements ActionHandler<CustomQuizUsageCountAction, IntValueHolder> {

    @Override
    public IntValueHolder execute(Connection conn, CustomQuizUsageCountAction action) throws Exception {
    	int count = CmQuizzesDao.getInstance().getCustomQuizUsageCount(action.getQuizId()); 
        return new IntValueHolder(count);
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CustomQuizUsageCountAction.class;
    }
}
