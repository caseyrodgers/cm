package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Element;

public class HighlightImplComparePanel extends LayoutContainer {
	
    AbstractHighlightImpl base;
    
    HighlightsGetReportAction.ReportType reportType;
    
    public HighlightImplComparePanel(AbstractHighlightImpl base) {
        this.base = base;
    }
    
    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        
        add(new Label("Loading data from server ..."));
        getDataFromServer();
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
                }
                HighlightsGetReportAction action = new HighlightsGetReportAction(reportType, 
                        StudentGridPanel.instance._cmAdminMdl.getId(), 
                        HighlightsDataWindow._from,
                        HighlightsDataWindow._to
                        );
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<HighlightReportData> data) {
                drawTable(data);
                CmBusyManager.setBusy(false);                
            }
        }.register();        
    }
    
    private void drawTable(CmList<HighlightReportData> data) {
         removeAll();
         add(new Html("<h1>data read from server!</h1>"));
         
         for(int i=0,t=data.size();i<t;i++) {
             add(new Label(data.get(i).getData()));
         }
         layout();
    }
}