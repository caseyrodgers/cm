package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetUserInfoAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.IsSerializable;

/** Class to encapsulate all user information
 * 
 * @author Casey
 *
 */
public class UserInfo implements IsSerializable, Response {
	
	private static final long serialVersionUID = -3704985207550273686L;
	
	int uid;
	int testId;
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
	boolean isShowWorkRequired;
	boolean isTutoringAvail;
	boolean isFirstView=true;
	boolean isDemoUser;
	Integer sessionCount;
	String password;
	String loginName;
	boolean customProgram;
	ProgramCompletionAction onCompletion= ProgramCompletionAction.AUTO_ADVANCE;

    public UserInfo() {}
    
    public UserInfo(int uid, int testId) {
        this.uid = uid;
        this.testId = testId;
    }
    
    public UserInfo(int uid, int testId, int runId) {
        this(uid, testId);
        this.runId = runId;
    }
    

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    
	public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    public boolean isFirstView() {
        return isFirstView;
    }

    public void setFirstView(boolean isFirstView) {
        this.isFirstView = isFirstView;
    }

    public boolean isTutoringAvail() {
        return isTutoringAvail;
    }

    public void setTutoringAvail(boolean isTutoringAvail) {
        this.isTutoringAvail = isTutoringAvail;
    }

    UserType userAccountType = UserType.SINGLE_USER;
	int passPercentRequired;
	String subTitle;
	

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    boolean autoTestMode=false;

	public boolean isAutoTestMode() {
        return autoTestMode;
    }

    public void setAutoTestMode(boolean autoTestMode) {
        this.autoTestMode = autoTestMode;
    }

    
    /** Define the type of user, either a student
	 *  at a single school, or a single user purchase.
	 */
	public static enum UserType {SCHOOL_USER, SINGLE_USER};


	/** Set the type of user, either single or student
	 * 
	 * @return
	 */
    public UserType getUserAccountType() {
        return userAccountType;
    }

    public void setUserAccountType(UserType userType) {
        this.userAccountType = userType;
    }

    /** Set the UserType depending on the subscriber type (ST, PS)
     * 
     * @param userType
     */
    public void setUserAccountType(AccountType accountType) {
        if(accountType.getTag().equals("ST"))
            this.userAccountType = UserType.SCHOOL_USER;
        else 
            this.userAccountType = UserType.SINGLE_USER;
    }
    /** Helper method to return true if this is
     *  user is currently setup as single user.
     *  
     * @return True if is single user
     */
    public boolean isSingleUser() {
        return userAccountType != null && userAccountType.equals(UserType.SINGLE_USER)?true:false;
    }
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

    /** The total number of resources (solutions) viewed
     * 
     * @return
     */
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

    public boolean isDemoUser() {
	    return(this.isDemoUser);
	}
   
	public void setDemoUser(boolean isDemoUser) {
		this.isDemoUser = isDemoUser;
	}

    public boolean isCustomProgram() {
        return customProgram;
    }

    public void setCustomProgram(boolean customProgram) {
        this.customProgram = customProgram;
    }

    public int getPassPercentRequired() {
        return passPercentRequired;
    }

    public void setPassPercentRequired(int passPercentToMoveOn) {
        this.passPercentRequired = passPercentToMoveOn;
    }
    
    /** Return string indicating current user status in program
     * 
     * @return
     */
    public String getUserStatus() {
       String status = "uid: " + uid + ", test_id: " + getTestId() + ", run_id: " + getRunId() + ", quiz: " + getTestSegment() + "/" + getTestSegmentCount() + ", prescription: " + getSessionNumber() + "/" + getSessionCount();
       return status;
    }
    
    

    public ProgramCompletionAction getOnCompletion() {
        return onCompletion;
    }

    public void setOnCompletion(ProgramCompletionAction onCompletion) {
        this.onCompletion = onCompletion;
    }

    @Override
    public String toString() {
        return "UserInfo [DEMO_USER_NAME=" + DEMO_USER_NAME + ", activeUser=" + activeUser + ", autoTestMode="
                + autoTestMode + ", backgroundStyle=" + backgroundStyle + ", correctAnswers=" + correctAnswers
                + ", correctPercent=" + correctPercent + ", customProgram=" + customProgram + ", isDemoUser="
                + isDemoUser + ", isFirstView=" + isFirstView + ", isShowWorkRequired=" + isShowWorkRequired
                + ", isTutoringAvail=" + isTutoringAvail + ", loginName=" + loginName + ", onCompletion="
                + onCompletion + ", passPercentRequired=" + passPercentRequired + ", password=" + password + ", runId="
                + runId + ", sessionCount=" + sessionCount + ", sessionNumber=" + sessionNumber + ", subTitle="
                + subTitle + ", testId=" + testId + ", testName=" + testName + ", testSegment=" + testSegment
                + ", testSegmentCount=" + testSegmentCount + ", uid=" + uid + ", userAccountType=" + userAccountType
                + ", userName=" + userName + ", viewCount=" + viewCount + "]";
    }
    
    

    static private UserInfo __instance;

    /** The CM Student user object
     *  (will be null for CM Admin)
     *  
     *  This is only active for one session, meaning there will only be one
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
    /** Lookup this user and callback when complete.
     * 
     *  The UserInfo has been set before callback.
     * 
     * @param uid
     * @param callback
     * @return
     */
    static public void loadUser(final int uid, final CmAsyncRequest callback) {
        new RetryAction<UserInfo>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetUserInfoAction action = new GetUserInfoAction(uid);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            @Override
            public void oncapture(UserInfo user) {

                __instance = user;
                
                // if run_id passed in, then allow user to view_only
                if(CmShared.getQueryParameter("run_id") != null) {
                    int runId = Integer.parseInt(CmShared.getQueryParameter("run_id"));
                    // setup user to masquerade as real user
                    user.setRunId(runId);
                    user.setActiveUser(false);
                }
                else {
                    user.setActiveUser(true);
                }
                
                Log.info("UserInfo object set to: " + user);
                
                CmBusyManager.setBusy(false);     
                callback.requestComplete(null);

                // fire an event on the event bus, passing new userinfo
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USERCHANGED,user));
            }
        }.register();
    }    

    
    
    
    public enum AccountType{
        SCHOOL_TEACHER("ST"),PARENT_STUDENT("PS"),OTHER("O");
    
        String tag;
        private AccountType(String tag) {
            this.tag = tag;
        }
        
        public String getTag() {
            return this.tag;
        }
    };

    /** Define what should happen when a program is completed 
     * 
     * @author casey
     *
     */
    public enum ProgramCompletionAction {
        /** Stop when program is completed
         * 
         */
        STOP,
        
        /** The user should be auto advanced when completed
         * 
         */
        AUTO_ADVANCE
    }
}
