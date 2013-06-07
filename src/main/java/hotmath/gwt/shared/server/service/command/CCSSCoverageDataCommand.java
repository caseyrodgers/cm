package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.CCSSReportDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CCSSCoverageData;
import hotmath.gwt.shared.client.rpc.action.CCSSCoverageDataAction;

import java.sql.Connection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 
 * @author bob
 *
 */

public class CCSSCoverageDataCommand implements ActionHandler< CCSSCoverageDataAction, CmList<CCSSCoverageData>>{

    @Override
    public CmList<CCSSCoverageData> execute(Connection conn, CCSSCoverageDataAction action) throws Exception {
        try {
            CmList<CCSSCoverageData> list=null;

            Date fromDate = action.getFrom();
            if(fromDate == null) {
                fromDate = new GregorianCalendar(2010,0,0).getTime();
            }
            Date toDate = action.getTo();
            if(toDate == null) {
                toDate = new GregorianCalendar(2050,0,0).getTime();
            }

            CCSSReportDao crDao = CCSSReportDao.getInstance();
            switch(action.getType()) {
                case STUDENT_ASSIGNED_COMPLETED:
                    list = toCmList(crDao.getStudentAssignmentStandardNames(action.getUID(), fromDate, toDate));
                    break;

                case STUDENT_QUIZZED_PASSED:
                    list = toCmList(crDao.getStudentQuizStandardNames(action.getUID(), fromDate, toDate));
                    break;

                case STUDENT_REVIEWED:
                    list = toCmList(crDao.getStudentReviewStandardNames(action.getUID(), fromDate, toDate));
                    break;

                case STUDENT_COMBINED:
                    list = toCmList(crDao.getStudentCombinedStandardNames(action.getUID(), fromDate, toDate));
                    break;

                case GROUP:
                    //list = crDao.getGroupStandardNames(conn, action.getGroupId(), fromDate, toDate);
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
    
	private CmList<CCSSCoverageData> toCmList(List<CCSSCoverageData> list) {
        CmList<CCSSCoverageData> listOut = new CmArrayList<CCSSCoverageData>();
        listOut.addAll(list);
        return listOut;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return  CCSSCoverageDataAction.class;
    }
}
