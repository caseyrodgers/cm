package hotmath.gwt.shared.client.util;


import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CatchupMathVersionInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.event.AssignmentsUpdatedEvent;
import hotmath.gwt.shared.client.event.ForceSystemSyncCheckEvent;
import hotmath.gwt.shared.client.event.ForceSystemSyncCheckHandler;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.action.GetUserSyncAction;
import hotmath.gwt.shared.client.rpc.result.CatchupMathVersion;
import hotmath.gwt.shared.client.rpc.result.UserSyncInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

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
                add(new HTML("GetCatchupMathVersionAction: " + version.getVersion() + " current: " + CatchupMathVersionInfo.getBuildVersion()));
            }
        }
            
        /** only show one */
        if(_theWindow != null)
            return;
        
        setVisible(true);
    }
    
    public SystemSyncChecker(String title, String msg, TextButton btn) {
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
        
        
        /** do it first */
        checkForUpdate();
    }
    
    
    /** Call server and look for new messages.
     * 
     * Only call server if window is not currently being displayed.
     */
    static public void checkForUpdate() {
        
        if(_theWindow != null)
            return;
        
        /** handle as separate request 
         * to keep errors silent in case of temp offline
         * 
         */
        UserInfo ui=UserInfo.getInstance();
         GetUserSyncAction action = new GetUserSyncAction(UserInfoBase.getInstance().getUid());
         CmShared.getCmService().execute(action, new AsyncCallback<UserSyncInfo>() {
             @Override
            public void onSuccess(UserSyncInfo info) {
                 CatchupMathVersion version = info.getVersionInfo();
                 
     	    	String startType = UserInfoBase.getInstance().getCmStartType();
    	    	if(startType == null)startType = "";

    	    	CmLogger.debug("GetCatchupMathVersionAction: " + version.getVersion() + " current: " + CatchupMathVersionInfo.getBuildVersion());
                 if(version.getVersion() != CatchupMathVersionInfo.getBuildVersion()) {
                     new SystemSyncChecker(version);
                 }
                 else if(!"ADMIN".equals(startType) && !"AUTO_CREATE".equals(startType) && CmShared.getQueryParameter("debug") == null) {
                	 /** only for CM Student not in debug mode */
                	 if(info.getCurrentUserLoginKey() != null && !info.getCurrentUserLoginKey().equals(CmShared.getSecurityKey())) {
	                     new SystemSyncChecker("Auto Log Out", "You have been automatically logged out due to multiple logins. Please log back in to continue.",
	                             
	                             new TextButton("Logout", new SelectHandler() {
	                                 @Override
	                                public void onSelect(SelectEvent event) {
	                                     Window.Location.assign("/login.html");
	                                 }
	                             }));
                	 }
                 }
                 if(info.getAssignmentInfo().isAdminUsingAssignments()) {
                     fireAppropriateEvent(info.getAssignmentInfo());
                 }
            }
             @Override
            public void onFailure(Throwable arg0) {
                 /** fail silent */
                 CatchupMathTools.showAlert(arg0.getMessage());
            }
        });
    }
    

    /** Fire update event if things are actually different
     * 
     * @param assignmentInfo
     */
    static AssignmentUserInfo _lastCheckedData;
    protected static void fireAppropriateEvent(AssignmentUserInfo assignmentInfo) {
        if(_lastCheckedData == null || !_lastCheckedData.equals(assignmentInfo)) {
            _lastCheckedData = assignmentInfo;
            
            Log.debug("Firing AssignmentsUpdatedEvent: " + assignmentInfo);
            CmRpcCore.EVENT_BUS.fireEvent(new AssignmentsUpdatedEvent(assignmentInfo));
        }
    }


    static {
        CmRpcCore.EVENT_BUS.addHandler(ForceSystemSyncCheckEvent.TYPE, new ForceSystemSyncCheckHandler() {
            public void forceSyncCheck() {
                checkForUpdate();
            }
        });
    }
}
