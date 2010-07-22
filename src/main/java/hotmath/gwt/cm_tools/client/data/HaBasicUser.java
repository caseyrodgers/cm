package hotmath.gwt.cm_tools.client.data;

import java.util.Date;

/** Represents the basic user information for
 *  signing into catchup math.
 *  
 * @author casey
 *
 */
public interface HaBasicUser {
    
    public static enum UserType { STUDENT, ADMIN, AUTO_CREATE };
    
    String getUserName();
    String getPassword();
    String getLoginName();
    String getEmail();
    
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
