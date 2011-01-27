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
public class HighlightImplGroupProgressDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplGroupProgressDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.GROUP_PERFORMANCE;
    }
    
    @Override
    protected HighlightReportModel createTableModel(HighlightReportData data) {
        return new HighlightReportModel(data.getName(),data.getActiveCount(),data.getLoginCount(),data.getLessonsViewed(),data.getQuizzesPassed(),data.getSchoolQuizzesPassed());                
    }
    
    @Override
    protected ColumnModel getColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Group Name");
        column.setWidth(150);
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
        column.setId("loginCount");
        column.setHeader("Login");
        column.setWidth(75);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.RIGHT);
        configs.add(column);
        
        column = new ColumnConfig();
        column.setId("lessonsViewed");
        column.setHeader("Lessons Viewed");
        column.setWidth(75);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.RIGHT);        
        configs.add(column);
        
        column = new ColumnConfig();
        column.setId("quizzesPassed");
        column.setHeader("Quizzes Passed");
        column.setWidth(75);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.RIGHT);        
        configs.add(column);
        
        column = new ColumnConfig();
        column.setId("schoolQuizzesPassed");
        column.setHeader("Quizzes Passed (school)");
        column.setWidth(75);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.RIGHT);        
        configs.add(column);        

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }
    
    @Override
    protected void showSelectStudentDetail() {
    }
  
}
