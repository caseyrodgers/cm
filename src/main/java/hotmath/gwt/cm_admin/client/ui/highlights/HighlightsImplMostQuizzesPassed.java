package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplMostQuizzesPassed extends HighlightsImplBase {
	    HighlightsImplMostQuizzesPassedDetailsPanel panel = new HighlightsImplMostQuizzesPassedDetailsPanel(this);
	    static String title = "Most Quizzes Passed";
	    
	    public HighlightsImplMostQuizzesPassed() {
	        super(title,"Displays students in order of most quizzes passed (which correlates to most Sections completed as Auto-Enroll quizzes are not counted).");
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
}
