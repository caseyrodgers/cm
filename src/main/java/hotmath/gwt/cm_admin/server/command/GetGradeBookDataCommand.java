package hotmath.gwt.cm_admin.server.command;

import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_admin.server.model.GradeBookDao;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc.client.rpc.GetGradeBookDataAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.GradeBookModel;
import hotmath.gwt.shared.server.service.command.GetActiveInfoForStudentUidCommand;
import hotmath.gwt.shared.server.service.command.GetStudentGridPageCommand;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/** Get Gradebook data
 *  
 * @author bob
 *
 */
public class GetGradeBookDataCommand implements ActionHandler<GetGradeBookDataAction, CmList<GradeBookModel>> {

    static final Logger logger = Logger.getLogger(GetActiveInfoForStudentUidCommand.class);

    @Override
    public CmList<GradeBookModel> execute(Connection conn, GetGradeBookDataAction action) throws Exception {
        CmList<GradeBookModel> cmList = new CmArrayList<GradeBookModel>();

        String dateRange = action.getStudentGridAction()!=null?action.getStudentGridAction().getDateRange():null;
        logger.debug("dateRange: " + dateRange);
        String[] dates;
        if (dateRange == null || dateRange.length() < 1) {
        	dates = QueryHelper.getDateTimeRange(null, null);
        }
        else {
            dates = QueryHelper.getDatesFromDateRange(dateRange);
        }
        logger.debug("dates: " + dates[0] + ", " + dates[1]);
        
    	List<StudentModelI> studentList = new GetStudentGridPageCommand().getStudentPool(action.getStudentGridAction());
        List<Integer> uidList = new ArrayList<Integer>();
        for (StudentModelI m : studentList) {
        	uidList.add(m.getUid());
        }
        logger.debug("uidList size(): " + uidList.size());

        cmList.addAll(GradeBookDao.getInstance().getGradeBookData(action.getUid(), uidList, dates));
        return cmList;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetGradeBookDataAction.class;
    }
}





