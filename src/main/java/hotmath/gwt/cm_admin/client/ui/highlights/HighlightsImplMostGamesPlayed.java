package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

class HighlightsImplMostGamesPlayed extends HighlightsImplBase {
    HighlightsImplMostGamesPlayedDetailsPanel panel = new HighlightsImplMostGamesPlayedDetailsPanel(this);
    static String title = "Most Games Played";
    public HighlightsImplMostGamesPlayed() {
        super(title,"Displays students in order of most games played (excluding those who have played no games).");
    }
    public Widget prepareWidget() {
        return panel;
    }
    
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:50", "Games Played:25", "Quizzes Taken:25"};
        HighlightReportLayout rl = new HighlightReportLayout(title, "Student Count: ", cols,panel.getReportValues());
        return rl;
    }    
}