package hotmath.gwt.shared.server.service.command.helper;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;
import hotmath.gwt.shared.server.service.command.GetStudentGridPageCommand;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>GetStudentGridPageHelper</code> supports the retrieval of extended student data on
 * a page-by-page basis (limited number of rows) and the retrieval of a column (all rows) of
 * extended student data for sorting.
 *  
 * @author bob
 *
 */

public class GetStudentGridPageHelper {
	
    /**
     *  Ensure sublist has all fields populated, also add to studentPool if indicated
     *  
     * @param conn
     * @param action
     * @param subList
     * @param studentPool
     * @throws Exception
     */
    public void getAnyMissingData(final Connection conn, final GetStudentGridPageAction action, List<StudentModelExt> subList,
        List<StudentModelExt> studentPool) throws Exception {
    	List<Integer> studentUids = getStudentUidsWithMissingData(subList);
    	if (studentUids.size() > 0) {
    		// get missing data
    		List<StudentModelI> extendedSummaries = new CmStudentDao().getStudentExtendedSummaries(conn, studentUids);
    		
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
    		
       		// assign extended summary data to student pool
    		for (StudentModelExt sme : studentPool ) {
    			if (map.containsKey(sme.getUid())) {
    				sme.assignExtendedData(map.get(sme.getUid()));
    			}
    		}

    		// refresh cache
            String cacheKey = GetStudentGridPageCommand.getCacheKey(action.getAdminId());
            CmCacheManager.getInstance().addToCache(CacheName.STUDENT_PAGED_DATA, cacheKey, studentPool);
    	}
    }

    private static Map<String, String> extendedFieldMap = new HashMap<String,String>();
    
    static {
    	extendedFieldMap.put(StudentModelExt.LAST_LOGIN_KEY,    StudentModelExt.HAS_LAST_LOGIN_KEY);
    	extendedFieldMap.put(StudentModelExt.LAST_QUIZ_KEY,     StudentModelExt.HAS_LAST_QUIZ_KEY);
    	extendedFieldMap.put(StudentModelExt.PASSING_COUNT_KEY, StudentModelExt.HAS_PASSING_COUNT_KEY);
    	extendedFieldMap.put(StudentModelExt.STATUS_KEY,        StudentModelExt.HAS_STATUS_KEY);    	
    	extendedFieldMap.put(StudentModelExt.TUTORING_USE_KEY,  StudentModelExt.HAS_TUTORING_USE_KEY);
    }
    
    public void fillSortField(final Connection conn, String sortField, List<StudentModelExt>studentPool,
    	List<StudentModelExt> allStudents, Integer adminUid) throws Exception {
    	
    	if (! extendedFieldMap.containsKey(sortField)) return;
    	
    	String hasFieldKey = extendedFieldMap.get(sortField);
    	
    	List<Integer> uids = new ArrayList<Integer> ();
    	
    	for (StudentModelExt sm : allStudents) {
    		if (! sm.getHasExtendedData() && (sm.get(hasFieldKey) == null || ! (Boolean)sm.get(hasFieldKey))) {
    			uids.add(sm.getUid());
    		}
    	}
    	
    	System.out.println("+++ fillSortField(): uids.size(): " + uids.size());
    	
    	if (uids.size() < 1) return;
    	
    	CmStudentDao dao = new CmStudentDao();
    	List<StudentModelI> extendedData = dao.getStudentExtendedData(conn, hasFieldKey, uids);
    	
    	System.out.println("+++ fillSortField(): extendedData.size(): " + extendedData.size());
    	
		// load extended data into a Map
		Map<Integer, StudentModelI> map = new HashMap<Integer, StudentModelI>();
		for (StudentModelI sm : extendedData) {
			map.put(sm.getUid(), sm);
		}
		
		assignExtendedData(sortField, studentPool, map);
		
		assignExtendedData(sortField, allStudents, map);
		
		// refresh cache
        String cacheKey = GetStudentGridPageCommand.getCacheKey(adminUid);
        CmCacheManager.getInstance().addToCache(CacheName.STUDENT_PAGED_DATA, cacheKey, allStudents);		
    }

	private void assignExtendedData(String sortField,
			List<StudentModelExt> studentPool, Map<Integer, StudentModelI> map) {
		
		System.out.println("+++ assignExtendedData(): sortField: " + sortField);
		
		// assign extended data to studentPool
		for (StudentModelExt sme : studentPool ) {
			Integer uid = sme.getUid();
			if (map.containsKey(uid)) {
				
				System.out.println("+++ assignExtendedData(): uid: " + uid);

	            if (StudentModelExt.LAST_LOGIN_KEY.equals(sortField)) {
	                sme.setLastLogin(map.get(uid).getLastLogin());
	                sme.setHasLastLogin(true);
					System.out.println("+++ assignExtendedData(): lastLogin: " +map.get(uid).getLastLogin());

	            	continue;
	            }
	            if (StudentModelExt.LAST_QUIZ_KEY.equals(sortField)) {
	                sme.setLastQuiz(map.get(uid).getLastQuiz());
	                sme.setHasLastQuiz(true);
					System.out.println("+++ assignExtendedData(): lastQuiz: " + map.get(uid).getLastQuiz());

	            	continue;
	            }
	            
				if (StudentModelExt.PASSING_COUNT_KEY.equals(sortField)) {
    				sme.setNotPassingCount(map.get(sme.getUid()).getNotPassingCount());
    				sme.setPassingCount(map.get(uid).getPassingCount());
    				sme.setHasPassingCount(true);
    				System.out.println("+++ assignExtendedData(): passingCount, notPassingCount" +
    						map.get(sme.getUid()).getPassingCount() + ", " + map.get(sme.getUid()).getNotPassingCount());

    				continue;
				}

				if (StudentModelExt.STATUS_KEY.equals(sortField)) {
	                sme.setStatus(map.get(uid).getStatus());
	                sme.setHasStatus(true);
					System.out.println("+++ assignExtendedData(): status: " + map.get(uid).getStatus());

	            	continue;
	            }

				if (StudentModelExt.TUTORING_USE_KEY.equals(sortField)) {
	                sme.setTutoringUse(map.get(uid).getTutoringUse());
					sme.setHasTutoringUse(true);
					System.out.println("+++ assignExtendedData(): tutoring use: " + map.get(uid).getTutoringUse());

	            	continue;
	            }
			}
		}
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
