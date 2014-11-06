package hotmath.gwt.shared.server.service.command.helper;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * <code>GetStudentGridPageHelper</code> supports the retrieval of extended student data on
 * a page-by-page basis (limited number of rows) and the retrieval of a column (all rows) of
 * extended student data for sorting.
 *  
 * @author bob
 *
 */

public class GetStudentGridPageHelper {

	private static final Logger LOGGER = Logger.getLogger(GetStudentGridPageHelper.class);
	
    /**
     *  Ensure sublist has all fields populated, also add to studentPool if indicated
     *  
     * @param conn
     * @param action
     * @param subList
     * @param studentPool
     * @throws Exception
     */
    public void getAnyMissingData(final Connection conn, final GetStudentGridPageAction action, List<StudentModelExt> subList) throws Exception {

    	List<Integer> studentUids = getStudentUidsWithMissingData(subList);

    	if (studentUids.size() < 1) return;

    	// get missing data
    	List<StudentModelI> extendedSummaries = CmStudentDao.getInstance().getStudentExtendedSummaries(conn, studentUids);

    	// load extended summaries into a Map
    	Map<Integer, StudentModelI> map = new HashMap<Integer, StudentModelI>();
    	for (StudentModelI sm : extendedSummaries) {
    		map.put(sm.getUid(), sm);
    	}

    	// assign extended summary data to sub list
    	for (StudentModelExt sme : subList ) {
    		if (map.containsKey(sme.getUid())) {
    			sme.assignExtendedData(map.get(sme.getUid()));
    		}
    	}
    }


    private static Map<String, String> extendedFieldMap = new HashMap<String,String>();
    
    static {
    	extendedFieldMap.put(StudentModelExt.LAST_LOGIN_KEY,    StudentModelExt.HAS_LAST_LOGIN_KEY);
    	extendedFieldMap.put(StudentModelExt.LAST_QUIZ_KEY,     StudentModelExt.HAS_LAST_QUIZ_KEY);
    	extendedFieldMap.put(StudentModelExt.PASSING_COUNT_KEY, StudentModelExt.HAS_PASSING_COUNT_KEY);
    	extendedFieldMap.put(StudentModelExt.TUTORING_USE_KEY,  StudentModelExt.HAS_TUTORING_USE_KEY);
    }
    
	
    
    private List<Integer> getStudentUidsWithMissingData(List<StudentModelExt> list) {
    	List<Integer> uids = new ArrayList<Integer>();
    	for (StudentModelExt sme : list) {
    		if (! sme.getHasExtendedData()) {
    			uids.add(sme.getUid());
    		}
    	}
    	return uids;
    }


}
