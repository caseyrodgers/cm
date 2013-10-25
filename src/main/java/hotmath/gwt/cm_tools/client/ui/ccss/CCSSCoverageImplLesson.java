package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplLesson extends CCSSCoverageImplBase {
    static String title = "Lesson";
    CCSSCoverageImplLessonPanel panel;
    public CCSSCoverageImplLesson(String lessonFile, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage for a Lesson.", 0, callback);
        panel = new CCSSCoverageImplLessonPanel(this, lessonFile);
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