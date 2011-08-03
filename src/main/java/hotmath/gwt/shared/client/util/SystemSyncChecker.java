package hotmath.gwt.shared.client.util;


import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CatchupMathVersionInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.action.GetUserSyncAction;
import hotmath.gwt.shared.client.rpc.result.CatchupMathVersion;
import hotmath.gwt.shared.client.rpc.result.UserSyncInfo;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Display a message from central server
 * 
 * @author casey
 *
 */
public class SystemSyncChecker extends StandardSystemRefreshWindow {

    static final int CHECK_EVERY = 1000 * 60 * 15;
    static SystemSyncChecker _theWindow;

    public SystemSyncChecker() {
        this((CatchupMathVersion)null);
    }
    
    public SystemSyncChecker(CatchupMathVersion version) {
        super("Catchup Math Update", 
                "We have recently updated Catchup Math.  " + 
                "Please Refresh your browser by pressing the F5 key or click the button below. " +
                "Thank you for using Catchup Math!");

        if(CmShared.getQueryParameter("debug") != null) {
            if(version != null) {
                add(new Html("GetCatchupMathVersionAction: " + version.getVersion() + " current: " + CatchupMathVersionInfo.getBuildVersion()));
            }
        }
            
        /** only show one */
        if(_theWindow != null)
            return;
        
        setVisible(true);
    }
    
    public SystemSyncChecker(String title, String msg, Button btn) {
        super(title, msg, btn);
        _theWindow = this;
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
    }
    
    
    /** Call server and look for new messages.
     * 
     * Only call server if window is not currently being displayed.
     */
    static public void checkForUpdate() {
        
        if(_theWindow != null)
            return;
        
        /** handle as separate request to keep errors silent in case of temp offline
         * 
         */
        UserInfo ui=UserInfo.getInstance();
         GetUserSyncAction action = new GetUserSyncAction(UserInfoBase.getInstance().getUid());
         CmShared.getCmService().execute(action, new AsyncCallback<UserSyncInfo>() {
             @Override
            public void onSuccess(UserSyncInfo info) {
                 CatchupMathVersion version = info.getVersionInfo();
                 CmLogger.debug("GetCatchupMathVersionAction: " + version.getVersion() + " current: " + CatchupMathVersionInfo.getBuildVersion());
                 if(version.getVersion() != CatchupMathVersionInfo.getBuildVersion()) {
                     new SystemSyncChecker(version);
                 }
                 else if(!UserInfoBase.getInstance().getCmStartType().equals("ADMIN") && CmShared.getQueryParameter("debug") == null) {
                	 /** only for CM Student not in debug mode */
                	 if(info.getCurrentUserLoginKey() != null && !info.getCurrentUserLoginKey().equals(CmShared.getSecurityKey())) {
	                     new SystemSyncChecker("Auto Log Out", "You have been automatically logged out due to multiple logins. Please log back in to continue.",
	                             
	                             new Button("Logout", new SelectionListener<ButtonEvent>() {
	                                 @Override
	                                 public void componentSelected(ButtonEvent ce) {
	                                     Window.Location.assign("/login.html");
	                                 }
	                             }));
                	 }
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
