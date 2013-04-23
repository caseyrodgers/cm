package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

/**
 * 
Report #2

    •  Name: Group Usage
    •  Tooltip: Shows the usage of optional learning resources for groups with at least one active student.
    •  Sample report
    Group name  Active Videos Games Activities Flash Cards
    7th-Graders <n> <n> <n> <n> <n>
    8th-Graders <n> <n> <n> <n> <n>
    SCHOOLWIDE <n> <n> <n> <n> <n>
 *
 */
public class HighlightsImplGroupUsageDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplGroupUsageDetailsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.GROUP_USAGE;
    }
    
    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
        List<ColumnConfig<HighlightReportData, ?>> configs = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

        ColumnConfig<HighlightReportData, String> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 100, "Group");
        column.setSortable(false);
        configs.add(column);

        ColumnConfig<HighlightReportData, Integer> col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.active(), 75, "Active");
        col.setSortable(false);
        //column.setAlignment(HorizontalAlignment.RIGHT);
        configs.add(col);

        col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.videos(), 75, "Videos");
        col.setSortable(false);
        //col.setAlignment(HorizontalAlignment.RIGHT);
        configs.add(col);
        
        col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.games(), 75, "Games");
        col.setSortable(false);
        //col.setAlignment(HorizontalAlignment.RIGHT);        
        configs.add(col);
        
        col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.activities(), 75, "Activities");
        col.setSortable(false);
        //col.setAlignment(HorizontalAlignment.RIGHT);        
        configs.add(col);
        
        return new ColumnModel<HighlightReportData>(configs);
    }
    
    
    @Override
    public String[] getReportColumns() {
        return new String[]{"Group", "Active", "Videos", "Games", "Activities"};
    }
    
    @Override
    public String[][] getReportValues() {
        CmList<HighlightReportData> hd = getHighLightData();
        String[][] vals = new String[hd.size()][5];
        for(int i=0;i<hd.size();i++) {
            vals[i][0] = hd.get(i).getName();
            vals[i][1] = hd.get(i).getName();
            vals[i][2] = hd.get(i).getName();
            vals[i][3] = hd.get(i).getName();
            vals[i][4] = hd.get(i).getName();
        }
        return vals;
    }
 
    @Override
    protected void showSelectedStudentDetail() {
    }
    
    @Override
    protected String getGridToolTip() {
        return null;
    }

}
