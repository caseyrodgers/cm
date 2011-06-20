package hotmath.gwt.shared.client.util;

/** Represents when a program is current in use either
 *  directly or in detail history.
 *  
 * @author casey
 *
 */
public class CmExceptionProgramBusy extends CmException {

    public CmExceptionProgramBusy() {
        super();
    }
    
    public CmExceptionProgramBusy(String e) {
        super(e);
    }

}
