package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplCustomProgram extends CCSSCoverageImplBase {
    static String title = "Custom Program";
    CCSSCoverageImplCustomProgramPanel panel;
    public CCSSCoverageImplCustomProgram(int assignKey, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage for a Custom Program.", assignKey, callback);
        panel = new CCSSCoverageImplCustomProgramPanel(this, assignKey);
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