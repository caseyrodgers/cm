package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

public class HighlightsImplTimeOnTaskPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplTimeOnTaskPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.TIME_ON_TASK;
    }
    
    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	ColumnModel<HighlightReportData> columnModel = super.getColumns();
    	
    	List<ColumnConfig<HighlightReportData, ?>> columns = columnModel.getColumns();
    	columns.get(1).setHeader("Time-on-Task");
        
        return new ColumnModel<HighlightReportData>(columns);
    }

}
