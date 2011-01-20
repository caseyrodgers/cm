package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

/** 
- Least Effort: 
    Least Effort from <start date> to <end date>
    Ordered listing showing number of lesson topics completed
    Jones, John         0
*/
public class HighlightImplLeastEffortDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplLeastEffortDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.LEAST_EFFORT;
    }
   
    
}
