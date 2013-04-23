package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

public class HighlightsImplHighestAverageQuizScoresDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplHighestAverageQuizScoresDetailsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.AVERAGE_QUIZ_SCORES;
    }

    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	List<ColumnConfig<HighlightReportData, ?>> cols = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

    	ColumnConfig<HighlightReportData, ?> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 140, "Students with one or more logins");
    	column.setWidth(300);
    	column.setSortable(false);
    	cols.add(column);

        column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.quizAverage(), 140, "Average");
    	column.setWidth(130);
    	column.setSortable(false);
    	cols.add(column);

    	column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.quizzesTaken());
    	column.setHeader("Quizzes Taken");
    	column.setWidth(100);
    	column.setSortable(false);
    	cols.add(column);

    	return new ColumnModel<HighlightReportData>(cols);

    }
    
    @Override
    public String[] getReportColumns() {
        return new String[] {"Name", "Average", "Quizzes Taken"};
    }
    
    @Override
    public String[][] getReportValues() {
        CmList<HighlightReportData> hd = getHighLightData();
        String[][] vals = new String[hd.size()][3];
        for(int i=0;i<hd.size();i++) {
            vals[i][0] = hd.get(i).getName();
            vals[i][1] = hd.get(i).getQuizAverage() + "";
            vals[i][2] = hd.get(i).getQuizzesTaken() + "";
        }
        return vals;
    }
   
}
