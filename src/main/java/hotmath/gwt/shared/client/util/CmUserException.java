package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_rpc.server.rpc.CmExceptionDoNotNotify;


/** Represents any type of user error
 *    - duplicate passcode
 *    - duplicate name
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
