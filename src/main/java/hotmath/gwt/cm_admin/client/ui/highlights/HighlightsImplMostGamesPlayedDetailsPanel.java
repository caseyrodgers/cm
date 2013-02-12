package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

/** 
- Least Effort: 
    Least Effort from <start date> to <end date>
    Ordered listing showing number of lesson topics completed
    Jones, John         0
*/
public class HighlightsImplMostGamesPlayedDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplMostGamesPlayedDetailsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.MOST_GAMES;
    }
    
    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	ColumnModel<HighlightReportData> columnModel = super.getColumns();
    	
    	List<ColumnConfig<HighlightReportData, ?>> columns = columnModel.getColumns();
    	columns.get(1).setHeader("Games Played");

        ColumnConfig<HighlightReportData, Integer> column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.quizzesTaken());
        column.setHeader("Quizzes Taken");
        column.setWidth(100);
        column.setSortable(false);
        columns.add(column);

        return new ColumnModel<HighlightReportData>(columns);
    }
    
}
