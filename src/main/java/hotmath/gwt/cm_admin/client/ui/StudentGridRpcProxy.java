package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.ui.StudentSearchInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CmStudentPagingLoadResult;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfigBean;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * Create proxy to handle the paged student grid RPC calls
 * 
 */

// extends RpcProxy<PagingLoadConfigBean, PagingLoadResult<StudentModelI>>
public class StudentGridRpcProxy extends RpcProxy<PagingLoadConfigBean, CmStudentPagingLoadResult<StudentModelI>> {

    CmAdminModel cmAdminModel;

    DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");

    private int currentStudentCount;
    DateRangePanel dateRangePanel;

    public StudentGridRpcProxy(CmAdminModel model, DateRangePanel dateRangePanel) {
        this.cmAdminModel = model;
        this.dateRangePanel = dateRangePanel;
    }

    public DateTimeFormat getDateTimeFormat() {
        return this.dateFormat;
    }

    public void setCmAdminModel(CmAdminModel adminModel) {
        this.cmAdminModel = adminModel;
    }


    public void setCurrentStudentCount(int currentStudentCount) {
        this.currentStudentCount = currentStudentCount;
    }

    public int getCurrentStudentCount() {
        return this.currentStudentCount;
    }

    @Override
    public void load(final PagingLoadConfigBean loadConfig, final AsyncCallback<CmStudentPagingLoadResult<StudentModelI>> callback) {
        new RetryAction<CmStudentPagingLoadResult<StudentModelI>>() {
            @Override
            public void attempt() {

                GetStudentGridPageAction pageAction = new GetStudentGridPageAction(cmAdminModel.getUid(), (PagingLoadConfig) loadConfig);
                StudentGridPanel.instance.setPageAction(pageAction);
                setAction(pageAction);

                CmBusyManager.setBusy(true);

                /**
                 * setup request for special handling
                 * 
                 * use module vars to hold request options
                 */
                pageAction.setForceRefresh(StudentGridPanel.instance._forceServerRefresh);
                if (StudentSearchInfo.__instance.getGroupIdFilter() > 0) {
                    pageAction.setGroupFilter(StudentSearchInfo.__instance.getGroupIdFilter());
                    pageAction.addFilter(GetStudentGridPageAction.FilterType.GROUP,Integer.toString(StudentSearchInfo.__instance.getGroupIdFilter()));
                } else {
                    pageAction.setGroupFilter(0);
                }

                pageAction.setQuickSearch(StudentSearchInfo.__instance.getQuickSearch());
                if (StudentSearchInfo.__instance.getQuickSearch() != null && StudentSearchInfo.__instance.getQuickSearch().trim().length() > 0) {
                    pageAction.addFilter(GetStudentGridPageAction.FilterType.QUICKTEXT, StudentSearchInfo.__instance.getQuickSearch().trim());
                }

                String dateRange = null;
                TextField dateRangeFilter = (dateRangePanel != null) ? dateRangePanel.getDateRangeFilter() : null;
                String value = (dateRangeFilter != null) ? dateRangeFilter.getValue() : null;
                /** will be null until initialized */
                if (value != null && value.trim().length() > 0) { // && fromDate
                                                                  // != null &&
                                                                  // toDate !=
                                                                  // null) {
                    dateRange = dateFormat.format(dateRangePanel.getFromDate()) + " - " + dateFormat.format(dateRangePanel.getToDate());
                    pageAction.addFilter(GetStudentGridPageAction.FilterType.DATE_RANGE, dateRange);
                    pageAction.addFilter(GetStudentGridPageAction.FilterType.OPTIONS, (dateRangePanel.getFilterOptions() != null ? dateRangePanel
                            .getFilterOptions().toParsableString() : ""));
                }
                pageAction.setDateRange(dateRange);

                CmShared.getCmService().execute(pageAction, this);

                /** always turn off */
                StudentGridPanel.instance._forceServerRefresh = false;
                pageAction.setForceRefresh(StudentGridPanel.instance._forceServerRefresh);
            }

            @Override
            public void oncapture(CmStudentPagingLoadResult<StudentModelI> students) {
                CmBusyManager.setBusy(false);
                
                /** always reset request options */
                StudentGridPanel.instance._forceServerRefresh = false;
                
                /**
                 * callback the proxy listener
                 * 
                 */
                currentStudentCount = students.getTotalLength();

                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_STUDENT_GRID_FILTERED, StudentGridPanel.instance.getPageAction()));

                callback.onSuccess(students);
                
            }
        }.attempt();
    }

}