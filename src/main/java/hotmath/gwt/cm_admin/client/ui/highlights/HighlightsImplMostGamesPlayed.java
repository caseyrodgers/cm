package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

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
    String[] getReportCols() {
        return panel.getReportColumns();
    }
    
    @Override
    String[][] getReportValues() {
        return panel.getReportValues();
    }  
}