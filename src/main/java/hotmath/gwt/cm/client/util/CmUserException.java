package hotmath.gwt.cm.client.util;

/** Represents any type of user login error
 * 
 * @author casey
 *
 */
public class CmUserException extends CmException {
    
    public CmUserException(String msg) {
        super(msg);
    }
    
    public CmUserException(Exception e) {
        super(e);
    }
}
