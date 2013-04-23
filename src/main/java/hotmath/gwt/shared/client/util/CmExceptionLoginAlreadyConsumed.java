package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_rpc_core.client.CmUserException;


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
