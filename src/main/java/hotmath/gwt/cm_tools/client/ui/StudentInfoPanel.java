package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoStatsAction;
import hotmath.gwt.cm_rpc.client.rpc.UserInfoStats;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Composite;

public class StudentInfoPanel extends Composite{
    
    @UiField
    SpanElement password, showWork, widgetPercent, activeMinutes;

    interface MyUiBinder extends UiBinder<Widget, StudentInfoPanel> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public StudentInfoPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    public StudentInfoPanel(StudentModelI studentModel) {
        this();
        password.setInnerHTML(studentModel.getPasscode());
        showWork.setInnerHTML(studentModel.getShowWorkState());
        widgetPercent.setInnerHTML("--");
        
        getTutorWidgetStatsFromServer(studentModel.getUid());
    }
    private void getTutorWidgetStatsFromServer(final int uid) {

        new RetryAction<UserInfoStats>() {
            @Override
            public void attempt() {
                GetUserInfoStatsAction action = new GetUserInfoStatsAction(uid);
                DateRangeWidget dateRange = DateRangeWidget.getInstance();
                Date fromDate = dateRange.getFromDate();
                Date toDate = dateRange.getToDate();
                action.setFromDate(fromDate);
                action.setToDate(toDate);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(UserInfoStats stats) {
                UserTutorWidgetStats ts = stats.getTutorStats();
                if(ts.getCountWidgets() < 1) {
                    widgetPercent.setInnerHTML("n/a");
                }
                else {
                    widgetPercent.setInnerHTML(ts.getCorrectPercent() + "%");
                }
                
                activeMinutes.setInnerHTML(stats.getActiveMinutes() + " minutes");
            }

        }.register();
        
    }
}
