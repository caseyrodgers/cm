package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.model.UserWidgetStats;
import hotmath.gwt.cm_rpc.client.rpc.GetUserWidgetStatsAction;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Composite;

public class StudentInfoPanel extends Composite{
    
    @UiField
    SpanElement password, showWork, widgetPercent;

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

        new RetryAction<UserWidgetStats>() {
            @Override
            public void attempt() {
                GetUserWidgetStatsAction action = new GetUserWidgetStatsAction(uid);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(UserWidgetStats stats) {
                widgetPercent.setInnerHTML(stats.getCorrectPercent() + "%");
            }

        }.register();
        
    }
}
