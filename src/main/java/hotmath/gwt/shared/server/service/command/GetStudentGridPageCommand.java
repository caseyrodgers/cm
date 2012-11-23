package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_admin.server.model.StudentActivityDao;
import hotmath.gwt.cm_admin.server.model.StudentQuickSearcher;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.model.CmStudentPagingLoadResult;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;
import hotmath.gwt.shared.server.service.command.helper.GetStudentGridPageHelper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;

/**
 * Provide GXT paged access to a given admin's list of students
 * 
 * 
 *  1. maintaining a shared list of all students for a given uid.
 *  2. creates a subset that might have been filtered.
 *  3. sort the subset
 *  4. return the proper page from subset.
 *  
 * @author casey
 * 
 */
public class GetStudentGridPageCommand implements
        ActionHandler<GetStudentGridPageAction, CmStudentPagingLoadResult<StudentModelExt>> {

	private static Logger logger = Logger.getLogger(GetStudentGridPageCommand.class);

	GetStudentGridPageHelper pageHelper;
	
	public GetStudentGridPageCommand() {
		pageHelper = new GetStudentGridPageHelper();
	}
	
    @Override
    public CmStudentPagingLoadResult<StudentModelExt> execute(Connection conn, GetStudentGridPageAction action)
            throws Exception {

        List<StudentModelExt> studentPool = getStudentPool(action);

        
        /**
         * Prepare a holder for the page of data to return
         * 
         */
        List<StudentModelExt> sublist = new ArrayList<StudentModelExt>();
        /** Extract the page from the entire pool
         * 
         */
        int limit = studentPool.size();
        if (action.getLoadConfig().getLimit() > 0) {
            limit = Math.min(action.getLoadConfig().getOffset() + action.getLoadConfig().getLimit(), limit);
        }
        int start = (action.getLoadConfig().getOffset() >= 0) ? action.getLoadConfig().getOffset() : 0;
        for (int i = start; i < limit; i++) {
            sublist.add(studentPool.get(i));
        }
        
        // pageHelper.getAnyMissingData(conn, action, sublist);
        
        return new CmStudentPagingLoadResult<StudentModelExt>(sublist, action.getLoadConfig().getOffset(), studentPool.size());
    }
    
    @SuppressWarnings("unchecked")
    public List<StudentModelExt> getStudentPool(GetStudentGridPageAction action) throws Exception {


        PagingLoadConfig config = action!=null?action.getLoadConfig():null;
        if (logger.isDebugEnabled()) logger.debug("aid=" + action.getAdminId() + " page config: " + config);
        
        String cacheKey = getCacheKey(action.getAdminId());
        if (logger.isDebugEnabled()) logger.debug("aid=" + action.getAdminId() + " cache key: " + cacheKey);
        
        
        /**
         * Get all student data for this one admin from cache
         * 
         */
        List<StudentModelExt> _allStudents = (List<StudentModelExt>) CmCacheManager.getInstance().retrieveFromCache(CacheName.STUDENT_PAGED_DATA, cacheKey);
        if (action.isForceRefresh() || _allStudents == null) {
            if (logger.isDebugEnabled()) logger.debug("aid=" + action.getAdminId() + " creating _allStudents");
            _allStudents = new ArrayList<StudentModelExt>();
            for (StudentModelI smi : CmStudentDao.getInstance().getStudentSummaries(action.getAdminId(), true)) {
                _allStudents.add(new StudentModelExt(smi));
            }
            CmCacheManager.getInstance().addToCache(CacheName.STUDENT_PAGED_DATA, cacheKey, _allStudents);
        }
        else {
            if (logger.isDebugEnabled()) logger.debug("aid=" + action.getAdminId() + " using cached all_students");
        }
        if (logger.isDebugEnabled()) logger.debug("aid=" + action.getAdminId() + " _allStudents: " + _allStudents.size());        

        List<StudentModelExt> studentPool = null;

        /** if group not null and matches either group NONE or is set to NO_FILTERING
         * 
         * @TODO: why group NONE is used.
         * 
         *  && !action.getGroupFilter().equals(GroupInfoModel.NONE_GROUP.toString())
         */
        if (action.getGroupFilter() != null && 
                !action.getGroupFilter().equals(GroupInfoModel.NO_FILTERING.toString())) {
            /**
             * filtered values only matching filtered group
             * 
             */
            if (logger.isDebugEnabled()) logger.debug("aid=" + action.getAdminId() + "filtering student pool");
            studentPool = new ArrayList<StudentModelExt>();
            for (StudentModelExt sme : _allStudents) {
                if (sme.getGroupId().equals(action.getGroupFilter()))
                    studentPool.add(sme);
            }
        } else {
            /**
             * un-filtered uses the entire set
             * 
             */
            studentPool = _allStudents;
        }
        if (logger.isDebugEnabled()) logger.debug("aid=" + action.getAdminId() + " filtered student pool: " + studentPool.size());        
        
        /** apply the quick search algorithm
         * 
         */
        if(action.getQuickSearch() != null) {
            String search = action.getQuickSearch();
            List<StudentModelExt> qsStudentPool = new ArrayList<StudentModelExt>();
            for (StudentModelExt sme : studentPool) {
                if (quickSearchMatch(sme,search)) {
                    qsStudentPool.add(sme);
                }
            }
            
            studentPool = qsStudentPool;
            if (logger.isDebugEnabled()) logger.debug("aid=" + action.getAdminId() + " quick_search student pool: " + studentPool.size());
        }

        /** 
         *  apply date range filter
         *  
         */
        if (action.getDateRange() != null) {
        	
        	String options = action.getFilterMap().get(GetStudentGridPageAction.FilterType.OPTIONS);

        	if (logger.isDebugEnabled())
        		logger.debug(String.format("date range: %s, options: %s", action.getDateRange(),
        				(options != null) ? options : "NULL"));

        	List<Integer> uidList = new ArrayList<Integer>();
        	for (StudentModelExt m : studentPool) {
        	    uidList.add(m.getUid());
        	}
        	StudentActivityDao dao = StudentActivityDao.getInstance();
        	List<Integer> userIds =
        		dao.getStudentsWithActivityInDateRange(uidList, action.getDateRange(), options);

            if (userIds.size() != uidList.size()) {
            	
            	List<StudentModelExt> drStudentPool = new ArrayList<StudentModelExt>();
            	for (StudentModelExt m : studentPool) {
            		if (userIds.contains(m.getUid())) {
            			drStudentPool.add(m);
            		}
            	}
            	studentPool = drStudentPool;            	
            }
        }

        /**
         * Should the student pool be sorted?
         * 
         */
        if (config != null && config.getSortInfo() != null && config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            if (sortField != null) {
            	
            	if (logger.isDebugEnabled()) logger.debug("aid=" + action.getAdminId() + " sorting student pool");

            	/**
            	 * fill the sort field as needed in both studentPool and _allStudents
            	 * student models are fully populated when first read, no need to fill the sort field
            	 */
            	//pageHelper.fillSortField(conn, sortField, studentPool, _allStudents, action.getAdminId());

                StudentGridComparator cmp = new StudentGridComparator(sortField);

                Collections.sort(studentPool, config.getSortInfo().getSortDir().comparator(cmp));
            }
        }   
        if (logger.isDebugEnabled()) logger.debug("aid=" + action.getAdminId() + " sorted student pool: " + studentPool.size());
        
        return studentPool;
    }
    
    /** key used to specify all data for this admin
     * 
     * @param aid
     * @return
     */
    static public String getCacheKey(int aid) {
        return "paged_" + aid;
    }

    
    /** run through specialized field searches looking for matches
     * 
     * 
     * @param sme
     * @param search
     * @return
     */
    private boolean quickSearchMatch(StudentModelI sme, String search) {
        if(checkMatch(sme.getName(), search))
            return true;
        
        if(checkMatch(sme.getPasscode(), search))
            return true;
        
        if(checkMatch(sme.getGroup(), search))
            return true;
        
        if(checkMatch(sme.getProgram().getProgramDescription(), search))
            return true;

        if(checkMatch(sme.getStatus(), search))
            return true;
        
        if(checkMatch(sme.getLastQuiz(), search))
            return true;

        if(checkMatch(sme.getLastLogin(), search))
            return true;

        if(checkMatch(sme.getTotalUsage(), search))
            return true;
        
        if (checkMatch(formatQuizzes(sme), search))
        	return true;

        return false;
    }
    
    /** return true if search 'string' is in value 'string'
     * 
     * @param value
     * @param search
     * @return
     */
    private boolean checkMatch(String value, String search) {
        if(value == null)
            return false;
        return value.toLowerCase().contains(search.toLowerCase());        
    }
    
    private boolean checkMatch(Integer value, String search) {
        if(value == null)
            return false;
        return checkMatch(value.toString(), search); 
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetStudentGridPageAction.class;
    }

    private String formatQuizzes(StudentModelI sm) {
        if ((sm.getPassingCount() > 0 || sm.getNotPassingCount() > 0) && sm.getProgram().getCustom().isCustom() == false) {
        	if (logger.isDebugEnabled())
        	    logger.debug("+++ formatQuizzes: " + String.format("%d passed out of %d", sm.getPassingCount(), (sm.getPassingCount()+sm.getNotPassingCount())));
            return String.format("%d passed out of %d", sm.getPassingCount(), (sm.getPassingCount()+sm.getNotPassingCount()));
        }
        else {
        	return "";
        }
    }

    /** Create a map of UIDS to StudentModelExt 
     *  instances that DO NOT have their extended data read.
     *  
     * @param list
     * @return
     */
    private Map <Integer, StudentModelExt> collectNotFullyPopulated(List<StudentModelExt> list) {
    	Map<Integer, StudentModelExt> rMap = new HashMap<Integer, StudentModelExt> ();
    	for (StudentModelExt sme : list) {
    		if (sme.getHasExtendedData() == false) {
    			rMap.put(sme.getUid(), sme);
    		}
    	}
    	return rMap;
    }
    
    /** Remove from map any entries that are in list
     * 
     * @param smMap
     * @param smList
     */
    private void removeOverlap(Map<Integer, StudentModelExt> smMap, List<StudentModelExt> smList) {
    	for (StudentModelExt sme : smList) {
    		if (smMap.containsKey(sme.getUid())) {
    			smMap.remove(sme.getUid());
    		}
    	}
    }

}

