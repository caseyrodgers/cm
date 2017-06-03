package hotmath.gwt.cm_tools.client.data;

import java.util.Date;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** Represents the basic user information for
 *  signing into catchup math.
 *  
 * @author casey
 *
 */
public interface HaBasicUser extends Response {
    
    public static enum UserType { STUDENT, ADMIN, AUTO_CREATE, PARALLEL_PROGRAM, AUTO_REG_SELF_PAY, ERROR, STUDENT_MOBILE };
    
    String getUserName();
    String getPassword();
    String getLoginName();
    String getEmail();
    String getPartner();
    
    String getLoginMessage();
    void setLoginMessage(String msg);
    
    Date getExpireDate();
    void setExpireDate(Date expireDate);
    boolean isExpired();
    
    void setAccountType(String accountType);
    String getAccountType();
    
    // either the uid, or the aid depending on type
    int getUserKey();
    
    // hack to return the proper type
    UserType getUserType();
	void setUserType(UserType userType);
}
