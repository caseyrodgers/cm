package hotmath.gwt.cm_admin.client.ui;

import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

public class HighlightImplMostFailuresLatestQuizDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplMostFailuresLatestQuizDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.FAILED_CURRENT_QUIZZES;
    }
    
    @Override
    protected ColumnModel getColumns() {
        // TODO Auto-generated method stub
        ColumnModel cm = super.getColumns();
        cm.getColumn(1).setHeader("Failed Last Quiz Count");
        return cm;
    }
   
}
