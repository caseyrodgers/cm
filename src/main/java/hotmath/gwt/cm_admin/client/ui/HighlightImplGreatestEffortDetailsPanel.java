package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

public class HighlightImplGreatestEffortDetailsPanel extends HighlightImplDetailsPanelBase {
    
    public HighlightImplGreatestEffortDetailsPanel(HighlightImplBase base) {
        super(base);
    }

    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.GREATEST_EFFORT;
    }

}