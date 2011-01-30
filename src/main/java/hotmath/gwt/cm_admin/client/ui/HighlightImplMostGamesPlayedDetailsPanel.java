package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

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
    protected List<ColumnConfig> getColumns() {
        List<ColumnConfig> cm = super.getColumns();
        cm.get(1).setHeader("Games Played");

        ColumnConfig column = new ColumnConfig();
        column.setId("quizzesTaken");
        column.setHeader("Quizzes Taken");
        column.setWidth(100);
        column.setSortable(false);
        cm.add(column);
            
        return cm;
    }
}
