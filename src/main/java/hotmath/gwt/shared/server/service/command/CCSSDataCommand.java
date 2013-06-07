package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.CCSSReportDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CCSSData;
import hotmath.gwt.shared.client.rpc.action.CCSSDataAction;

import java.sql.Connection;

public class CCSSDataCommand implements ActionHandler< CCSSDataAction, CCSSData>{

    @Override
    public CCSSData execute(Connection conn, CCSSDataAction action) throws Exception {
        try {
            
            CCSSReportDao crDao = CCSSReportDao.getInstance();
            return crDao.getCCSSData();
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return  CCSSDataAction.class;
    }
}
