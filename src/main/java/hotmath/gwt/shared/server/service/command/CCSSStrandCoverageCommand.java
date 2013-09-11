package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.CCSSReportDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSStrandCoverage;
import hotmath.gwt.shared.client.rpc.action.CCSSStrandCoverageAction;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author bob
 *
 */

public class CCSSStrandCoverageCommand implements ActionHandler<CCSSStrandCoverageAction, CmList<CCSSStrandCoverage>>{

    @Override
    public CmList<CCSSStrandCoverage> execute(Connection conn, CCSSStrandCoverageAction action) throws Exception {
        try {
            List<CCSSStrandCoverage> sList = null;

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
            Map<Integer, List<CCSSStrandCoverage>> map = crDao.getCCSSStrandCoverageForStudent(Arrays.asList(uid), fromDate, toDate);

            sList = crDao.getCCSSStrandCoverage();

            List<CCSSStrandCoverage> list = map.get(uid);
            
            calculatePercentComplete(sList, list);

            return toCmList(sList);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
	private void calculatePercentComplete(List<CCSSStrandCoverage> sList,
			List<CCSSStrandCoverage> list) {
		for (CCSSStrandCoverage stdCov : sList) {
			String name = stdCov.getLevelName();
			stdCov.setPercentComplete(String.format("%d%s", 0, "%"));
			stdCov.setLabel(String.format("%s  (%s)", name, stdCov.getPercentComplete()));
			float stdCount = stdCov.getCount();
			if (list == null) continue;
			for (CCSSStrandCoverage cov : list) {
				if (name.equals(cov.getLevelName())) {
					// calculate % complete
					int percentComplete = Math.round(((float)cov.getCount()*100f)/stdCount);
					stdCov.setPercentComplete(String.format("%d%s", percentComplete, "%"));
					stdCov.setLabel(String.format("%s  (%s)", name, stdCov.getPercentComplete()));
					break;
				}
			}
		}
	}

	private CmList<CCSSStrandCoverage> toCmList(List<CCSSStrandCoverage> list) {
        CmList<CCSSStrandCoverage> listOut = new CmArrayList<CCSSStrandCoverage>();
        listOut.addAll(list);
        return listOut;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return  CCSSStrandCoverageAction.class;
    }
}
