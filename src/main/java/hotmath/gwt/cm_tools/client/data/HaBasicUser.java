package hotmath.gwt.cm_tools.client.data;

import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.Date;

/** Represents the basic user information for
 *  signing into catchup math.
 *  
 * @author casey
 *
 */
public interface HaBasicUser extends Response {
    
    public static enum UserType { STUDENT, ADMIN, AUTO_CREATE, ERROR };
    
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
}
