package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ShowResultsPanel extends LayoutContainer{
    
    ShowResultsPanel () {
        drawGui();
    }
    
    String _title;
    private void drawGui() {

        CatchupMathTools.setBusy(true);
        
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getQuizResultsHtml(UserInfo.getInstance().getRunId(),new AsyncCallback() {
            public void onSuccess(Object result) {
                try {
                    RpcData rdata = (RpcData)result;
                    String html = rdata.getDataAsString("quiz_html");
                    _title = rdata.getDataAsString("title");

                    add(new Html(html));
                }
                finally {
                    CatchupMathTools.setBusy(false);
                }
            }
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }
        });
    }
    
    
    static public void showWindow() {
        Window w = new Window();
        w.add(new ShowResultsPanel());
        w.setVisible(true);
    }
}
