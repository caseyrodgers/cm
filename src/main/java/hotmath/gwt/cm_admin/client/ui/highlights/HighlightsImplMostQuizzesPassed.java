package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

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
	    protected HighlightReportLayout getReportLayout() {
	        String cols[] = {"Name:75", "Quizzes Passed:25"};
	        HighlightReportLayout rl = new HighlightReportLayout(title, "Student Count: ", cols, panel.getReportValues());
	        return rl;
	    }    
}
