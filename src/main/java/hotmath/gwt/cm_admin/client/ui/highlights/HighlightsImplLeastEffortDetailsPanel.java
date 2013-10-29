package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

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
    public String[] getReportColumns() {
        return new String[]{"Name", "Lessons"};
    }

    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.LEAST_EFFORT;
    }

    @Override
    public String[][] getReportValues() {
        List<HighlightReportData> reportData = getHighLightData();
        
        String vars[][] = new String[reportData.size()][2];
        for(int i=0; i < reportData.size();i++) {
            vars[i][0] = reportData.get(i).getName();
            vars[i][1] = reportData.get(i).getLessonsViewed() + "";
        }
        return vars;
    }

}
