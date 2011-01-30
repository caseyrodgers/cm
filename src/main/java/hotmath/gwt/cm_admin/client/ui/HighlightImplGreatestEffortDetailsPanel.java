package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class HighlightImplGreatestEffortDetailsPanel extends HighlightImplDetailsPanelBase {
    
    public HighlightImplGreatestEffortDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    protected List<ColumnConfig> getColumns() {
        List<ColumnConfig> configs = super.getColumns();


        ColumnConfig column = new ColumnConfig();
        column.setId("quizzesTaken");
        column.setHeader("Quizzes Taken");
        column.setWidth(100);
        column.setSortable(false);
        configs.add(column);

        return configs;
    }

    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.GREATEST_EFFORT;
    }

}