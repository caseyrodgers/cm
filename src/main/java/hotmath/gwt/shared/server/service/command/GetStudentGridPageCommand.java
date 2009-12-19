package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
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
 * Provide paged access to a given admin's list of students
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
        List<StudentModelExt> _allStudents = (List<StudentModelExt>) CmCacheManager.getInstance().retrieveFromCache(
                CacheName.STUDENT_PAGED_DATA, cacheKey);
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

        if (action.getGroupFilter() != null) {
            /**
             * filtered values
             * 
             */
            studentPool = new ArrayList<StudentModelExt>();
            for (StudentModelExt sme : _allStudents) {
                if (sme.getGroupId().equals(action.getGroupFilter()))
                    studentPool.add(sme);
            }
        } else {
            /**
             * unfiltered
             * 
             */
            studentPool = _allStudents;
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

        int limit = studentPool.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + action.getLoadConfig().getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(studentPool.get(i));
        }
        return new CmStudentPagingLoadResult<StudentModelExt>(sublist, config.getOffset(), studentPool.size());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetStudentGridPageAction.class;
    }

}

/**
 * Provide soring control the student grid
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