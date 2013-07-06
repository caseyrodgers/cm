package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.CCSSReportDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CCSSDetail;
import hotmath.gwt.shared.client.rpc.action.CCSSDetailAction;

import java.sql.Connection;

/**
 * 
 * @author bob
 *
 */

public class CCSSDetailCommand implements ActionHandler<CCSSDetailAction, CCSSDetail>{

    @Override
    public CCSSDetail execute(Connection conn, CCSSDetailAction action) throws Exception {
        try {
            CCSSReportDao crDao = CCSSReportDao.getInstance();
            return crDao.getCCSSDetail(action.getStandardName());
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return  CCSSDetailAction.class;
    }
}
