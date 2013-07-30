package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.CCSSReportDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CCSSCoverageBar;
import hotmath.gwt.shared.client.rpc.action.CCSSCoverageChartDataAction;

import java.sql.Connection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 
 * @author bob
 *
 */

public class CCSSCoverageChartDataCommand implements ActionHandler<CCSSCoverageChartDataAction, CmList<CCSSCoverageBar>>{

    @Override
    public CmList<CCSSCoverageBar> execute(Connection conn, CCSSCoverageChartDataAction action) throws Exception {
        try {
            CmList<CCSSCoverageBar> list=null;

            Date fromDate = action.getFrom();
            if(fromDate == null) {
                fromDate = new GregorianCalendar(2010,0,0).getTime();
            }
            Date toDate = action.getTo();
            if(toDate == null) {
                toDate = new GregorianCalendar(2050,0,0).getTime();
            }

            int adminId = action.getAdminId();
            int groupId = action.getUID();

            CCSSReportDao crDao = CCSSReportDao.getInstance();
            switch(action.getType()) {
                case STUDENT_CUMULATIVE_CHART:
                    list = toCmList(crDao.getStudentAllByWeekStandardNames(action.getUID(), fromDate, toDate));
                    break;

                case GROUP_CUMULATIVE_CHART:
                    //list = toCmList(crDao.getGroupAllByNameStandardNames(adminId, groupId, fromDate, toDate));
                    break;

                default:
                    throw new Exception("Unknown report type: " + action);
            }
            return list;
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

	private CmList<CCSSCoverageBar> toCmList(
			List<CCSSCoverageBar> listIn) {
		CmList<CCSSCoverageBar> listOut = new CmArrayList<CCSSCoverageBar>();
		listOut.addAll(listIn);
		return listOut;
	}

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return  CCSSCoverageChartDataAction.class;
    }
}