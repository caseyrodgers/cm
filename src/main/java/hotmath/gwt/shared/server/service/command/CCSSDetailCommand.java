package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.CCSSReportDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CCSSDetail;
import hotmath.gwt.shared.client.rpc.action.CCSSDetailAction;

import java.sql.Connection;
import java.util.List;

/**
 * 
 * @author bob
 *
 */

public class CCSSDetailCommand implements ActionHandler<CCSSDetailAction, CmList<CCSSDetail>>{

    @Override
    public CmList<CCSSDetail> execute(Connection conn, CCSSDetailAction action) throws Exception {
        try {
            CCSSReportDao crDao = CCSSReportDao.getInstance();
            List<CCSSDetail> list = crDao.getCCSSDetail(action.getStandardNames());
            return xfetToCmList(list);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private CmList<CCSSDetail> xfetToCmList(List<CCSSDetail> list) {
    	CmList<CCSSDetail> cmList = new CmArrayList<CCSSDetail>();
    	cmList.addAll(list);
		return cmList;
	}

	@Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return  CCSSDetailAction.class;
    }
}
