package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.model.CmStudentPagingLoadResult;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageExtendedAction;
import hotmath.gwt.shared.server.service.command.helper.GetStudentGridPageHelper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Provide method of reading extended data for current pool
 * 
 * 
 */
public class GetStudentGridPageExtendedCommand implements  ActionHandler<GetStudentGridPageExtendedAction, CmStudentPagingLoadResult<StudentModelExt>> {

	private static Logger logger = Logger.getLogger(GetStudentGridPageExtendedCommand.class);

	GetStudentGridPageHelper pageHelper;
	
	public GetStudentGridPageExtendedCommand() {
		pageHelper = new GetStudentGridPageHelper();
	}

	
	/** Read extended summaries identified in 
	 * 
	 */
	public CmStudentPagingLoadResult<StudentModelExt> execute(Connection conn, GetStudentGridPageExtendedAction action)   throws Exception {
    	CmStudentDao dao = new CmStudentDao();
    	List<StudentModelI> extendedSummaries = dao.getStudentExtendedSummaries(conn, action.getStudentUids());
    	
    	List<StudentModelExt> studentsExt = new ArrayList<StudentModelExt>();
    	for(StudentModelI s: extendedSummaries) {
    		studentsExt.add(new StudentModelExt(s));
    	}
    	
    	// load extended summaries into a Map
    	Map<Integer, StudentModelI> map = new HashMap<Integer, StudentModelI>();
    	for (StudentModelI sm : extendedSummaries) {
    		map.put(sm.getUid(), sm);
    	}

    	String cacheKey = GetStudentGridPageCommand.getCacheKey(action.getAdminId());
    	List<StudentModelExt> _allStudents = (List<StudentModelExt>) CmCacheManager.getInstance().retrieveFromCache(CacheName.STUDENT_PAGED_DATA, cacheKey);
    	
    	// assign extended summary data to pool
    	for (StudentModelExt sme : _allStudents ) {
    		if (map.containsKey(sme.getUid())) {
    			sme.assignExtendedData(map.get(sme.getUid()));
    		}
    	}
        
    	CmStudentPagingLoadResult<StudentModelExt> result = new CmStudentPagingLoadResult<StudentModelExt>(studentsExt);
    	return result;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
    	// TODO Auto-generated method stub
    	return GetStudentGridPageExtendedAction.class;
    }
}