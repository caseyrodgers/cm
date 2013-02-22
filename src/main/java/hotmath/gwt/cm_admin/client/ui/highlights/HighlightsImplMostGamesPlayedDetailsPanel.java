package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

/** 
- Most Games Played: 
    Most Games from <start date> to <end date>
    Ordered listing showing number of lesson topics completed
    Jones, John         0
*/
public class HighlightsImplMostGamesPlayedDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplMostGamesPlayedDetailsPanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.MOST_GAMES;
    }

    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	List<ColumnConfig<HighlightReportData, ?>> cols = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

    	ColumnConfig<HighlightReportData, ?> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 140, "Students with one or more logins");
    	column.setWidth(300);
    	column.setSortable(false);
    	cols.add(column);

    	column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.games(), 140, "Games Played");
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
        return new String[] {"Name", "Games Played", "Quizzes Taken"};
    }
    
    @Override
    public String[][] getReportValues() {
        List<HighlightReportData> reportData = getHighLightData();
        String vars[][] = new String[reportData.size()][3];
        for(int i=0; i < reportData.size();i++) {
            vars[i][0] = reportData.get(i).getName();
            vars[i][1] = reportData.get(i).getGamesViewed() + "";
            vars[i][2] = reportData.get(i).getQuizzesTaken() + "";
        }
        return vars;
    }
}
