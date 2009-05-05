package hotmath.testset.ha;

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
    
    
    // hack to return the proper type
    UserType getUserType();
    Object getUserObject();
}
