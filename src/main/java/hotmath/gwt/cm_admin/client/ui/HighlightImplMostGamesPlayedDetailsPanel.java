package hotmath.gwt.cm_admin.client.ui;

import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

/** 
- Least Effort: 
    Least Effort from <start date> to <end date>
    Ordered listing showing number of lesson topics completed
    Jones, John         0
*/
public class HighlightImplMostGamesPlayedDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplMostGamesPlayedDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.MOST_GAMES;
    }
    
    
    @Override
    protected ColumnModel getColumns() {
        ColumnModel cm = super.getColumns();
        cm.getColumn(1).setHeader("Games Played");
        return cm;
    }
}
