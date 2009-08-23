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
    
    Date getExpireDate();
    void setExpireDate(Date expireDate);
    boolean isExpired();
    
    // either the uid, or the aid depending on type
    int getUserKey();
    
    // hack to return the proper type
    UserType getUserType();
}
