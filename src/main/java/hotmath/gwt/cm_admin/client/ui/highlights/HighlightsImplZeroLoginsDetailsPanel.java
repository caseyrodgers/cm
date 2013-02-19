package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
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
    	List<ColumnConfig<HighlightReportData, ?>> cols = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

    	ColumnConfig<HighlightReportData, ?> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 140, "Students with zero logins");
    	column.setWidth(300);
    	column.setSortable(false);
    	cols.add(column);

    	return new ColumnModel<HighlightReportData>(cols);

    }

/*    
    @Override
    protected HighlightsReportModel createTableModel(HighlightReportData data) {
        return new HighlightsReportModel(data.getUid(), data.getName(), null);
    }
*/   
}
