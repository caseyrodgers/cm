package hotmath.testset.ha;

/** Thrown when a login is attempted with an already verified login key
 *  
 * @author casey
 *
 */
public class CmExceptionLoginAlreadyConsumed extends Exception {
    
    public CmExceptionLoginAlreadyConsumed(String msg) {
        super(msg);
    }

}
