package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class HighlightImplHighestAverageQuizScoresDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplHighestAverageQuizScoresDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.AVERAGE_QUIZ_SCORES;
    }
    
    @Override
    protected List<ColumnConfig> getColumns() {
        // TODO Auto-generated method stub
        List<ColumnConfig> configs = super.getColumns();
        configs.get(1).setHeader("Average");
        

        ColumnConfig column = new ColumnConfig();
        column.setId("quizzesTaken");
        column.setHeader("Quizzes Taken");
        column.setWidth(100);
        column.setSortable(false);
        configs.add(column);
                    
        return configs;
    }
   
}
