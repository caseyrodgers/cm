package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.Composite;



public class HighlightsImplCCSSGroupCoveragePanel extends Composite implements HighlightPanel {
    
    HighlightsImplBase base;
    public HighlightsImplCCSSGroupCoveragePanel(HighlightsImplBase base) {
        this.base = base;
        initWidget(new HTML("THIS IS THE CHART"));
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.CCSS_GROUP_COVERAGE;
    }

    @Override
    public void getDataFromServer(CallbackOnComplete callbackOnComplete) {
        /** Make RPC call here */
        callbackOnComplete.isComplete();
    }

    @Override
    public String[] getReportColumns() {
        return null;
    }

    @Override
    public String[][] getReportValues() {
        return null;
    }
}
