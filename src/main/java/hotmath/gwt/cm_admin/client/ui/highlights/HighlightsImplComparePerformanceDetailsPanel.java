package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

/**
 * 
 *  
 *                           Group  School   District   Nationwide
Sections Passed     3.2     5.2       4.5            3.6
Avg. Quiz Score      63%   52%    31%          75%
Avg Logins/week      2.1    2.2       3.2            2.7
Games Played        3.2     5.2       4.5            3.6
Videos Watched     3.2     5.2       4.5            3.6
Flashcards used      3.2     5.2       4.5            3.6


 * @author casey
 *
 */
public class HighlightsImplComparePerformanceDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplComparePerformanceDetailsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.COMPARE_PERFORMANCE;
    }

    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
        List<ColumnConfig<HighlightReportData, ?>> configs = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

        ColumnConfig<HighlightReportData, String> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 100, "");
        column.setSortable(false);
/*        
        column.setRenderer(new GridCellRenderer<ModelData>() {
            public Object render(ModelData model, String property, com.extjs.gxt.ui.client.widget.grid.ColumnData config, int rowIndex, int colIndex, com.extjs.gxt.ui.client.store.ListStore<ModelData> store, com.extjs.gxt.ui.client.widget.grid.Grid<ModelData> grid) {
                String msg = (String)model.get("name");
                Html html = new Html("<h2 style='font-size: 1.2em;'>" + msg + "</h2>");
                html.setToolTip("Data associated with '" + msg);
                return html;
            }
        });
*/
        configs.add(column);

        ColumnConfig<HighlightReportData, Integer> col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.group(), 75, "Group");
        col.setSortable(false);
        //col.setAlignment(HorizontalAlignment.RIGHT);
        configs.add(col);

        col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.school(), 75, "School");
        col.setSortable(false);
        //col.setAlignment(HorizontalAlignment.RIGHT);
        configs.add(col);
        
        col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.national(), 75, "Nation wide");
        col.setSortable(false);
        //column.setAlignment(HorizontalAlignment.RIGHT);        
        configs.add(col);
        
        return new ColumnModel<HighlightReportData>(configs);
    }

    @Override
    protected void showSelectedStudentDetail() {
    }
  
}
