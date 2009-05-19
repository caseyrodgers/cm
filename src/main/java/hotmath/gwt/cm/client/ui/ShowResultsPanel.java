package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.util.UserInfo;
import hotmath.gwt.shared.client.util.RpcData;

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

        CatchupMath.setBusy(true);
        
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
                    CatchupMath.setBusy(false);
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
