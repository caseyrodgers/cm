package hotmath.gwt.cm_core.client;

import hotmath.gwt.cm_core.client.model.CmPartner;

import com.google.gwt.user.client.rpc.IsSerializable;

/** Class to encapsulate basic user information
 * 
 * @author Bob
 *
 */
public class UserInfoBase implements IsSerializable {

    public enum Mode{TEACHER_MODE, STUDENT_MODE};
	static protected UserInfoBase __instance;

	int uid;
	int runId;
	boolean isAdmin;
	String cmStartType;
	String email;
	CmPartner partner;

	String userName;

    // is this user the owner or a viewer (guest/admin) ?
	// If not the owner, then state info should not be updated
	boolean isOwner;

	// is the user active (true) or inactive (false)
	boolean isActive;
	
	Mode mode = Mode.STUDENT_MODE;

    private boolean mobile;

	static public UserInfoBase getInstance() {
	    if (__instance == null) {
	    	__instance = new UserInfoBase();
	    }
	    return __instance;
	}
	

    /** Set is this is a mobile browser
     * 
     * @param b
     */
    public void setMobile(boolean b) {
        mobile = b;
    }
    
    /** Is this a mobile browser
     * 
     * @return
     */
    public boolean isMobile() {
        return mobile;
    }

	
	public String getCmStartType() {
		return cmStartType;
	}

	public void setCmStartType(String cmStartType) {
		this.cmStartType = cmStartType;
	}

	public UserInfoBase() {
	}
		
	/** Is this the active (owner) of current 
	 * session, or is this a guest/viewer/admin?
	 * 
	 * @return
	 */
	public boolean isActiveUser() {
        return isOwner;
    }

    public void setActiveUser(boolean activeUser) {
        this.isOwner = activeUser;
    }

    
    public void setMode(Mode mode) {
        this.mode = mode;
    }
    
    public Mode getMode() {
        return mode;
    }
    
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}


	public boolean isAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	

	public CmPartner getPartner() {
		return partner;
	}

	public void setPartner(CmPartner partner) {
		this.partner = partner;
	}
}