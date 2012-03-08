package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.IntValueHolder;
import hotmath.gwt.shared.client.rpc.action.CustomProgramUsageCountAction;

import java.sql.Connection;

/** Provides historical assignment count for specified CP Id
 *  
 * @author bob
 *
 */
public class CustomProgramUsageCountCommand implements ActionHandler<CustomProgramUsageCountAction, IntValueHolder> {

    @Override
    public IntValueHolder execute(Connection conn, CustomProgramUsageCountAction action) throws Exception {
    	int count = CmCustomProgramDao.getInstance().getCustomProgramUsageCount(action.getProgramId()); 
        return new IntValueHolder(count);
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CustomProgramUsageCountAction.class;
    }
}
