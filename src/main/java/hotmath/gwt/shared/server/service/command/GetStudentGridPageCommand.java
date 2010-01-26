package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModelI;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.model.CmStudentPagingLoadResult;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    @SuppressWarnings("unchecked")
    @Override
    public CmStudentPagingLoadResult<StudentModelExt> execute(Connection conn, GetStudentGridPageAction action)
            throws Exception {

        PagingLoadConfig config = action.getLoadConfig();

        String cacheKey = "paged_" + action.getAdminId();

        /**
         * Get all student data for this one admin from cache
         * 
         */
        List<StudentModelExt> _allStudents = (List<StudentModelExt>) CmCacheManager.getInstance().retrieveFromCache(CacheName.STUDENT_PAGED_DATA, cacheKey);
        if (action.isForceRefresh() || _allStudents == null) {
            _allStudents = new ArrayList<StudentModelExt>();
            for (StudentModelI smi : new CmStudentDao().getStudentSummaries(conn, action.getAdminId(), true)) {
                _allStudents.add(new StudentModelExt(smi));
            }
            CmCacheManager.getInstance().addToCache(CacheName.STUDENT_PAGED_DATA, cacheKey, _allStudents);
        }

        /**
         * Prepare a holder for the page of data to return
         * 
         */
        ArrayList<StudentModelExt> sublist = new ArrayList<StudentModelExt>();

        int start = config.getOffset();
        List<StudentModelExt> studentPool = null;

        /** if group not null and matches either group NONE or is set to NO_FILTERING
         * 
         */
        if (action.getGroupFilter() != null && 
                !action.getGroupFilter().equals(GroupInfoModel.NO_FILTERING.toString()) &&
                !action.getGroupFilter().equals(GroupInfoModel.NONE_GROUP.toString())) {
            /**
             * filtered values only matching filtered group
             * 
             */
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
        }
        

        /**
         * Should the student pool be sorted?
         * 
         */
        if (config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            if (sortField != null) {
                Collections.sort(studentPool, config.getSortInfo().getSortDir().comparator(
                        new StudentGridComparator(sortField)));
            }
        }

        /** Extract the page from the entire pool
         * 
         */
        int limit = studentPool.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + action.getLoadConfig().getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(studentPool.get(i));
        }
        return new CmStudentPagingLoadResult<StudentModelExt>(sublist, config.getOffset(), studentPool.size());
    }
    
    
    /** run through specialized field searches looking for matches
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
        
        if(checkMatch(sme.getProgramDescr(), search))
            return true;
        
        if(checkMatch(sme.getStatus(), search))
            return true;
        
        if(checkMatch(sme.getLastQuiz(), search))
            return true;

        if(checkMatch(sme.getLastLogin(), search))
            return true;

        if(checkMatch(sme.getTotalUsage(), search))
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
        if (sortField.equals(StudentModelExt.NAME_KEY)) {
            return p1.getName().compareToIgnoreCase(p2.getName());
        } else if (sortField.equals(StudentModelExt.PASSCODE_KEY)) {
            return p1.getPasscode().compareToIgnoreCase(p2.getPasscode());
        } else if (sortField.equals(StudentModelExt.GROUP_KEY)) {
            return p1.getGroup().compareToIgnoreCase(p2.getGroup());
        } else if (sortField.equals(StudentModelExt.PROGRAM_DESCR_KEY)) {
            return p1.getProgramDescr().compareToIgnoreCase(p2.getProgramDescr());
        } else if (sortField.equals(StudentModelExt.STATUS_KEY)) {
            return p1.getStatus().compareToIgnoreCase(p2.getStatus());
        } else if (sortField.equals(StudentModelExt.LAST_QUIZ_KEY)) {
            return nz(p1.getLastQuiz()).compareToIgnoreCase(nz(p2.getLastQuiz()));
        } else if (sortField.equals(StudentModelExt.LAST_LOGIN_KEY)) {
            return nz(p1.getLastLogin()).compareToIgnoreCase(nz(p2.getLastLogin()));
        } else if (sortField.equals(StudentModelExt.TOTAL_USAGE_KEY)) {
            return p1.getTotalUsage() - p2.getTotalUsage();
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
        if (z == null)
            return "";
        else
            return z;
    }
}