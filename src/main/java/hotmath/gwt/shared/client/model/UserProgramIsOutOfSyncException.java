package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.CmExceptionDoNotNotify;
import hotmath.gwt.shared.client.util.CmException;

/** Thrown when a user's client representation
 *  is out of sync with the actual server side
 *  representation of the program.
 *  
 * @author casey
 *
 */
public class UserProgramIsOutOfSyncException extends CmException implements CmExceptionDoNotNotify{
    
    public UserProgramIsOutOfSyncException() {
        super();
    }
}
