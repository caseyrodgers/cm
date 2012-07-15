package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_admin.server.model.GradeBookDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetGradeBookDataAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.GradeBookModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;

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

        String dateRange = action.getStudentGridAction().getDateRange();
        logger.debug("dateRange: " + dateRange);
        String[] dates;
        if (dateRange == null || dateRange.length() < 1) {
        	dates = QueryHelper.getDateTimeRange(null, null);
        }
        else {
            dates = QueryHelper.getDatesFromDateRange(dateRange);
        }
        logger.debug("dates: " + dates[0] + ", " + dates[1]);
        
    	List<StudentModelExt> studentList = new GetStudentGridPageCommand().getStudentPool(conn, action.getStudentGridAction());
        List<Integer> uidList = new ArrayList<Integer>();
        for (StudentModelExt m : studentList) {
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





