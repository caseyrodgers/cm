package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

public class HighlightsImplMostQuizzesFailedDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplMostQuizzesFailedDetailsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.FAILED_QUIZZES;
    }
    
    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	ColumnModel<HighlightReportData> columnModel = super.getColumns();
    	
    	List<ColumnConfig<HighlightReportData, ?>> columns = columnModel.getColumns();
    	columns.get(1).setHeader("Failed Quizzes");
        
        return new ColumnModel<HighlightReportData>(columns);
    }
}
