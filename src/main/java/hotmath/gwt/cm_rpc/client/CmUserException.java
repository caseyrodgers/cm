package hotmath.gwt.cm_rpc.client;

import hotmath.gwt.shared.client.util.CmException;



/** Represents any type of user error
 *    - duplicate passcode
 *    - duplicate name
 *    - duplicate group
 *    - ...
 * 
 * @author casey
 *
 */
public class CmUserException extends CmException implements CmExceptionDoNotNotify{
    
	private static final long serialVersionUID = 6319270152152288015L;

	public CmUserException(String msg) {
        super(msg);
    }
    
    public CmUserException(Exception e) {
        super(e);
    }
}
