package hotmath.gwt.cm_admin.client.ui;

import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;


public class HighlightImplMostQuizzesPassedDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplMostQuizzesPassedDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.MOST_QUIZZES_PASSED;
    }
    
    protected ColumnModel getColumns() {
        ColumnModel cm = super.getColumns();
        cm.getColumn(1).setHeader("Quizzes Passed");
        return cm;
    }
   
}
