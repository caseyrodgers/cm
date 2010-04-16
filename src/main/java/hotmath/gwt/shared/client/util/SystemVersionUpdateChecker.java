package hotmath.gwt.shared.client.util;

import hotmath.gwt.shared.client.CatchupMathVersionInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.action.GetCatchupMathVersionAction;
import hotmath.gwt.shared.client.rpc.result.CatchupMathVersion;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Display a message from central server
 * 
 * @author casey
 *
 */
public class SystemVersionUpdateChecker extends StandardSystemRefreshWindow {

    static final int CHECK_EVERY = 1000 *  60 * 15;
    static SystemVersionUpdateChecker _theWindow;

    public SystemVersionUpdateChecker() {
        super("Catchup Math Update", 
                "We have recently updated Catchup Math.  " + 
                "Please Refresh your browser by pressing the F5 key or click the button below. " +
                "Thank you for using Catchup Math!");
        
        /** only show one */
        if(_theWindow != null)
            return;
        
        setVisible(true);
    }

    /** Monitor server for version changes.
     * 
     * Checks every 15 minutes.
     * 
     */
    static public void monitorVersionChanges() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                checkForUpdate();
            }
        };
        timer.scheduleRepeating(CHECK_EVERY);
        
        /** check now */
        checkForUpdate();
    }
    
    
    /** Call server and look for new messages.
     * 
     * Only call server if window is not currently being displayed.
     */
    static private void checkForUpdate() {
        if(_theWindow != null)
            return;
        
        /** handle as separate request to keep errors silent in case of temp offline
         * 
         */
         GetCatchupMathVersionAction action = new GetCatchupMathVersionAction();
         CmShared.getCmService().execute(action, new AsyncCallback<CatchupMathVersion>() {
             @Override
            public void onSuccess(CatchupMathVersion version) {
                 Log.debug("GetCatchupMathVersionAction: " + version.getVersion() + " current: " + CatchupMathVersionInfo.getBuildVersion());
                 if(version.getVersion() != CatchupMathVersionInfo.getBuildVersion()) {
                     new SystemVersionUpdateChecker();
                 }
            }
             @Override
            public void onFailure(Throwable arg0) {
                 /** fail silent */
                 // CatchupMathTools.showAlert(arg0.getMessage());
            }
        });
    }
}
