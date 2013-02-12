package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.List;

public class HighlightsImplZeroLoginsDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplZeroLoginsDetailsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.ZERO_LOGINS;
    }
    
    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	ColumnModel<HighlightReportData> columnModel = super.getColumns();
    	
    	List<ColumnConfig<HighlightReportData, ?>> columns = columnModel.getColumns();
    	columns.get(0).setHeader("Students with zero logins");
    	columns.get(0).setWidth(500);
        
        return new ColumnModel<HighlightReportData>(columns);
    }

/*    
    @Override
    protected HighlightsReportModel createTableModel(HighlightReportData data) {
        return new HighlightsReportModel(data.getUid(), data.getName(), null);
    }
*/   
}