/**
 * Provide sorting control for the student grid
 * 
 * @author casey
 * 
 */
class StudentGridComparator implements Comparator<StudentModelExt> {

    String sortField;
    
    public StudentGridComparator(String sortField) {
        this.sortField = sortField;
    }

    public int compare(StudentModelExt p1, StudentModelExt p2) {
        
        
        boolean p1IsCustom = p1.getProgram().getCustom().isCustom();
        boolean p2IsCustom = p2.getProgram().getCustom().isCustom();
        
    	if (sortField.equals(StudentModelExt.NAME_KEY)) {
            return p1.getName().compareToIgnoreCase(p2.getName());
        } else if (sortField.equals(StudentModelExt.PASSCODE_KEY)) {
            return p1.getPasscode().compareToIgnoreCase(p2.getPasscode());
        } else if (sortField.equals(StudentModelExt.GROUP_KEY)) {
            return p1.getGroup().compareToIgnoreCase(p2.getGroup());
        } else if (sortField.equals(StudentModelExt.PROGRAM_DESCR_KEY)) {
            return p1.getProgram().getProgramDescription().compareToIgnoreCase(p2.getProgram().getProgramDescription());
        } else if (sortField.equals(StudentModelExt.STATUS_KEY)) {
            return p1.getStatus().compareToIgnoreCase(p2.getStatus());
        } else if (sortField.equals(StudentModelExt.LAST_LOGIN_KEY)) {
            return nz(p1.getLastLogin()).compareToIgnoreCase(nz(p2.getLastLogin()));
        } else if (sortField.equals(StudentModelExt.TOTAL_USAGE_KEY)) {
            return p1.getTotalUsage() - p2.getTotalUsage();
        } else if (sortField.equals(StudentModelExt.TUTORING_USE_KEY)) {
            return p1.getTutoringUse() - p2.getTutoringUse();
        } else if (sortField.equals(StudentModelExt.LAST_QUIZ_KEY)) { 
        	
            String lq1 = (p1IsCustom) ? "" : p1.getLastQuiz();
            String lq2 = (p2IsCustom) ? "" : p2.getLastQuiz();
            
            if ((lq1 == null || lq1.trim().length() == 0) && (lq2 == null | lq2.trim().length() == 0)) return 0;
            
            return nz(lq1).compareToIgnoreCase(nz(lq2));

        } else if(sortField.equals(StudentModelExt.PASSING_COUNT_KEY)) {
        	
        	int t1 = (p1IsCustom) ? 0 : p1.getNotPassingCount() + p1.getPassingCount();
        	int t2 = (p2IsCustom) ? 0 : p2.getNotPassingCount() + p2.getPassingCount();

            // check total # quizzes
        	if (t1 != t2) return t1 - t2;
        	
        	// total same, sort by number passed
        	int pass1 = (p1IsCustom) ? 0 : p1.getPassingCount();
        	int pass2 = (p2IsCustom) ? 0 : p2.getPassingCount();
        	int pDiff = pass1 - pass2;
        	if (pDiff != 0) return pDiff;
//        	
//            // following test disabled for now
//        	if (false && t1 > 0 && t2 > 0) {
//            	// sort on ratio of passed quizzes
//            	float f1 = (float) p1.getPassingCount() / (float) t1;
//            	float f2 = (float) p2.getPassingCount() / (float) t2;
//            	if (f1 != f2) {
//        	    	return (f1 < f2) ? -1 : 1;
//        	    }
//        	}

            /** 
             *  passed ratios the same or not available, 
             *  so sort on total number of tests taken
             */
            return t1 - t2;
        }

        return 0;
    }

    /**
     * Make sure string is not null, if so return empty string.
     * 
     * @param z
     * @return
     */
    private String nz(String z) {
    	return (z == null) ? "" : z;
    }
}