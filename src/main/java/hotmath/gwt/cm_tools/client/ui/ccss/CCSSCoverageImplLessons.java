package hotmath.gwt.cm_tools.client.ui.ccss;

import java.util.List;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.LessonModel;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplLessons extends CCSSCoverageImplBase {
    static String title = "Lessons";
    CCSSCoverageImplLessonsPanel panel;
    public CCSSCoverageImplLessons(List<LessonModel> lessons, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage for one or more Lessons.", 0, callback);
        panel = new CCSSCoverageImplLessonsPanel(this, lessons);
    }

    public Widget prepareWidget() {
        return panel;
    }
    
    @Override
    String[] getReportCols() {
        return panel.getPanelColumns();
    }
    
    @Override
    String[][] getReportValues() {
        return panel.getPanelValues();
    }  
}