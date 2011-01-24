package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

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
public class HighlightImplComparePerformanceDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplComparePerformanceDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.COMPARE_PERFORMANCE;
    }
    
    @Override
    protected ColumnModel getColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("");
        column.setWidth(100);
        column.setSortable(false);
        column.setRenderer(new GridCellRenderer<ModelData>() {
            public Object render(ModelData model, String property, com.extjs.gxt.ui.client.widget.grid.ColumnData config, int rowIndex, int colIndex, com.extjs.gxt.ui.client.store.ListStore<ModelData> store, com.extjs.gxt.ui.client.widget.grid.Grid<ModelData> grid) {
                String msg = (String)model.get("name");
                Html html = new Html("<h2 style='font-size: 1.2em;'>" + msg + "</h2>");
                html.setToolTip("Data associated with '" + msg);
                return html;
            }
        });
        configs.add(column);

        column = new ColumnConfig();
        column.setId("group");
        column.setHeader("Group");
        column.setWidth(75);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.RIGHT);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("school");
        column.setHeader("School");
        column.setWidth(75);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.RIGHT);
        configs.add(column);
        
        column = new ColumnConfig();
        column.setId("national");
        column.setHeader("Nation Wide");
        column.setWidth(75);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.RIGHT);        
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }
}
