package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.model.UserInfoBase;

import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

/** Class to encapsulate all user information
 * 
 * @author Casey
 *
 */
public class UserInfo implements IsSerializable {

	static private UserInfo __instance;

	/** This is only active for one session, meaning there will only be one
	 *  userid... The state of that user will change: diff topic,resource,etc.
	 *  
	 *
	 *  It is an error is getInstance is called before being set.
	 *   
	 * @return
	 */
	static public UserInfo getInstance() {
		return __instance;
	}
	
	/** Set the shared user object
	 * 
	 * @param user
	 */
	static public void setInstance(UserInfo user) {
	    __instance = user;
	}
	
	static public void setInstanceBase(UserInfoBase user) {
		
	}
	/** Lookup this user and callback when complete 
	 * 
	 * @param uid
	 * @param callback
	 * @return
	 */
	static public void loadUser(int uid, final CmAsyncRequest callback) {
	    
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getUserInfo(uid, new AsyncCallback() {
            public void onSuccess(Object result) {
                RpcData ui = (RpcData)result;
                
                UserInfo user = new UserInfo(ui.getDataAsInt("uid"),ui.getDataAsInt("test_id"), ui.getDataAsInt("run_id"));
                user.setTestSegment(ui.getDataAsInt("test_segment"));
                user.setUserName(ui.getDataAsString("user_name"));
                user.setViewCount(ui.getDataAsInt("view_count"));
                user.setSessionNumber(ui.getDataAsInt("session_number"));
                user.setActiveUser(true);
                user.setBackgroundStyle(ui.getDataAsString("gui_background_style"));
                user.setTestName(ui.getDataAsString("test_name"));
                __instance = user;
                
                CatchupMathTools.setBusy(false);     
                callback.requestComplete(null);
                
                // fire an event on the event bus, passing new userinfo
                EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_USERCHANGED,user));
            }
            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathTools.showAlert(msg);
            }
        });
	}
	
	int uid;
	int testId;
	// int runId=563;
	int runId;
	int sessionNumber=0;
	int testSegment=1;
	int testSegmentCount=4;
	String testName;
	String userName;
	int viewCount;
	int correctPercent;
	String backgroundStyle;
	int correctAnswers;
	boolean isShowWorkRequired=true;



    public boolean isShowWorkRequired() {
        return isShowWorkRequired;
    }

    public void setShowWorkRequired(boolean isShowWorkRequired) {
        this.isShowWorkRequired = isShowWorkRequired;
    }

    public String getBackgroundStyle() {
        return backgroundStyle;
    }

    public void setBackgroundStyle(String backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
    }

    public int getCorrectPercent() {
        return correctPercent;
    }

    public void setCorrectPercent(int correctPercent) {
        this.correctPercent = correctPercent;
    }

    // is this user active?  If not
	// the state info should not be updated
	boolean activeUser;
	

	/** Is this the active (owner) of current 
	 * session, or is this a guest/viewer/admin?
	 * 
	 * @return
	 */
	public boolean isActiveUser() {
        return activeUser;
    }

	/** Mark this user as the owner of the thread
	 * 
	 * @param activeUser
	 */
    public void setActiveUser(boolean activeUser) {
        this.activeUser = activeUser;
    }

    public int getViewCount() {
        return viewCount;
    }


    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public int getTestSegmentCount() {
		return testSegmentCount;
	}

	public void setTestSegmentCount(int testSegmentCount) {
		this.testSegmentCount = testSegmentCount;
	}

	public UserInfo(int uid, int testId) {
		this.uid = uid;
		this.testId = testId;
	}
	
	public UserInfo(int uid, int testId, int runId) {
		this(uid, testId);
		this.runId = runId;
	}
	
	public int getTestSegment() {
		return testSegment;
	}

	public void setTestSegment(int testSegment) {
		this.testSegment = testSegment;
	}	
	
	public int getSessionNumber() {
		return sessionNumber;
	}

	public void setSessionNumber(int sessionNumber) {
		this.sessionNumber = sessionNumber;
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}

	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getTestId() {
		return testId;
	}
	public void setTestId(int testId) {
		this.testId = testId;
	}
	
	
	String DEMO_USER_NAME="Student";
	/** Return true if this is a demo user
	 * 
	 * @return
	 */
	public boolean isDemoUser() {
	    return(this.getUserName().equals(DEMO_USER_NAME));
	}
	
	@Override
	public String toString() {
	    return "User: " + userName +
	           ", Uid: " + uid +
	           ", Program: " + testName + 
	           ", Segment: " + getTestSegment() + 
	           ", TestId: " + getTestId() + 
	           ", RunId: " + getRunId();
	}

}
