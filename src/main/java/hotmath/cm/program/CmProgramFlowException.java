package hotmath.cm.program;

import hotmath.gwt.shared.client.util.CmException;

/** Thrown when a request to change a CM Flow is 
 * considered invalid.
 *  
 * @author casey
 *
 */
public class CmProgramFlowException extends CmException {
    
    public CmProgramFlowException(String msg, Exception th) {
        super(msg, th);
    }
    
    public CmProgramFlowException(String msg) {
        super(msg);
    }
}
