package hotmath.gwt.cm_admin.client.ui;

import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

public class HighlightImplHighestAverageQuizScoresDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplHighestAverageQuizScoresDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.AVERAGE_QUIZ_SCORES;
    }
    
    @Override
    protected ColumnModel getColumns() {
        // TODO Auto-generated method stub
        ColumnModel cm = super.getColumns();
        cm.getColumn(1).setHeader("Average Quiz Score");
        return cm;
    }
   
}
