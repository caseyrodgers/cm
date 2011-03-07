package hotmath.gwt.shared.client.util;


/** Thrown when a login is attempted with an already verified login key
 *  
 * @author casey
 *
 */
public class CmExceptionLoginAlreadyConsumed extends CmUserException {
    
    public CmExceptionLoginAlreadyConsumed(String msg) {
        super(msg);
    }

}
