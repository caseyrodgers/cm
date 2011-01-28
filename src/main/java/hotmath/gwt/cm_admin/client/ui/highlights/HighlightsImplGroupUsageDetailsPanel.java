package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

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
public class HighlightImplGroupUsageDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplGroupUsageDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.GROUP_USAGE;
    }
    
    @Override
    protected ColumnModel getColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Group");
        column.setWidth(100);
        column.setSortable(false);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("activeCount");
        column.setHeader("Active");
        column.setWidth(75);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.RIGHT);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("videosViewed");
        column.setHeader("Videos");
        column.setWidth(75);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.RIGHT);
        configs.add(column);
        
        column = new ColumnConfig();
        column.setId("gamesViewed");
        column.setHeader("Games Viewed");
        column.setWidth(75);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.RIGHT);        
        configs.add(column);
        
        column = new ColumnConfig();
        column.setId("activitiesViewed");
        column.setHeader("Activities");
        column.setWidth(75);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.RIGHT);        
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }
    
    protected HighlightReportModel createTableModel(HighlightReportData data) {
        return new HighlightReportModel(data.getName(),data.getActiveCount(), data.getVideosViewed(), data.getGamesViewed(), data.getActivitiesViewed(), data.getFlashCardsViewed());
    }
    
    @Override
    protected void showSelectStudentDetail() {
    }
}
