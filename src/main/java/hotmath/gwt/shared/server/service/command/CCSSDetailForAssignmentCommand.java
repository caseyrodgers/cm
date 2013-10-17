package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.CCSSReportDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CCSSDetail;
import hotmath.gwt.shared.client.rpc.action.CCSSDetailForAssignmentAction;

import java.sql.Connection;
import java.util.List;

/**
 * 
 * @author bob
 *
 */

public class CCSSDetailForAssignmentCommand implements ActionHandler<CCSSDetailForAssignmentAction, CmList<CCSSDetail>>{

    @Override
    public CmList<CCSSDetail> execute(Connection conn, CCSSDetailForAssignmentAction action) throws Exception {
        try {
            CCSSReportDao crDao = CCSSReportDao.getInstance();
            List<CCSSDetail> list = crDao.getCCSSDetailForAssignment(action.getAssignmentKey());
            return xferToCmList(list);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private CmList<CCSSDetail> xferToCmList(List<CCSSDetail> list) {
    	CmList<CCSSDetail> cmList = new CmArrayList<CCSSDetail>();
    	cmList.addAll(list);
		return cmList;
	}

	@Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return  CCSSDetailForAssignmentAction.class;
    }
}
