package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

/**
 * - Least Effort: Least Effort from <start date> to <end date> Ordered listing
 * showing number of lesson topics completed Jones, John 0
 */
public class HighlightsImplLeastEffortDetailsPanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplLeastEffortDetailsPanel(HighlightsImplBase base) {
        super(base);
    }

    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.LEAST_EFFORT;
    }

}
