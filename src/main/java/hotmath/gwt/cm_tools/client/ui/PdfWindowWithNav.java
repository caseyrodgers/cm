package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.Window;

public class PdfWindowWithNav {

    Action<CmWebResource> action;
    Integer aid;
    public PdfWindowWithNav(Integer aid,  String title, Action<CmWebResource> action) {
        this.action = action;
        this.aid = aid;
        createPdfRpc();
    }

    private void createPdfRpc() {
        new RetryAction<CmWebResource>() {
            @Override
            public void attempt() {
                CatchupMathTools.setBusy(true);
                CmServiceAsync s = CmRpcCore.getCmService();
                setAction(action);
                s.execute(action,this);                
            }
            @Override
            public void oncapture(CmWebResource webResource) {
                CatchupMathTools.setBusy(false);
                
                /** Was blanking out screen in IE 
                 * 
                 */
                String features = "resizable=1,scrollbars=1,status=1,toolbar=1,height=900,width=700";
                Window.open(webResource.getUrl(), "_blank", features);
            }
        }.register();
    }
}
