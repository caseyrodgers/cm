package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_admin.client.ui.highlights.HighlightsImplBase;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

/** 
 * 
 * Group Report #1

    •  Name: Group Progress
    •  Tooltip: Shows number of active students (logged in at least once), total logins, lessons viewed, and quizzes passed for each group and enbtire school. Groups with no active students are omitted.
    •  Sample report

     Group name  Active Logins Lessons Passed
     7th-Graders <n> <n> <n> <n>
     8th-Graders <n> <n> <n> <n>
     SCHOOLWIDE <n> <n> <n> <n>
     
 * 
 * @author casey
 *
 */
public class HighlightsImplGroupProgressDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplGroupProgressDetailsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.GROUP_PERFORMANCE;
    }
    
    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
        List<ColumnConfig<HighlightReportData, ?>> configs = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

        ColumnConfig<HighlightReportData, String> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 150, "Group");
        column.setSortable(false);
        configs.add(column);

        ColumnConfig<HighlightReportData, Integer> col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.active(), 75, "Active");
        col.setSortable(false);
        //col.setToolTip("Count of active users");
        //col.setAlignment(HorizontalAlignment.RIGHT);
        configs.add(col);

        col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.logins(), 75, "Logins");
        col.setSortable(false);
        //col.setToolTip("Number of logins");
        //coln.setAlignment(HorizontalAlignment.RIGHT);
        configs.add(col);
        
        col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.lessons(), 75, "Lessons");
        col.setSortable(false);
        //col.setToolTip("Lessons viewed");
        //col.setAlignment(HorizontalAlignment.RIGHT);        
        configs.add(col);
        
        col = new ColumnConfig<HighlightReportData, Integer>(_gridProps.quizzesPassed(), 75, "Passed");
        col.setSortable(false);
        //col.setToolTip("Quizzes passed");
        //col.setAlignment(HorizontalAlignment.RIGHT);        
        configs.add(col);
        
        return new ColumnModel<HighlightReportData>(configs);
    }
    
//    @Override
//    protected HighlightsReportModel createTableModel(HighlightReportData data) {
//        return new HighlightsReportModelGroupReport(data.getName(),data.getActiveCount(),data.getLoginCount(),data.getLessonsViewed(),data.getQuizzesPassed());                
//    }

    @Override
    protected void showSelectStudentDetail() {
    }
    
    @Override
    protected String getGridToolTip() {
        return null;
    }
  
}
