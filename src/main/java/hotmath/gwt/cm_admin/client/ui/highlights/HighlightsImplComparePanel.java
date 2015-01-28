package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.event.EnableEvent;
import com.sencha.gxt.widget.core.client.event.EnableEvent.EnableHandler;

public class HighlightsImplComparePanel extends ContentPanel {
	
    AbstractHighlightsImpl base;
    
    HighlightsGetReportAction.ReportType reportType;
    
    public HighlightsImplComparePanel(AbstractHighlightsImpl base) {
        this.base = base;
        
        super.addEnableHandler(new EnableHandler() {

			@Override
			public void onEnable(EnableEvent event) {
		        add(new Label("Loading data from server ..."));
		        getDataFromServer();
			}
        	
        });
    }
    
    protected void getDataFromServer() {
        new RetryAction<CmList<HighlightReportData>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                switch (base.getType()) {
                case NATIONWIDE_COMPARE:
                	reportType = HighlightsGetReportAction.ReportType.NATIONWIDE_COMPARE;
                	break;
                case SCHOOL_COMPARE:
                	reportType = HighlightsGetReportAction.ReportType.SCHOOL_COMPARE;
                	break;
                default:
                	reportType = null;
                }
                HighlightsGetReportAction action = new HighlightsGetReportAction(
                		StudentGridPanel.instance.getPageAction(),
                		reportType, 
                        StudentGridPanel.instance.getCmAdminMdl().getUid(), 
                        DateRangePanel.getInstance().getFromDate(),
                        DateRangePanel.getInstance().getToDate()
                        );
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<HighlightReportData> data) {
                drawTable(data);
                CmBusyManager.setBusy(false);                
            }
        }.register();        
    }
    
    private void drawTable(CmList<HighlightReportData> data) {
    	super.clear();
         add(new HTML("<h1>data read from server!</h1>"));
         
         for(int i=0,t=data.size();i<t;i++) {
             add(new Label(data.get(i).getData()));
         }
         super.doLayout();
    }
}