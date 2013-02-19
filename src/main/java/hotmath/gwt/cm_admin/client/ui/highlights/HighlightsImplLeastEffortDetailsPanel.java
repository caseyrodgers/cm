package hotmath.gwt.cm_admin.client.ui.highlights;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

/**
 * - Least Effort: Least Effort from <start date> to <end date> Ordered listing
 * showing number of lesson topics completed Jones, John 0
 */
public class HighlightsImplLeastEffortDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplLeastEffortDetailsPanel(HighlightsImplBase base) {
        super(base);
    }

    @Override
    protected ColumnModel<HighlightReportData> getColumns() { 
        List<ColumnConfig<HighlightReportData, ?>> cols = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

        ColumnConfig<HighlightReportData, String> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 140, "Students with one or more logins");
        column.setWidth(300);
        column.setSortable(false);
        cols.add(column);

        ColumnConfig<HighlightReportData, Integer> col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.lessons(), 140, "Lessons Viewed");
        col.setWidth(130);
        col.setSortable(false);
        cols.add(col);
        
        return new ColumnModel<HighlightReportData>(cols);
    }

    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.LEAST_EFFORT;
    }

}
