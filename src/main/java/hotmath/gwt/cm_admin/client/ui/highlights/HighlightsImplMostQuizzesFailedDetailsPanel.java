package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class HighlightImplMostQuizzesFailedDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplMostQuizzesFailedDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.FAILED_QUIZZES;
    }
    
    @Override
    protected List<ColumnConfig> getColumns() {
        List<ColumnConfig> configs = super.getColumns();
        configs.get(1).setHeader("Failed Quizzes");
        return configs;
    }
   
}
