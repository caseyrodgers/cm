package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

public class HighlightsImplFirstAnswersPercentPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplFirstAnswersPercentPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.FIRST_TIME_CORRECT;
    }
    
    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	List<ColumnConfig<HighlightReportData, ?>> cols = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

    	ColumnConfig<HighlightReportData, ?> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 140, "Students with one or more logins");
    	column.setWidth(300);
    	column.setSortable(false);
    	cols.add(column);

        column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.firstTimeCorrect(), 140, "% Correct");
    	column.setWidth(130);
    	column.setSortable(false);
    	cols.add(column);

    	return new ColumnModel<HighlightReportData>(cols);

    }
    
    @Override
    public String getReportTitle() {
        return "First Answer Correct Percentage";
    }

    @Override
    public String[] getReportColumns() {
        return new String[]{"Name", "% Correct"};
    }

    @Override
    public String[][] getReportValues() {
        List<HighlightReportData> reportData = getHighLightData();
        
        /** in reverse order */
        String vars[][] = new String[reportData.size()][2];
        for(int i=0;i<reportData.size(); i++) {
            vars[i][0] = reportData.get(i).getName();
            vars[i][1] = reportData.get(i).getFirstTimeCorrectPercent() + "";
        }
        return vars;
        
    }
    
    
}
