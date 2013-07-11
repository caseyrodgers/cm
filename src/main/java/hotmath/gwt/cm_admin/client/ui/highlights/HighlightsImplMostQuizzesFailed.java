package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplMostQuizzesFailed extends HighlightsImplBase {
    HighlightsImplMostQuizzesFailedDetailsPanel panel = new HighlightsImplMostQuizzesFailedDetailsPanel(this);
    static String title = "Most Quizzes Failed";
    public HighlightsImplMostQuizzesFailed() {
        super(title,"Displays students in order of most quizzes failed, a possible indicator of students in a program above their level.");
    }
    public Widget prepareWidget() {
        return panel;
    }
    
    @Override
    String[] getReportCols() {
        return panel.getReportColumns();
    }
    
    @Override
    String[][] getReportValues() {
        return panel.getReportValues();
    }

    @Override
    protected HighlightReportLayout getReportLayout() {
    	HighlightReportLayout rl = super.getReportLayout();
    	rl.setTitle(title);
    	return rl;
    }
    
}
