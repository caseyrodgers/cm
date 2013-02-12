package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

public class HighlightsImplHighestAverageQuizScoresDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplHighestAverageQuizScoresDetailsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.AVERAGE_QUIZ_SCORES;
    }

    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	ColumnModel<HighlightReportData> columnModel = super.getColumns();
    	
    	List<ColumnConfig<HighlightReportData, ?>> columns = columnModel.getColumns();
    	columns.get(1).setHeader("Average");

        ColumnConfig<HighlightReportData, Integer> column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.quizzesTaken());
        column.setHeader("Quizzes Taken");
        column.setWidth(100);
        column.setSortable(false);
        columns.add(column);
                    
        return new ColumnModel<HighlightReportData>(columns);

    }

/*    
    protected HighlightsReportModel createTableModel(HighlightReportData data) {
        return new HighlightsReportModel(data.getUid(), data.getName(), data.getData(),data.getQuizzesTaken());
    }
*/    
   
}
