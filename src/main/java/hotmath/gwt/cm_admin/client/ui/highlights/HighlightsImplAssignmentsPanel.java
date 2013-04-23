package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

public class HighlightsImplAssignmentsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplAssignmentsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.ASSIGNMENTS;
    }
    
    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	List<ColumnConfig<HighlightReportData, ?>> cols = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

    	ColumnConfig<HighlightReportData, ?> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 140, "Students with one or more assignments");
    	column.setWidth(300);
    	column.setSortable(false);
    	cols.add(column);

        column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.assignmentCount(), 140, "# of Assignments");
    	column.setWidth(120);
    	column.setSortable(false);
    	cols.add(column);

        column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.assignmentAverage(), 140, "Average Score");
    	column.setWidth(110);
    	column.setSortable(false);
    	cols.add(column);
    	
    	return new ColumnModel<HighlightReportData>(cols);

    }
    
    @Override
    public String[] getReportColumns() {
        return new String[] {"Name", "Assignment Count", "Average Score"};
    }
    
    @Override
    public String[][] getReportValues() {
        CmList<HighlightReportData> hd = getHighLightData();
        String[][] vals = new String[hd.size()][3];
        for(int i=0;i<hd.size();i++) {
            vals[i][0] = hd.get(i).getName();
            vals[i][1] = hd.get(i).getAssignmentCount() + "";
            vals[i][2] = hd.get(i).getAssignmentAverage() +"%";
        }
        return vals;
    }
    
}
