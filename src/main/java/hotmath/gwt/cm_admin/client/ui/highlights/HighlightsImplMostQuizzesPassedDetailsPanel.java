package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

public class HighlightsImplMostQuizzesPassedDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplMostQuizzesPassedDetailsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.MOST_QUIZZES_PASSED;
    }
    
    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	List<ColumnConfig<HighlightReportData, ?>> cols = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

    	ColumnConfig<HighlightReportData, ?> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 140, "Students with one or more logins");
    	column.setWidth(300);
    	column.setSortable(false);
    	cols.add(column);

    	column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.quizzesPassed(), 140, "Quizzes Passed");
    	column.setWidth(130);
    	column.setSortable(false);
    	cols.add(column);

    	return new ColumnModel<HighlightReportData>(cols);

    }
    
    
    @Override
    public String[] getReportColumns() {
        return new String[] {"Name", "Quizzes Passed"};
    }
    
    @Override
    public String[][] getReportValues() {
        CmList<HighlightReportData> hd = getHighLightData();
        String[][] vals = new String[hd.size()][2];
        for(int i=0;i<hd.size();i++) {
            vals[i][0] = hd.get(i).getName();
            vals[i][1] = hd.get(i).getQuizzesPassed() + "";
        }
        return vals;
    }
   
}
