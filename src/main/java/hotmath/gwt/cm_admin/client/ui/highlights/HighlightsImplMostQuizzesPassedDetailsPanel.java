package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;


public class HighlightImplMostQuizzesPassedDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplMostQuizzesPassedDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.MOST_QUIZZES_PASSED;
    }
    
    protected List<ColumnConfig> getColumns() {
        List<ColumnConfig> configs = super.getColumns();
        configs.get(1).setHeader("Quizzes Passed");
        return configs;
    }
   
}
