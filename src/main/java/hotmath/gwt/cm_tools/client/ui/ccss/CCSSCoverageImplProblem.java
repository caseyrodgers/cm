package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplProblem extends CCSSCoverageImplBase {
    static String title = "Lesson";
    CCSSCoverageImplProblemPanel panel;
    public CCSSCoverageImplProblem(String pid, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage for a Lesson.", 0, callback);
        panel = new CCSSCoverageImplProblemPanel(this, pid);
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