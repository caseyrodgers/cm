package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.CCSSReportDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CCSSCoverageData;
import hotmath.gwt.shared.client.model.CCSSStandard;
import hotmath.gwt.shared.client.model.CCSSStrandCoverageData;
import hotmath.gwt.shared.client.rpc.action.CCSSStrandCoverageDataAction;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 
 * @author bob
 *
 */

public class CCSSStrandCoverageDataCommand implements ActionHandler<CCSSStrandCoverageDataAction, CmList<CCSSStrandCoverageData>>{

    @Override
    public CmList<CCSSStrandCoverageData> execute(Connection conn, CCSSStrandCoverageDataAction action) throws Exception {
        try {
            Date fromDate = action.getFrom();
            if(fromDate == null) {
                fromDate = new GregorianCalendar(2010,0,0).getTime();
            }
            Date toDate = action.getTo();
            if(toDate == null) {
                toDate = new GregorianCalendar(2050,0,0).getTime();
            }

            int adminId = action.getAdminId();
            int uid = action.getUID();

            CCSSReportDao crDao = CCSSReportDao.getInstance();

            List<CCSSCoverageData> list = crDao.getStandardNamesForStudentAndLevel(Arrays.asList(uid), action.getLevelName(), fromDate, toDate);

            List<CCSSStandard> stdList = crDao.getStandardsForLevel(action.getLevelName(), true);

            CmList<CCSSStrandCoverageData> covData = new CmArrayList<CCSSStrandCoverageData>();

            for (CCSSStandard std : stdList) {
            	String stdName = std.getName();
            	CCSSStrandCoverageData scData = new CCSSStrandCoverageData();
            	covData.add(scData);
            	scData.setCcssName(stdName);
            	scData.setLevelName(action.getLevelName());
            	scData.setSequenceNum(std.getSequenceNum());
            	for (CCSSCoverageData data : list) {
            		if (stdName.equalsIgnoreCase(data.getName())) {
            			if (data.getColumnLabels().get(0).equalsIgnoreCase("ASSIGNMENT")) {
            				scData.setAsAssignment(true);
            				continue;
            			}
            			if (data.getColumnLabels().get(0).equalsIgnoreCase("LESSON")) {
            				scData.setAsLesson(true);
            				continue;
            			}
            			if (data.getColumnLabels().get(0).equalsIgnoreCase("QUIZ")) {
            				scData.setAsQuiz(true);
            				continue;
            			}
            		}
            		
            	}
            }

            return covData;
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return  CCSSStrandCoverageDataAction.class;
    }
}
