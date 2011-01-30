package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

public class HighlightImplGreatestEffortDetailsPanel extends HighlightImplDetailsPanelBase {
    
    public HighlightImplGreatestEffortDetailsPanel(HighlightImplBase base) {
        super(base);
    }


    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.GREATEST_EFFORT;
    }

}