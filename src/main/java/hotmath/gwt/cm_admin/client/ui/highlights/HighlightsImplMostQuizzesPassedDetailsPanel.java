package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

public class HighlightsImplMostQuizzesPassedDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplMostQuizzesPassedDetailsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.MOST_QUIZZES_PASSED;
    }
    
    
    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	ColumnModel<HighlightReportData> columnModel = super.getColumns();
    	
    	List<ColumnConfig<HighlightReportData, ?>> columns = columnModel.getColumns();
    	columns.get(1).setHeader("Quizzes Passed");
        
        return new ColumnModel<HighlightReportData>(columns);
    }
   
}
