package hotmath.gwt.cm_rpc.client;

import hotmath.gwt.cm_rpc.client.rpc.Response;


/** Class to encapsulate all user information
 * 
 * @author Casey
 *
 */
public class UserInfo implements  Response {
	
	private static final long serialVersionUID = -3704985207550273686L;
	
	int uid;
	int testId;
	int runId;
	int sessionNumber=0;
	int testSegment=1;
	int programSegmentCount=4;
	int testSegmentSlot;
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
	boolean limitGames;

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

    /** return the background style, if null then 
     * return default.
     * 
     * @return
     */
    public String getBackgroundStyle() {
        if(backgroundStyle != null && backgroundStyle.length() > 0) {
            return backgroundStyle;
        }
        else {
            return "resource-container-default";
        }
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
        if(userName != null && userName.toLowerCase().contains("student:")) {
            return "Demo Student";
        }
        else {
           return userName;
        }
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTestName() {
		return testName;
	}

	public void setProgramName(String testName) {
		this.testName = testName;
	}

	public int getProgramSegmentCount() {
		return programSegmentCount;
	}

	public void setProgramSegmentCount(int programSegmentCount) {
		this.programSegmentCount = programSegmentCount;
	}

	public int getTestSegment() {
		return testSegment;
	}

	public void setProgramSegment(int testSegment) {
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
       String status = "uid: " + uid + ", test_id: " + getTestId() + ", alt_test: " + getTestSegmentSlot() + 
                       ", run_id: " + getRunId() + ", quiz: " + getTestSegment() + "/" + getProgramSegmentCount() + 
                       ", prescription: " + getSessionNumber() + "/" + getSessionCount();
       return status;
    }
    

    
    public boolean isLimitGames() {
        return limitGames;
    }

    public void setLimitGames(boolean limitGames) {
        this.limitGames = limitGames;
    }

    UserProgramCompletionAction onCompletion= UserProgramCompletionAction.AUTO_ADVANCE;


    /** Return action indicating what to do once the active
     *  program has been completed.
     *  
     * @return
     */
    public UserProgramCompletionAction getOnCompletion() {
        return onCompletion;
    }

    /** What should happen on program completion
     * 
     * @param onCompletion
     */
    public void setOnCompletion(UserProgramCompletionAction onCompletion) {
        this.onCompletion = onCompletion;
    }
    
    

    public int getTestSegmentSlot() {
        return testSegmentSlot;
    }

    public void setTestSegmentSlot(int testSegmentSlot) {
        this.testSegmentSlot = testSegmentSlot;
    }

    @Override
    public String toString() {
        return "UserInfo [uid=" + uid + ", testId=" + testId + ", runId=" + runId + ", sessionNumber=" + sessionNumber
                + ", testSegment=" + testSegment + ", programSegmentCount=" + programSegmentCount
                + ", testSegmentSlot=" + testSegmentSlot + ", testName=" + testName + ", userName=" + userName
                + ", viewCount=" + viewCount + ", correctPercent=" + correctPercent + ", backgroundStyle="
                + backgroundStyle + ", correctAnswers=" + correctAnswers + ", isShowWorkRequired=" + isShowWorkRequired
                + ", isTutoringAvail=" + isTutoringAvail + ", isFirstView=" + isFirstView + ", isDemoUser="
                + isDemoUser + ", sessionCount=" + sessionCount + ", password=" + password + ", loginName=" + loginName
                + ", customProgram=" + customProgram + ", limitGames=" + limitGames + ", userAccountType="
                + userAccountType + ", passPercentRequired=" + passPercentRequired + ", subTitle=" + subTitle
                + ", autoTestMode=" + autoTestMode + ", activeUser=" + activeUser + ", DEMO_USER_NAME="
                + DEMO_USER_NAME + ", onCompletion=" + onCompletion + "]";
    }
    
    

    static private UserInfo __instance;

    /** The CM Student user object
     *  (will be null for CM Admin)
     *  
     *  This is only active for one session, meaning there will only be one
     *  userid... The state of that user will change: diff topic,resource,etc.
     *  
     *
     *  It is an error if getInstance is called before being set.
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
    public enum UserProgramCompletionAction {
        /** Stop when end of problem and 
         *  do not allow user to continue
         */
        STOP,
        
        /** The user should be auto advanced when completed
         * 
         */
        AUTO_ADVANCE
    }
    
    
    
}
