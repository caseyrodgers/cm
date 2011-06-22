package hotmath.gwt.cm_rpc.client;

import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;



/** Represents any type of user error
 *    - duplicate passcode
 *    - duplicate name
 *    - duplicate group
 *    - ...
 * 
 * @author casey
 *
 */
public class CmUserException extends CmRpcException implements CmExceptionDoNotNotify{
    
	private static final long serialVersionUID = 6319270152152288015L;

	public CmUserException(String msg) {
        super(msg);
    }
    
    public CmUserException(Exception e) {
        super(e);
    }
}
