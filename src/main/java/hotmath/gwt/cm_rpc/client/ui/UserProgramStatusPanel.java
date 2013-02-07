package hotmath.gwt.cm_rpc.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class UserProgramStatusPanel extends Composite {

    private int uid;
    private int viewCount;
    private UserTutorWidgetStats tutorInputWidgetStats;

    interface MyUiBinder extends UiBinder<Widget, UserProgramStatusPanel> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    SpanElement rppCount, gradedRppCount, gradedRppPercent;
    
    public UserProgramStatusPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public UserProgramStatusPanel(int uid, int viewCount, UserTutorWidgetStats tutorInputWidgetStats) {
        this();
        this.uid = uid;
        this.viewCount = viewCount;
        this.tutorInputWidgetStats = tutorInputWidgetStats;
        
        rppCount.setInnerHTML(viewCount + "");
        gradedRppCount.setInnerHTML(tutorInputWidgetStats.getCountWidgets() + "");
        gradedRppPercent.setInnerHTML(tutorInputWidgetStats.getCorrectPercent() + "");
    }

    @Override
    public String toString() {
        return "UserProgramStatusPanel [uid=" + uid + ", viewCount=" + viewCount + ", tutorInputWidgetStats=" + tutorInputWidgetStats + "]";
    }
}