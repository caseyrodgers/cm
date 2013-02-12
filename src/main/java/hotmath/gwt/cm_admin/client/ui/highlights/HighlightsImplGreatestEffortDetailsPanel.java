package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_admin.client.ui.highlights.HighlightsImplBase;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

public class HighlightsImplGreatestEffortDetailsPanel extends HighlightsImplDetailsPanelBase {
    
    public HighlightsImplGreatestEffortDetailsPanel(HighlightsImplBase base) {
        super(base);
    }


    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.GREATEST_EFFORT;
    }

}