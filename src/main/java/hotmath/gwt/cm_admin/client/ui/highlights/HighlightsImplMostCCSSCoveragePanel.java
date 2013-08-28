package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

public class HighlightsImplMostCCSSCoveragePanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplMostCCSSCoveragePanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.MOST_CCSS_COVERAGE;
    }
    
    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	List<ColumnConfig<HighlightReportData, ?>> cols = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

    	ColumnConfig<HighlightReportData, ?> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 140, "Student");
    	column.setWidth(230);
    	//column.setSortable(false);
    	cols.add(column);

        column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.assignmentCount(), 140, "Assignments");
    	column.setWidth(75);
    	//column.setSortable(false);
    	cols.add(column);

        column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.lessonCount(), 140, "Lessons");
    	column.setWidth(75);
    	//column.setSortable(false);
    	cols.add(column);
    	
        column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.quizCount(), 140, "Quizzes");
    	column.setWidth(75);
    	//column.setSortable(false);
    	cols.add(column);

        column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.total(), 140, "Total");
    	column.setWidth(75);
    	//column.setSortable(false);
    	cols.add(column);
    	
    	return new ColumnModel<HighlightReportData>(cols);

    }
    
    @Override
    public String[] getReportColumns() {
        return new String[] {"Name", "Assignments", "Lessons", "Quizzes", "Total"};
    }
    
    @Override
    public String[][] getReportValues() {
        CmList<HighlightReportData> hd = getHighLightData();
        String[][] vals = new String[hd.size()][5];
        for(int i=0;i<hd.size();i++) {
            vals[i][0] = hd.get(i).getName();
            vals[i][1] = String.valueOf(hd.get(i).getAssignmentCount());
            vals[i][2] = String.valueOf(hd.get(i).getLessonCount());
            vals[i][3] = String.valueOf(hd.get(i).getQuizCount());
            vals[i][4] = String.valueOf(hd.get(i).getDbCount());
        }
        return vals;
    }
    
}
