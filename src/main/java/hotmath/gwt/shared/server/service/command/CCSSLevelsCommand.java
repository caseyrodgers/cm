package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.CCSSReportDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CCSSGradeLevel;
import hotmath.gwt.shared.client.rpc.action.CCSSLevelsAction;

import java.sql.Connection;
import java.util.List;

/**
 * 
 * @author bob
 *
 */

public class CCSSLevelsCommand implements ActionHandler<CCSSLevelsAction, CmList<CCSSGradeLevel>>{

    @Override
    public CmList<CCSSGradeLevel> execute(Connection conn, CCSSLevelsAction action) throws Exception {
        try {
            CCSSReportDao crDao = CCSSReportDao.getInstance();
            List<String> list = crDao.getStandardLevelNames();
            CmList<CCSSGradeLevel> retList = new CmArrayList<CCSSGradeLevel>();
            for (String levelName : list) {
            	CCSSGradeLevel level = new CCSSGradeLevel(levelName);
            	retList.add(level);
            }
            return retList;
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return  CCSSLevelsAction.class;
    }
}
