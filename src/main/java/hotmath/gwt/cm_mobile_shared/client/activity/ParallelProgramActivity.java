package hotmath.gwt.cm_mobile_shared.client.activity;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_mobile_shared.client.view.ParallelProgramView;
import hotmath.gwt.cm_rpc.client.rpc.ParallelProgramLoginAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ParallelProgramActivity implements ParallelProgramView.Presenter {

    private int uid;

    public ParallelProgramActivity(int uid) {
        this.uid = uid;
    }

    @Override
    public void doLogin(final ParallelProgramView view, final String password) {
        
        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
        
        ParallelProgramLoginAction action = new ParallelProgramLoginAction(this.uid, password);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData rdata) {
                
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));

                /**
                 * check for error message
                 * 
                 * @TODO: move to specific object away from RpcData
                 * 
                 *        if error_message is non-null, then a programmatic
                 *        (local domain) error occurred. As opposed to a lower
                 *        level exception that would be caught by the generic
                 *        error handlers.
                 * 
                 */
                String errorMessage = rdata.getDataAsString("error_message");
                if (errorMessage != null && errorMessage.length() > 0) {
                    if (errorMessage.indexOf("is not available") > -1) {
                        view.showParallelProgNotAvail(password);
                    }
                } else {
                    // complete login
                    String key = rdata.getDataAsString("key");
                    completeLogin(key);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                Log.error(caught.getMessage(), caught);
                String msg = caught.getMessage();
                MessageBox.showError("There was a problem logging into the Parallel Program: " + msg);
            }
        });        
        
    }

    private void completeLogin(final String key) {
        String url = "http://" + Window.Location.getHost();
        url += "/loginService?key=" + key;
        Window.Location.replace(url);
    }
}
