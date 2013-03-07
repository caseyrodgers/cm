package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.Window;

public class PdfWindow {

    Action<CmWebResource> action;
    Integer aid;
    public PdfWindow(Integer aid,  String title, Action<CmWebResource> action) {
        this.action = action;
        this.aid = aid;
        createPdfRpc();
    }


    private void createPdfRpc() {
        new RetryAction<CmWebResource>() {
            @Override
            public void attempt() {
                CatchupMathTools.setBusy(true);
                CmServiceAsync s = CmShared.getCmService();
                setAction(action);
                s.execute(action,this);                
            }
            public void oncapture(CmWebResource webResource) {
                CatchupMathTools.setBusy(false);
                
                /** Was blanking out screen in IE 
                 * 
                 */
                String features = "resizable=yes,scrollbars=yes,status=yes,height=900,width=700";
                Window.open(webResource.getUrl(), "CmPDFViewer", features);
            }
        }.register();
    }
}
