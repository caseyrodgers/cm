package hotmath.gwt.cm_tools.client.data;

/** Represents the basic user information for
 *  signing into catchup math.
 *  
 * @author casey
 *
 */
public interface HaBasicUser {
    
    public static enum UserType { STUDENT, ADMIN };
    
    String getUserName();
    String getPassword();
    
    // either the uid, or the aid depending on type
    int getUserKey();
    
    // hack to return the proper type
    UserType getUserType();
}
