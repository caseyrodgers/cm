package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

public interface HighlightPanel {
    HighlightsGetReportAction.ReportType getReportType();
    void getDataFromServer(CallbackOnComplete callbackOnComplete);
    
    String[] getReportColumns();
    String[][] getReportValues();
}
