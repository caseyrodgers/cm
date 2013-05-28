package hotmath.gwt.cm_tools.client.ui.ccss;

import com.google.gwt.user.client.ui.Widget;

public class CCSSCoverageImplStudent extends CCSSCoverageImplBase {

    CCSSCoverageImplStudentQuizzedPassedPanel panel = new CCSSCoverageImplStudentQuizzedPassedPanel(this);

    static String title = "CCSS Coverage for Student";

    public CCSSCoverageImplStudent() {
        super(title, "Displays CCSS coverage for a student", 0);
    }
    
    public Widget prepareWidget() {
        return panel;
    }

    @Override
    protected String[][] getReportValues() {
        return panel.getPanelValues();
    }
    
    @Override
    String[] getReportCols() {
        return panel.getPanelColumns();
    }
}
